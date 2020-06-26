package org.avrodite.tools.compiler


import io.github.classgraph.ClassGraph
import mu.KotlinLogging.logger
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Files.find

import java.util.function.BiPredicate
import java.util.stream.Collectors.toList
import kotlin.Int.Companion.MAX_VALUE


class KotlinJVMCompiler internal constructor(private val arguments: K2JVMCompilerArguments) : Compiler<FSCompilationResult> {


  override fun compile(source: List<SourceContext>): FSCompilationResult {
    synchronized(arguments) {
      val inputDir: File = Files.createTempDirectory(PREFIX).toFile()
      createSourceFiles(inputDir, source)

      logger.debug { "classpath: " + arguments.classpath }

      val outputDir: File = arguments.destination?.let { File(it) }?.also { it.mkdirs() }
        ?: Files.createTempDirectory(PREFIX).toFile()

      logger.debug { "outputDir: ${outputDir.absolutePath}" }

      arguments.apply {
        destination = destination ?: outputDir.absolutePath
        freeArgs = listOf(inputDir.absolutePath)
      }

      val result = K2JVMCompiler().exec(PrivateCollector(), Services.EMPTY, arguments)
      if (result == ExitCode.OK) {
        val metaInfFile = File(outputDir, "META-INF/services/org.avrodite.api.Codec")
        metaInfFile.parentFile.mkdirs()
        if (!metaInfFile.canRead()) {
          metaInfFile.createNewFile()
        }
        val lines = Files.readAllLines(metaInfFile.toPath())
        val sourceIndex: Map<String, SourceContext> = source.associateBy { it.fqName }
        val outputIndex: Map<String, ByteArray> = find(outputDir.toPath(), MAX_VALUE, BiPredicate { _, fileAttributes -> !fileAttributes.isDirectory })
          .filter { f -> source.map { it.fqName.replace(".", File.separator) }.any {
              ( f.toFile().absolutePath.endsWith("${it}.class") || f.toFile().absolutePath.endsWith("${it}Kt.class") )
            }
          }.collect(toList())
          .map { it.toFile() }
          .map { f ->
            val sourceKey = sourceIndex.keys.first { f.absolutePath.contains(it.replace(".", File.separator)) }
            val sourceContext: SourceContext = sourceIndex[sourceKey]
              ?: error("couldn't find source for key $sourceKey")
            val bytes = Files.readAllBytes(f.toPath()) ?: error("couldn't read file ${f.absolutePath}")
            lines.add(sourceContext.fqName)
            sourceContext.fqName to bytes
          }.associateBy({ it.first }, { it.second })

        lines.toSet().joinToString("\n").also {
          metaInfFile.writeText(it, Charsets.UTF_8)
        }

        return FSCompilationResult(CompilationStatus.SUCCESS, outputIndex, inputDir, outputDir)
      }

      return FSCompilationResult(CompilationStatus.ERROR, mapOf(), inputDir, outputDir)
    }

  }

  private fun createSourceFiles(dir: File, source: List<SourceContext>) {
    logger.debug { "input source dir : ${dir.absolutePath}" }
    source.forEach {
      val file = File(dir, "${it.fqName.replace(".", File.separator)}.kt")
      file.parentFile.mkdirs()
      file.writeText(it.codecSourceCode)
      logger.debug { "create source file: ${file.absolutePath}" }
    }
  }


  companion object {
    internal const val PREFIX: String = "avrodite-kt"
    internal val logger = logger {}

    fun of(init: Builder.() -> Unit): KotlinJVMCompiler = Builder().also(init).build()

    class Builder constructor() {

      private val classPathItems = hashSetOf<String>()
      private val arguments = K2JVMCompilerArguments()

      var verbose: Boolean = true
      var multiPlatform: Boolean = false
      var jvmTarget: String = "1.8"
      var addVMClasspath: Boolean = true
      var outputDir: String? = null

      fun addClassPathItem(item: String): Builder = also { classPathItems.add(item) }

      fun addClassPathItems(items: Collection<String>): Builder = also { items.onEach { classPathItems.add(it) } }

      fun compilerArguments(customizer: K2JVMCompilerArguments.() -> Unit): Builder = also { arguments.customizer() }

      fun build(): KotlinJVMCompiler {
        //add vm classpath
        if (addVMClasspath) {
          ClassGraph().classpathURIs
            .mapNotNull { obj: URI -> obj.path }
            .forEach { e: String? -> addClassPathItem(e!!) }
        }

        //configure compiler arguments
        arguments.apply {
          classpath = this@Builder.classPathItems.joinToString(File.pathSeparator)
          destination = destination ?: this@Builder.outputDir
          jvmTarget = jvmTarget ?: this@Builder.jvmTarget
          multiPlatform = multiPlatform || this@Builder.multiPlatform
          verbose = verbose || this@Builder.verbose
        }

        return KotlinJVMCompiler(arguments)
      }

    }

    internal class PrivateCollector : MessageCollector {
      private var hasErrors = false

      override fun clear() = logger.debug("clearing")

      override fun hasErrors(): Boolean = hasErrors

      override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
        val text = location?.let {
          "${it.path} [${it.line}, ${it.column}] $message"
        } ?: message

        when (severity) {
          CompilerMessageSeverity.EXCEPTION, CompilerMessageSeverity.ERROR -> {
            hasErrors = true
            logger.error(text)
          }
          CompilerMessageSeverity.STRONG_WARNING, CompilerMessageSeverity.WARNING ->
            logger.warn(text)
          CompilerMessageSeverity.INFO ->
            logger.info(text)
          CompilerMessageSeverity.LOGGING, CompilerMessageSeverity.OUTPUT ->
            logger.info(text)
          else ->
            logger.warn("[Unknown severity $severity] $text")
        }

      }

    }
  }

}




