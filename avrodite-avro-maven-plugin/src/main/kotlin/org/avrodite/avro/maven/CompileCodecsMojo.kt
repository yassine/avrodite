package org.avrodite.avro.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import java.io.File
import java.lang.Thread.currentThread
import java.net.URL
import java.net.URLClassLoader
import java.util.*

@Mojo(name = "compile-codecs", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
class CompileCodecsMojo : AbstractMojo() {

  @Parameter(defaultValue = "\${project}", readonly = true)
  private lateinit var project: MavenProject

  @Parameter(alias = "context", required = true)
  private var contextPackages: List<String>? = ArrayList()

  @Parameter(alias = "additionalClasspathItems")
  private var additionalClasspathItems: List<String?> = ArrayList()

  @Parameter(defaultValue = "\${project.build.outputDirectory}", required = true)
  private lateinit var output: File

  override fun execute() {

    val oldClassLoader = currentThread().contextClassLoader
    val classpathItems = getClasspathItems() ?: setOf()
    val urls: Array<URL> = classpathItems.map { "file://${it}" }.map(::URL).toTypedArray()
    val urlClassLoader = URLClassLoader(urls, CompileCodecsMojo::class.java.classLoader)

    try {
      currentThread().contextClassLoader = urlClassLoader
    } catch (e: Exception) {
      currentThread().contextClassLoader = oldClassLoader
      urlClassLoader.close()
      e.printStackTrace()
      throw e
    }

  }

  private fun getClasspathItems(): Set<String>? = HashSet(project.compileClasspathElements) +
    additionalClasspathItems.filterNotNull()
      .flatMap { it.split(File.separator) }
      .map(String::trim)
      .filterNot(String::isEmpty)
      .toSet()

}
