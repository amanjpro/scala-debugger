package org.scaladebugger.api.profiles.pure
import com.sun.jdi.VirtualMachine
import org.scaladebugger.api.lowlevel.ManagerContainer
import org.scaladebugger.api.profiles.pure.requests.breakpoints.PureBreakpointProfile
import org.scaladebugger.api.profiles.pure.requests.classes.{PureClassPrepareProfile, PureClassUnloadProfile}
import org.scaladebugger.api.profiles.pure.requests.events.PureEventListenerProfile
import org.scaladebugger.api.profiles.pure.requests.exceptions.PureExceptionProfile
import org.scaladebugger.api.profiles.pure.info.{PureCreateInfoProfile, PureGrabInfoProfile, PureInfoProducerProfile, PureMiscInfoProfile}
import org.scaladebugger.api.profiles.pure.requests.methods.{PureMethodEntryProfile, PureMethodExitProfile}
import org.scaladebugger.api.profiles.pure.requests.monitors.{PureMonitorContendedEnterProfile, PureMonitorContendedEnteredProfile, PureMonitorWaitProfile, PureMonitorWaitedProfile}
import org.scaladebugger.api.profiles.pure.requests.steps.PureStepProfile
import org.scaladebugger.api.profiles.pure.requests.threads.{PureThreadDeathProfile, PureThreadStartProfile}
import org.scaladebugger.api.profiles.pure.requests.vm.{PureVMDeathProfile, PureVMDisconnectProfile, PureVMStartProfile}
import org.scaladebugger.api.profiles.pure.requests.watchpoints.{PureAccessWatchpointProfile, PureModificationWatchpointProfile}
import org.scaladebugger.api.profiles.traits.ManagerContainerDebugProfile
import org.scaladebugger.api.profiles.traits.info.InfoProducerProfile
import org.scaladebugger.api.virtualmachines.ScalaVirtualMachine

/**
 * Contains information about the pure debug profile.
 */
object PureDebugProfile {
  val Name: String = "pure"
}

/**
 * Represents a debug profile that adds no extra logic on top of the standard
 * JDI.
 *
 * @param scalaVirtualMachine The high-level virtual machine using this profile
 * @param managerContainer The container of low-level managers to use as the
 *                         underlying implementation
 * @param _virtualMachine The underlying virtual machine to use for various
 *                        retrieval methods
 * @param infoProducer The producer of information profiles
 */
class PureDebugProfile(
  protected val scalaVirtualMachine: ScalaVirtualMachine,
  protected val managerContainer: ManagerContainer
)(
  protected val _virtualMachine: VirtualMachine =
    scalaVirtualMachine.underlyingVirtualMachine,
  protected val infoProducer: InfoProducerProfile = new PureInfoProducerProfile
) extends ManagerContainerDebugProfile
  with PureAccessWatchpointProfile
  with PureBreakpointProfile
  with PureClassPrepareProfile
  with PureClassUnloadProfile
  with PureCreateInfoProfile
  with PureEventListenerProfile
  with PureExceptionProfile
  with PureGrabInfoProfile
  with PureMethodEntryProfile
  with PureMethodExitProfile
  with PureMiscInfoProfile
  with PureModificationWatchpointProfile
  with PureMonitorContendedEnteredProfile
  with PureMonitorContendedEnterProfile
  with PureMonitorWaitedProfile
  with PureMonitorWaitProfile
  with PureStepProfile
  with PureThreadDeathProfile
  with PureThreadStartProfile
  with PureVMStartProfile
  with PureVMDeathProfile
  with PureVMDisconnectProfile
