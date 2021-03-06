package org.scaladebugger.api.lowlevel.exceptions

import com.sun.jdi.request.ExceptionRequest
import org.scaladebugger.api.lowlevel.requests.JDIRequestArgument

import scala.util.Try

/**
 * Represents the manager for exception requests.
 */
trait ExceptionManager {
  /**
   * Retrieves the list of exception requests contained by this manager.
   *
   * @return The collection of exception request information
   */
  def exceptionRequestList: Seq[ExceptionRequestInfo]

  /**
   * Retrieves the list of exception requests contained by this manager.
   *
   * @return The collection of exception requests by id
   */
  def exceptionRequestListById: Seq[String]

  /**
   * Creates a new exception request to catch all exceptions from the JVM.
   *
   * @note The request id given does not get added to the request id list and
   *       removing by id will not remove this request instance.
   *
   * @param requestId The id associated with the requests for lookup and removal
   * @param notifyCaught If true, events will be reported when any exception
   *                     is detected within a try { ... } block
   * @param notifyUncaught If true, events will be reported when any exception
   *                       is detected not within a try { ... } block
   * @param extraArguments Any additional arguments to provide to the request
   *
   * @return Success(id) if successful, otherwise Failure
   */
  def createCatchallExceptionRequestWithId(
    requestId: String,
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIRequestArgument*
  ): Try[String]

  /**
   * Creates a new exception request to catch all exceptions from the JVM.
   *
   * @note The request id given does not get added to the request id list and
   *       removing by id will not remove this request instance.
   *
   * @param notifyCaught If true, events will be reported when any exception
   *                     is detected within a try { ... } block
   * @param notifyUncaught If true, events will be reported when any exception
   *                       is detected not within a try { ... } block
   * @param extraArguments Any additional arguments to provide to the request
   *
   * @return Success(id) if successful, otherwise Failure
   */
  def createCatchallExceptionRequest(
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIRequestArgument*
  ): Try[String] = createCatchallExceptionRequestWithId(
    newRequestId(),
    notifyCaught,
    notifyUncaught,
    extraArguments: _*
  )

  /**
   * Creates a new exception request for the specified exception class.
   *
   * @note Any exception and its subclass will be watched.
   *
   * @param requestId The id associated with the requests for lookup and removal
   * @param exceptionName The full class name of the exception to watch
   * @param notifyCaught If true, events will be reported when the exception
   *                     is detected within a try { ... } block
   * @param notifyUncaught If true, events will be reported when the exception
   *                       is detected not within a try { ... } block
   * @param extraArguments Any additional arguments to provide to the request
   *
   * @return Success(id) if successful, otherwise Failure
   */
  def createExceptionRequestWithId(
    requestId: String,
    exceptionName: String,
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIRequestArgument*
  ): Try[String]

  /**
   * Creates a new exception request for the specified exception class.
   *
   * @note Any exception and its subclass will be watched.
   *
   * @param exceptionName The full class name of the exception to watch
   * @param notifyCaught If true, events will be reported when the exception
   *                     is detected within a try { ... } block
   * @param notifyUncaught If true, events will be reported when the exception
   *                       is detected not within a try { ... } block
   * @param extraArguments Any additional arguments to provide to the request
   *
   * @return Success(id) if successful, otherwise Failure
   */
  def createExceptionRequest(
    exceptionName: String,
    notifyCaught: Boolean,
    notifyUncaught: Boolean,
    extraArguments: JDIRequestArgument*
  ): Try[String] = createExceptionRequestWithId(
    newRequestId(),
    exceptionName,
    notifyCaught,
    notifyUncaught,
    extraArguments: _*
  )

  /**
   * Creates a new exception request based on the specified information. If the
   * class name is null, will create a catchall exception request.
   *
   * @param exceptionRequestInfo The information used to create the exception
   *                             request
   *
   * @return Success(id) if successful, otherwise Failure
   */
  def createExceptionRequestFromInfo(
    exceptionRequestInfo: ExceptionRequestInfo
  ): Try[String] = {
    if (!exceptionRequestInfo.isCatchall) {
      createExceptionRequestWithId(
        exceptionRequestInfo.requestId,
        exceptionRequestInfo.className,
        exceptionRequestInfo.notifyCaught,
        exceptionRequestInfo.notifyUncaught,
        exceptionRequestInfo.extraArguments: _*
      )
    } else {
      createCatchallExceptionRequestWithId(
        exceptionRequestInfo.requestId,
        exceptionRequestInfo.notifyCaught,
        exceptionRequestInfo.notifyUncaught,
        exceptionRequestInfo.extraArguments: _*
      )
    }
  }

  /**
   * Determines if an exception request exists for the specified exception
   * class name.
   *
   * @param exceptionName The full class name of the exception targeted by the
   *                      exception requests
   *
   * @return True if a exception request exists, otherwise false
   */
  def hasExceptionRequest(exceptionName: String): Boolean

  /**
   * Determines if an exception request exists with the specified id.
   *
   * @param requestId The id of the request used to retrieve and delete it
   *
   * @return True if a exception request exists, otherwise false
   */
  def hasExceptionRequestWithId(requestId: String): Boolean

  /**
   * Retrieves the collection of exception requests with the matching exception
   * class name.
   *
   * @param exceptionName The full class name of the exception targeted by the
   *                      exception requests
   *
   * @return Some collection of exception requests if they exist, otherwise None
   */
  def getExceptionRequest(
    exceptionName: String
  ): Option[Seq[ExceptionRequest]]

  /**
   * Retrieves the collection of exception requests with the specified id.
   *
   * @param requestId The id of the request used to retrieve and delete it
   *
   * @return Some collection of exception requests if they exist, otherwise None
   */
  def getExceptionRequestWithId(
    requestId: String
  ): Option[Seq[ExceptionRequest]]

  /**
   * Returns the information for an exception request with the specified id.
   *
   * @param requestId The id of the request
   *
   * @return Some exception information if found, otherwise None
   */
  def getExceptionRequestInfoWithId(
    requestId: String
  ): Option[ExceptionRequestInfo]

  /**
   * Removes the specified exception requests with the matching exception
   * class name.
   *
   * @param exceptionName The full class name of the exception targeted by the
   *                      exception requests
   *
   * @return True if the exception requests were removed (if they existed),
   *         otherwise false
   */
  def removeExceptionRequest(exceptionName: String): Boolean

  /**
   * Removes the exception request with the specified id.
   *
   * @param requestId The id of the request
   *
   * @return True if the exception request was removed (if it existed),
   *         otherwise false
   */
  def removeExceptionRequestWithId(requestId: String): Boolean

  /**
   * Generates an id for a new request.
   *
   * @return The id as a string
   */
  protected def newRequestId(): String = java.util.UUID.randomUUID().toString
}
