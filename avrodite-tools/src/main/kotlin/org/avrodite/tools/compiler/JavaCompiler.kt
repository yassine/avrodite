package org.avrodite.tools.compiler

import io.github.classgraph.ClassGraph
import mu.KotlinLogging
import org.avrodite.tools.compiler.JavaCompilerUtils.CharSequenceJavaFileObject
import org.avrodite.tools.compiler.JavaCompilerUtils.ClassFileManager
import java.io.File
import java.net.URI
import javax.tools.ToolProvider.getSystemJavaCompiler

class JavaCompiler internal constructor(val classPath: String) : Compiler<CompilationResult> {

  internal val logger = KotlinLogging.logger {}

  override fun compile(source: List<SourceContext>): CompilationResult {
    val compiler = getSystemJavaCompiler()
    val standardJavaFileManager = compiler.getStandardFileManager(null, null, null)
    val manager = ClassFileManager(standardJavaFileManager)
    val sourceFiles = source.map { CharSequenceJavaFileObject(it.fqName, it.codecSourceCode) }
    val options = mutableListOf<String>()

    if (classPath.trim().isNotBlank()) {
      options.add("-classpath")
      options.add(classPath)
    }

    val success = compiler.getTask(null, manager, null, options, null, sourceFiles).call() ?: false
    val results = manager.files()
      .entries.map { it.key to it.value.bytes }
      .associateBy({ it.first }, { it.second })
    val status  = success.takeIf { it }?.let { CompilationStatus.SUCCESS } ?: CompilationStatus.ERROR

    return CompilationResult(status, results)

  }

  companion object {

    fun of(init: Builder.() -> Unit): JavaCompiler = Builder().also(init).build()

    class Builder internal constructor() {

      private val classPathItems = hashSetOf<String>()

      var addVMClasspath: Boolean = true

      fun build(): JavaCompiler {
        if (addVMClasspath) {
          ClassGraph().classpathURIs
            .mapNotNull { obj: URI -> obj.path }
            .forEach { classPathItems.add(it) }
        }
        return JavaCompiler(classPathItems.joinToString(File.pathSeparator))
      }

    }

  }

}
