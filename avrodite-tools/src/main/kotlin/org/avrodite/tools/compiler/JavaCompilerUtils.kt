package org.avrodite.tools.compiler

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.URI
import java.util.*
import java.util.Collections.unmodifiableMap
import javax.tools.*

internal object JavaCompilerUtils {

  internal class ClassFileManager(m: StandardJavaFileManager?) : ForwardingJavaFileManager<StandardJavaFileManager?>(m) {

    private val files: MutableMap<String, CustomJavaFileObject> = HashMap()

    override fun getJavaFileForOutput(location: JavaFileManager.Location, className: String, kind: JavaFileObject.Kind, sibling: FileObject): CustomJavaFileObject
      = files[className] ?: CustomJavaFileObject(className, kind).also { files[className] = it }

    fun files(): Map<String, CustomJavaFileObject> = unmodifiableMap(files)

  }

  internal class CustomJavaFileObject internal constructor(name: String, kind: JavaFileObject.Kind) : SimpleJavaFileObject(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind) {
    private val os = ByteArrayOutputStream()
    val bytes: ByteArray get() = os.toByteArray()

    override fun openOutputStream(): OutputStream = os
  }

  internal class CharSequenceJavaFileObject(className: String, private val content: CharSequence) : SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE) {
    override fun getCharContent(ignoreEncodingErrors: Boolean): CharSequence = content
  }



}
