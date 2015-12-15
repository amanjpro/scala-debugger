package org.senkbeil.debugger.api.lowlevel.exceptions

import org.senkbeil.debugger.api.lowlevel.RequestInfo
import org.senkbeil.debugger.api.lowlevel.requests.JDIRequestArgument

/**
 * Represents information about an exception request.
 *
 * @param requestId The id of the request
 * @param className The full name of the exception class
 * @param notifyCaught Whether or not caught exceptions are reported
 * @param notifyUncaught Whether or not uncaught exceptions are reported
 * @param extraArguments The additional arguments provided to the exception
 */
case class ExceptionRequestInfo(
  requestId: String,
  className: String,
  notifyCaught: Boolean,
  notifyUncaught: Boolean,
  extraArguments: Seq[JDIRequestArgument] = Nil
) extends RequestInfo {
  /**
   * Indicates whether or not this exception request was to report all
   * exceptions or a specific exception.
   *
   * @return True if all exceptions are intended to be reported with this
   *         request, otherwise false
   */
  def isCatchall: Boolean = className == null
}
