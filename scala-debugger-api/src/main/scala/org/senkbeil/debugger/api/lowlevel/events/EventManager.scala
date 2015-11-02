package org.senkbeil.debugger.api.lowlevel.events

import org.senkbeil.debugger.api.lowlevel.events.data.JDIEventDataResult
import org.senkbeil.debugger.api.lowlevel.utils.JDIHelperMethods
import org.senkbeil.debugger.api.pipelines.Pipeline.IdentityPipeline
import org.senkbeil.debugger.api.pipelines.{CloseablePipeline, Pipeline}
import org.senkbeil.debugger.api.utils.{LoopingTaskRunner, Logging}
import com.sun.jdi.VirtualMachine
import com.sun.jdi.event.{EventSet, Event}

import java.util.concurrent.ConcurrentHashMap

import EventType._
import scala.collection.JavaConverters._

/**
 * Represents a manager for events coming in from a virtual machine.
 *
 * @param _virtualMachine The virtual machine whose events to manage
 * @param loopingTaskRunner The runner used to process events
 * @param autoStart If true, starts the event processing automatically
 * @param startTaskRunner If true, will attempt to start the task runner if
 *                        not already started (upon starting the event manager)
 * @param onExceptionResume If true, any event handler that throws an exception
 *                          will count towards resuming the event set, otherwise
 *                          it will cause the event set to not resume
 */
class EventManager(
  protected val _virtualMachine: VirtualMachine,
  private val loopingTaskRunner: LoopingTaskRunner,
  private val autoStart: Boolean = true,
  private val startTaskRunner: Boolean = false,
  private val onExceptionResume: Boolean = true
) extends JDIHelperMethods with Logging {
  /**
   * Represents an event callback, receiving the event and returning whether or
   * not to resume.
   */
  type EventHandler = (Event, Seq[JDIEventDataResult]) => Boolean

  /**
   * Represents a JDI event and any associated data retrieved from it.
   */
  type EventAndData = (Event, Seq[JDIEventDataResult])

  /** Represents the event id associated with the event handler. */
  type EventHandlerId = String

  /** Contains the events, associated handlers and their ids. */
  private val eventTypeToHandlerIds =
    new ConcurrentHashMap[EventType, ConcurrentHashMap[EventHandlerId, EventHandler]]()

  private var eventTaskId: Option[String] = None

  /**
   * Indicates whether or not the event manager is processing events.
   *
   * @return True if it is running, otherwise false
   */
  def isRunning: Boolean = eventTaskId.nonEmpty

  /**
   * Begins the processing of events from the virtual machine.
   */
  def start(): Unit = {
    assert(!isRunning, "Event manager already started!")

    if (startTaskRunner && !loopingTaskRunner.isRunning) {
      logger.debug("Event manager starting looping task runner!")
      loopingTaskRunner.start()
    }

    logger.trace("Starting event manager for virtual machine!")
    eventTaskId = Some(loopingTaskRunner.addTask(eventHandlerTask()))

    eventTaskId.foreach(id =>
      logger.trace(s"Event process task: $id"))
  }

  /**
   * Represents the task to be added per event handler. Can be overridden to
   * perform different tasks.
   */
  protected def eventHandlerTask(): Unit = {
    val eventQueue = _virtualMachine.eventQueue()
    val eventSet = eventQueue.remove()

    // Process the set of events, returning whether or not the event
    // set should resume
    val eventSetProcessor = newEventSetProcessor(eventSet)
    eventSetProcessor.process()
  }

  /**
   * Creates a new event set processor. Can be overridden.
   *
   * @param eventSet The event set to process
   *
   * @return The new event set processor instance
   */
  protected def newEventSetProcessor(
    eventSet: EventSet
  ): EventSetProcessor = new EventSetProcessor(
    eventSet                = eventSet,
    eventFunctionRetrieval  = getHandlersForEventType,
    onExceptionResume       = onExceptionResume
  )

  /**
   * Ends the processing of events from the virtual machine.
   */
  def stop(): Unit = {
    assert(isRunning, "Event manager not started!")

    logger.trace(s"Stopping event manager ($eventTaskId) for virtual machine!")
    loopingTaskRunner.removeTask(eventTaskId.get)
    eventTaskId = None
  }

  /**
   * Adds a new event stream that tracks incoming JDI events.
   *
   * @param eventType The type of JDI event to stream
   * @param eventArguments The arguments used when determining whether or not
   *                       to send the event down the stream
   *
   * @return The resulting event stream in the form of a pipeline of events
   */
  def addEventStream(
    eventType: EventType,
    eventArguments: JDIEventArgument*
  ): IdentityPipeline[Event] = {
    addEventDataStream(eventType, eventArguments: _*).map(_._1).noop()
  }

  /**
   * Adds a new event data stream that tracks incoming JDI events and data
   * collected from those events.
   *
   * @param eventType The type of JDI event to stream
   * @param eventArguments The arguments used when determining whether or not
   *                       to send the event down the stream
   *
   * @return The resulting event stream in the form of a pipeline of events
   *         and collected data
   */
  def addEventDataStream(
    eventType: EventType,
    eventArguments: JDIEventArgument*
  ): IdentityPipeline[EventAndData] = {
    val eventHandlerId = newEventHandlerId()

    val eventPipeline = CloseablePipeline.newPipeline(
      classOf[EventAndData],
      () => removeEventHandler(eventHandlerId)
    )

    // Create a resuming event handler while providing our own id
    wrapAndAddEventHandler(
      eventHandlerId,
      eventType,
      (e, d) => { eventPipeline.process((e, d)); true },
      eventArguments: _*
    )

    eventPipeline
  }

  /**
   * Adds the event function to this manager. This event automatically counts
   * towards resuming the event set after completion.
   *
   * @param eventType The type of the event to add a function
   * @param eventHandler The function to add, taking the occurring event and
   *                     a collection of retrieved data from the event
   * @param eventArguments The arguments used when determining whether or not to
   *                       invoke the event handler
   *
   * @return The id associated with the event handler
   */
  def addResumingEventHandler(
    eventType: EventType,
    eventHandler: (Event, Seq[JDIEventDataResult]) => Unit,
    eventArguments: JDIEventArgument*
  ): EventHandlerId = {
    // Convert the function to an "always true" event function
    val fullEventFunction = (event: Event, data: Seq[JDIEventDataResult]) => {
      eventHandler(event, data)
      true
    }

    addEventHandler(eventType, fullEventFunction, eventArguments: _*)
  }

  /**
   * Adds the event function to this manager. This event automatically counts
   * towards resuming the event set after completion.
   *
   * @param eventType The type of the event to add a function
   * @param eventHandler The function to add, taking the occurring event
   * @param eventArguments The arguments used when determining whether or not to
   *                       invoke the event handler
   *
   * @return The id associated with the event handler
   */
  def addResumingEventHandler(
    eventType: EventType,
    eventHandler: (Event) => Unit,
    eventArguments: JDIEventArgument*
  ): EventHandlerId = addResumingEventHandler(
    eventType = eventType,
    eventHandler = (event: Event, _: Seq[JDIEventDataResult]) => {
      eventHandler(event)
    },
    eventArguments: _*
  )

  /**
   * Adds the event function to this manager. The return value of the handler
   * function contributes towards whether or not to resume the event set.
   *
   * @param eventType The type of the event to add a function
   * @param eventHandler The function to add, taking the occurring event and
   *                     a collection of retrieved data from the event
   * @param eventArguments The arguments used when determining whether or not to
   *                       invoke the event handler
   *
   * @return The id associated with the event handler
   */
  def addEventHandler(
    eventType: EventType,
    eventHandler: EventHandler,
    eventArguments: JDIEventArgument*
  ): EventHandlerId = {
    // Generate the id for this handler
    val eventHandlerId = newEventHandlerId()

    wrapAndAddEventHandler(
      eventHandlerId,
      eventType,
      eventHandler,
      eventArguments: _*
    )
  }

  /**
   * Adds the event function to this manager. The return value of the handler
   * function contributes towards whether or not to resume the event set.
   *
   * @param eventType The type of the event to add a function
   * @param eventHandler The function to add, taking the occurring event
   * @param eventArguments The arguments used when determining whether or not to
   *                       invoke the event handler
   *
   * @return The id associated with the event handler
   */
  def addEventHandler(
    eventType: EventType,
    eventHandler: (Event) => Boolean,
    eventArguments: JDIEventArgument*
  ): EventHandlerId = addEventHandler(
    eventType = eventType,
    eventHandler = (event: Event, _: Seq[JDIEventDataResult]) => {
      eventHandler(event)
    },
    eventArguments: _*
  )

  /**
   * Wraps the provided event handler and adds it to the internal collection.
   *
   * @param eventHandlerId The id to associate with the event handler
   * @param eventType The type of the event to match against the handler
   * @param eventHandler The event handler function to be wrapped
   * @param eventArguments The arguments used when determining whether or not to
   *                       invoke the event handler
   *
   * @return The id associated with the wrapped event handler
   */
  protected def wrapAndAddEventHandler(
    eventHandlerId: EventHandlerId,
    eventType: EventType,
    eventHandler: EventHandler,
    eventArguments: JDIEventArgument*
    ): EventHandlerId = {
    // Retrieve the existing id -> handler map or create one
    val eventHandlerMap =
      if (eventTypeToHandlerIds.containsKey(eventType)) {
        eventTypeToHandlerIds.get(eventType)
      } else {
        val _newMap = new ConcurrentHashMap[EventHandlerId, EventHandler]()
        eventTypeToHandlerIds.put(eventType, _newMap)
        _newMap
      }

    // Create a wrapper that contains our filtering logic
    val wrapperEventHandler =
      newWrapperEventHandler(eventHandler, eventArguments)

    // Store the event handler with the filtering logic
    eventHandlerMap.put(eventHandlerId, wrapperEventHandler)

    eventHandlerId
  }

  /**
   * Generates a wrapper function around the event handler, using an argument
   * processor to evaluate the provided arguments to determine whether or not
   * to invoke the event handler as well as retrieve any requested data.
   *
   * @param eventHandler The event handler to wrap
   * @param eventArguments The arguments to use when determining if the event
   *                       handler should be invoked and what data to be
   *                       retrieved
   *
   * @return The wrapper around the event handler
   */
  protected def newWrapperEventHandler(
    eventHandler: EventHandler,
    eventArguments: Seq[JDIEventArgument]
  ): EventHandler = {
    val jdiEventArgumentProcessor =
      new JDIEventArgumentProcessor(eventArguments: _*)

    // Create a wrapper function that invokes the event handler only if the
    // filter processor yields a positive result, otherwise skip this handler
    (event: Event, data: Seq[JDIEventDataResult]) => {
      val (passesFilters, data, _) = jdiEventArgumentProcessor.processAll(event)
      if (passesFilters) eventHandler(event, data)
      else true
    }
  }

  /**
   * Retrieves the collection of event handler functions for the specific
   * event class.
   *
   * @param eventType The type of event whose functions to retrieve
   *
   * @return The collection of event functions
   */
  def getHandlersForEventType(eventType: EventType) : Seq[EventHandler] = {
    if (eventTypeToHandlerIds.containsKey(eventType)) {
      eventTypeToHandlerIds.get(eventType).values().asScala.toSeq
    } else {
      Nil
    }
  }

  /**
   * Retrieves the collection of event handler functions for the specific
   * event class.
   *
   * @param eventType The type of event whose functions to retrieve
   *
   * @return The collection of event functions
   */
  def getHandlerIdsForEventType(eventType: EventType): Seq[EventHandlerId] = {
    if (eventTypeToHandlerIds.containsKey(eventType)) {
      eventTypeToHandlerIds.get(eventType).keys().asScala.toSeq
    } else {
      Nil
    }
  }

  /**
   * Retrieves the handler with the specified id.
   *
   * @param eventHandlerId The id of the handler to retrieve
   *
   * @return Some event handler if found, otherwise None
   */
  def getEventHandler(eventHandlerId: EventHandlerId): Option[EventHandler] = {
    eventTypeToHandlerIds.values().asScala
      .find(_.containsKey(eventHandlerId))
      .map(_.get(eventHandlerId))
  }

  /**
   * Removes the event function from this manager.
   *
   * @param eventHandlerId The id of the event handler to remove
   *
   * @return Some event handler if removed, otherwise None
   */
  def removeEventHandler(eventHandlerId: EventHandlerId): Option[EventHandler] = {
    eventTypeToHandlerIds.values().asScala
      .find(_.containsKey(eventHandlerId))
      .map(_.remove(eventHandlerId))
  }

  /**
   * Generates an id for a new event handler.
   *
   * @return The id as a string
   */
  protected def newEventHandlerId(): String =
    java.util.UUID.randomUUID().toString

  // ==========================================================================
  // = CONSTRUCTOR
  // ==========================================================================

  // If marked to start automatically, do so
  if (autoStart) start()
}