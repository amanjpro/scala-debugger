package org.scaladebugger.api.pipelines

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}

class MapOperationSpec extends FunSpec with Matchers with ParallelTestExecution
  with MockFactory
{
  describe("MapOperation") {
    describe("#process") {
      it("should map all elements to other values") {
        val expected = Seq("1string", "2string", "3string")

        val data = Seq(1, 2, 3)
        val operation = new MapOperation[Int, String](i => s"${i}string")
        val actual = operation.process(data)

        actual should be (expected)
      }
    }
  }
}
