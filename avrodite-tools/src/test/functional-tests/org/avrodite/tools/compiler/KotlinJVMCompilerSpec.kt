package org.avrodite.tools.compiler

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.io.File

object KotlinJVMCompilerSpec : Spek({



  val source = """
        package org.avrodite.tools.test
        fun hello(arr: Array<String>){
          println(arr)
        }
      """.trimIndent()

  describe("when a kotlin compiler is configured with jvm as target") {

    it("sources are copied to configured directory") {
      val compiler = KotlinJVMCompiler.of {
        compilerArguments {
          multiPlatform = false
          noStdlib = true
        }
      }
      val context = listOf(SourceContext("org.avrodite.tools.test.Test", source))
      val result  = compiler.compile(context)
      val sourceFile = File(result.sourceDir, "org/avrodite/tools/test/Test.kt")

      expectThat(sourceFile.exists()).isTrue()
      expectThat(sourceFile.readText()).isEqualTo(source)

      result.sourceDir.deleteRecursively()
      result.outputDir.deleteRecursively()

    }

    it("compiled files are copied to configured output directory and under given package") {
      val compiler = KotlinJVMCompiler.of {
        compilerArguments {
          multiPlatform = false
          noStdlib = true
        }
      }
      val context = listOf(SourceContext("org.avrodite.tools.test.Test", source))
      val result  = compiler.compile(context)
      val byteCodeFile = File(result.outputDir, "org/avrodite/tools/test/TestKt.class")

      expectThat(byteCodeFile.exists()).isTrue()
      println(result.results.keys)
      expectThat(byteCodeFile.readBytes()).isEqualTo(result.results["org.avrodite.tools.test.Test"])

      result.sourceDir.deleteRecursively()
      result.outputDir.deleteRecursively()

    }

  }

})
