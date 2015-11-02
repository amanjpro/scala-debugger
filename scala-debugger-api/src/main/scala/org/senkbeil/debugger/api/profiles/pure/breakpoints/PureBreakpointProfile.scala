package org.senkbeil.debugger.api.profiles.pure.breakpoints

import com.sun.jdi.event.BreakpointEvent
import org.senkbeil.debugger.api.lowlevel.JDIArgument
import org.senkbeil.debugger.api.lowlevel.breakpoints.BreakpointManager
import org.senkbeil.debugger.api.lowlevel.requests.JDIRequestArgument
import org.senkbeil.debugger.api.lowlevel.utils.JDIRequestResponseBuilder
import org.senkbeil.debugger.api.pipelines.Pipeline
import org.senkbeil.debugger.api.pipelines.Pipeline.IdentityPipeline
import org.senkbeil.debugger.api.profiles.traits.breakpoints.BreakpointProfile

/**
 * Represents a pure profile for breakpoints that adds no extra logic on top
 * of the standard JDI.
 */
trait PureBreakpointProfile extends BreakpointProfile {
  protected val breakpointManager: BreakpointManager
  protected val requestResponseBuilder: JDIRequestResponseBuilder

  /**
   * Constructs a stream of breakpoint events for the specified file and line
   * number.
   *
   * @param fileName The name of the file where the breakpoint will be set
   * @param lineNumber The line number within the file where the breakpoint
   *                   will be set
   * @param extraArguments The additional JDI arguments to provide
   *
   * @return The stream of breakpoint events and any retrieved data based on
   *         requests from extra arguments
   */
  override def onBreakpointWithData(
    fileName: String,
    lineNumber: Int,
    extraArguments: JDIArgument*
  ): IdentityPipeline[BreakpointEventAndData] = {
    /** Creates a new request using arguments. */
    def newRequest(args: Seq[JDIRequestArgument]) = {
      // TODO: Provide error handling if determine breakpoint not pending?
      val success = breakpointManager.setLineBreakpoint(
        fileName,
        lineNumber,
        args: _*
      )
    }

    requestResponseBuilder.buildRequestResponse[BreakpointEvent](
      newRequest,
      extraArguments: _*
    )
  }
}