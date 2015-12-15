package org.senkbeil.debugger.api.profiles.pure.watchpoints

import java.util.concurrent.atomic.AtomicBoolean

import org.scalatest.concurrent.Eventually
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}
import org.senkbeil.debugger.api.profiles.pure.PureDebugProfile
import org.senkbeil.debugger.api.virtualmachines.DummyScalaVirtualMachine
import test.{TestUtilities, VirtualMachineFixtures}

class PureModificationWatchpointProfileIntegrationSpec
  extends FunSpec with Matchers with ParallelTestExecution
  with VirtualMachineFixtures with TestUtilities with Eventually
{
  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(test.Constants.EventuallyTimeout),
    interval = scaled(test.Constants.EventuallyInterval)
  )

  describe("PureModificationWatchpointProfile") {
    it("should be able to detect modification to a field") {
      val testClass = "org.senkbeil.debugger.test.watchpoints.ModificationWatchpoint"
      val testFile = scalaClassStringToFileString(testClass)

      val className = "org.senkbeil.debugger.test.watchpoints.SomeModificationClass"
      val fieldName = "field"

      val detectedModificationWatchpoint = new AtomicBoolean(false)

      val s = DummyScalaVirtualMachine.newInstance()

      // Listen for modification watchpoint events for specific variable
      s.withProfile(PureDebugProfile.Name)
        .onUnsafeModificationWatchpoint(className, fieldName)
        .filter(_.field().declaringType().name() == className)
        .filter(_.field().name() == fieldName)
        .foreach(_ => detectedModificationWatchpoint.set(true))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          assert(detectedModificationWatchpoint.get(), s"$fieldName never modified!")
        })
      }
    }
  }
}