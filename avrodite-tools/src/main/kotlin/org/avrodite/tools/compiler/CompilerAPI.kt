package org.avrodite.tools.compiler

import java.io.File

interface FileSystemCompiler: Compiler<FSCompilationResult>

interface Compiler<out R : CompilationResult> {
  fun compile(source: List<SourceContext>): R
}

enum class CompilationStatus {
  SUCCESS,
  ERROR
}

class SourceContext(val fqName: String, val codecSourceCode: String)
open class CompilationResult(val status: CompilationStatus, val results: Map<String, ByteArray>)
class FSCompilationResult(status: CompilationStatus, results: Map<String, ByteArray>, val sourceDir: File, val outputDir: File) : CompilationResult(status, results)
