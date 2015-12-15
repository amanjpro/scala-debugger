package org.senkbeil.debugger.api.lowlevel.monitors

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}
import org.senkbeil.debugger.api.lowlevel.DummyOperationException

class DummyMonitorWaitedManagerSpec extends FunSpec with Matchers with MockFactory
  with ParallelTestExecution with org.scalamock.matchers.Matchers
{
  private val TestRequestId = java.util.UUID.randomUUID().toString
  private val monitorWaitedManager = new DummyMonitorWaitedManager

  describe("DummyMonitorWaitedManager") {
    describe("#monitorWaitedRequestList") {
      it("should return an empty list") {
        monitorWaitedManager.monitorWaitedRequestList should be (empty)
      }
    }

    describe("#createMonitorWaitedRequestWithId") {
      it("should return a failure of dummy operation") {
        val result = monitorWaitedManager.createMonitorWaitedRequestWithId(
          TestRequestId
        )

        result.isFailure should be (true)
        result.failed.get shouldBe a [DummyOperationException]
      }
    }

    describe("#hasMonitorWaitedRequest") {
      it("should return false") {
        val expected = false

        val actual = monitorWaitedManager.hasMonitorWaitedRequest(
          TestRequestId
        )

        actual should be (expected)
      }
    }

    describe("#getMonitorWaitedRequest") {
      it("should return None") {
        val expected = None

        val actual = monitorWaitedManager.getMonitorWaitedRequest(
          TestRequestId
        )

        actual should be (expected)
      }
    }

    describe("#getMonitorWaitedRequestInfo") {
      it("should return None") {
        val expected = None

        val actual = monitorWaitedManager.getMonitorWaitedRequestInfo(
          TestRequestId
        )

        actual should be (expected)
      }
    }

    describe("#removeMonitorWaitedRequest") {
      it("should return false") {
        val expected = false

        val actual = monitorWaitedManager.removeMonitorWaitedRequest(
          TestRequestId
        )

        actual should be (expected)
      }
    }
  }
}