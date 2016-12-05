package org.scaladebugger.api.profiles.pure.info.events

import com.sun.jdi._
import com.sun.jdi.event._
import org.scaladebugger.api.lowlevel.JDIArgument
import org.scaladebugger.api.profiles.traits.info.InfoProducerProfile
import org.scaladebugger.api.profiles.traits.info.events._
import org.scaladebugger.api.virtualmachines.ScalaVirtualMachine

/**
 * Represents the interface to produce pure event info profile instances.
 *
 * @param infoProducer The parent information producer of this event
 *                     information producer
 */
class PureEventInfoProducerProfile(
  val infoProducer: InfoProducerProfile
) extends EventInfoProducerProfile {
  /**
   * Returns whether or not this info profile represents the low-level Java
   * implementation.
   *
   * @return If true, this profile represents the low-level Java information,
   *         otherwise this profile represents something higher-level like
   *         Scala, Jython, or JRuby
   */
  override def isJavaInfo: Boolean = true

  /**
   * Converts the current profile instance to a representation of
   * low-level Java instead of a higher-level abstraction.
   *
   * @return The profile instance providing an implementation corresponding
   *         to Java
   */
  override def toJavaInfo: EventInfoProducerProfile = {
    new PureEventInfoProducerProfile(infoProducer.toJavaInfo)
  }

  override def newEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    event: Event,
    jdiArguments: Seq[JDIArgument]
  ): EventInfoProfile = new PureEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    event = event,
    jdiArguments = jdiArguments
  )

  override def newLocatableEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    locatableEvent: LocatableEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): LocatableEventInfoProfile = new PureLocatableEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    locatableEvent = locatableEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newWatchpointEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    watchpointEvent: WatchpointEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    container: => Either[ObjectReference, ReferenceType],
    field: => Field,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): WatchpointEventInfoProfile = new PureWatchpointEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    watchpointEvent = watchpointEvent,
    jdiArguments = jdiArguments
  )(
    _container = container,
    _field = field,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newAccessWatchpointEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    accessWatchpointEvent: AccessWatchpointEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    container: => Either[ObjectReference, ReferenceType],
    field: => Field,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): AccessWatchpointEventInfoProfile = new PureAccessWatchpointEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    accessWatchpointEvent = accessWatchpointEvent,
    jdiArguments = jdiArguments
  )(
    _container = container,
    _field = field,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newBreakpointEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    breakpointEvent: BreakpointEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): BreakpointEventInfoProfile = new PureBreakpointEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    breakpointEvent = breakpointEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newClassPrepareEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    classPrepareEvent: ClassPrepareEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    referenceType: => ReferenceType
  ): ClassPrepareEventInfoProfile = new PureClassPrepareEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    classPrepareEvent = classPrepareEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _referenceType = referenceType
  )

  override def newClassUnloadEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    classUnloadEvent: ClassUnloadEvent,
    jdiArguments: Seq[JDIArgument]
  ): ClassUnloadEventInfoProfile = new PureClassUnloadEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    classUnloadEvent = classUnloadEvent,
    jdiArguments = jdiArguments
  )

  override def newExceptionEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    exceptionEvent: ExceptionEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    catchLocation: => Option[Location],
    exception: => ObjectReference,
    exceptionReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): ExceptionEventInfoProfile = new PureExceptionEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    exceptionEvent = exceptionEvent,
    jdiArguments = jdiArguments
  )(
    _catchLocation = catchLocation,
    _exception = exception,
    _exceptionReferenceType = exceptionReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMethodEntryEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    methodEntryEvent: MethodEntryEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    method: => Method,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MethodEntryEventInfoProfile = new PureMethodEntryEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    methodEntryEvent = methodEntryEvent,
    jdiArguments = jdiArguments
  )(
    _method = method,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMethodExitEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    methodExitEvent: MethodExitEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    method: => Method,
    returnValue: => Value,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MethodExitEventInfoProfile = new PureMethodExitEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    methodExitEvent = methodExitEvent,
    jdiArguments = jdiArguments
  )(
    _method = method,
    _returnValue = returnValue,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newModificationWatchpointEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    modificationWatchpointEvent: ModificationWatchpointEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    container: => Either[ObjectReference, ReferenceType],
    field: => Field,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): ModificationWatchpointEventInfoProfile = new PureModificationWatchpointEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    modificationWatchpointEvent = modificationWatchpointEvent,
    jdiArguments = jdiArguments
  )(
    _container = container,
    _field = field,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMonitorEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    monitorEvent: MonitorEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    monitor: => ObjectReference,
    monitorReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MonitorEventInfoProfile = new PureMonitorEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    monitorEvent = monitorEvent,
    jdiArguments = jdiArguments
  )(
    _monitor = monitor,
    _monitorReferenceType = monitorReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMonitorContendedEnteredEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    monitorContendedEnteredEvent: MonitorContendedEnteredEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    monitor: => ObjectReference,
    monitorReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MonitorContendedEnteredEventInfoProfile = new PureMonitorContendedEnteredEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    monitorContendedEnteredEvent = monitorContendedEnteredEvent,
    jdiArguments = jdiArguments
  )(
    _monitor = monitor,
    _monitorReferenceType = monitorReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMonitorContendedEnterEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    monitorContendedEnterEvent: MonitorContendedEnterEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    monitor: => ObjectReference,
    monitorReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MonitorContendedEnterEventInfoProfile = new PureMonitorContendedEnterEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    monitorContendedEnterEvent = monitorContendedEnterEvent,
    jdiArguments = jdiArguments
  )(
    _monitor = monitor,
    _monitorReferenceType = monitorReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMonitorWaitedEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    monitorWaitedEvent: MonitorWaitedEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    monitor: => ObjectReference,
    monitorReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MonitorWaitedEventInfoProfile = new PureMonitorWaitedEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    monitorWaitedEvent = monitorWaitedEvent,
    jdiArguments = jdiArguments
  )(
    _monitor = monitor,
    _monitorReferenceType = monitorReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newMonitorWaitEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    monitorWaitEvent: MonitorWaitEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    monitor: => ObjectReference,
    monitorReferenceType: => ReferenceType,
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): MonitorWaitEventInfoProfile = new PureMonitorWaitEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    monitorWaitEvent = monitorWaitEvent,
    jdiArguments = jdiArguments
  )(
    _monitor = monitor,
    _monitorReferenceType = monitorReferenceType,
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newStepEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    stepEvent: StepEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType,
    location: => Location
  ): StepEventInfoProfile = new PureStepEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    stepEvent = stepEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType,
    _location = location
  )

  override def newThreadDeathEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    threadDeathEvent: ThreadDeathEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType
  ): ThreadDeathEventInfoProfile = new PureThreadDeathEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    threadDeathEvent = threadDeathEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType
  )

  override def newThreadStartEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    threadStartEvent: ThreadStartEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType
  ): ThreadStartEventInfoProfile = new PureThreadStartEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    threadStartEvent = threadStartEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType
  )

  override def newVMDeathEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    vmDeathEvent: VMDeathEvent,
    jdiArguments: Seq[JDIArgument]
  ): VMDeathEventInfoProfile = new PureVMDeathEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    vmDeathEvent = vmDeathEvent,
    jdiArguments = jdiArguments
  )

  override def newVMDisconnectEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    vmDisconnectEvent: VMDisconnectEvent,
    jdiArguments: Seq[JDIArgument]
  ): VMDisconnectEventInfoProfile = new PureVMDisconnectEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    vmDisconnectEvent = vmDisconnectEvent,
    jdiArguments = jdiArguments
  )

  override def newVMStartEventInfoProfile(
    scalaVirtualMachine: ScalaVirtualMachine,
    vmStartEvent: VMStartEvent,
    jdiArguments: Seq[JDIArgument]
  )(
    virtualMachine: => VirtualMachine,
    thread: => ThreadReference,
    threadReferenceType: => ReferenceType
  ): VMStartEventInfoProfile = new PureVMStartEventInfoProfile(
    scalaVirtualMachine = scalaVirtualMachine,
    infoProducer = infoProducer,
    vmStartEvent = vmStartEvent,
    jdiArguments = jdiArguments
  )(
    _virtualMachine = virtualMachine,
    _thread = thread,
    _threadReferenceType = threadReferenceType
  )
}
