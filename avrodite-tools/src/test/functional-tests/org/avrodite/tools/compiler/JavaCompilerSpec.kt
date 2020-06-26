package org.avrodite.tools.compiler

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

object JavaCompilerSpec : Spek({

  val validSource = """

        package org.avrodite.tools.test;

        public class Test {
          public static void main() {
            System.out.println();
          }
        }

      """.trimIndent()

  describe("when a jv compiler is configured") {
    it("should compile correct Java code") {
      val compiler = JavaCompiler.of { addVMClasspath = true }
      val result = compiler.compile(listOf(SourceContext("org.avrodite.tools.test.Test", validSource)))

      expectThat(result.status).isEqualTo(CompilationStatus.SUCCESS)
      expectThat(result.results["org.avrodite.tools.test.Test"]).isNotNull()
    }
  }

})
