package org.avrodite.tools.template

import freemarker.cache.ClassTemplateLoader
import freemarker.core.PlainTextOutputFormat
import freemarker.template.Configuration
import java.io.StringWriter
import java.io.Writer
import freemarker.template.Template as FreemarkerTemplate

class Template internal constructor(
  private val context: Map<String, Any>,
  private val configuration: Configuration,
  private val templateLocation: String
){

  fun render() : String {
    val temp: FreemarkerTemplate = configuration.getTemplate(templateLocation)
    val out: Writer = StringWriter()
    temp.process(context, out)
    return out.toString()
  }

  companion object {

    fun of(init: Builder.() -> Unit): Template = Builder().also(init).create()

    class Builder internal constructor() {
      private val context: MutableMap<String, Any> = hashMapOf()
      private val configuration: Configuration = Configuration(Configuration.VERSION_2_3_30)
      lateinit var templateLocation: String

      fun withContextVar(key: String, context: Any) : Builder
        = also { it.context[key] = context }

      fun configure(configure: Configuration.() -> Unit) : Builder
        = also { configuration.configure() }

      fun create() : Template
        = configuration.apply {
            //apply config defaults if not already set
            templateLoader  = templateLoader ?: ClassTemplateLoader(Template::class.java.classLoader, "/")
            defaultEncoding = defaultEncoding ?: "UTF-8"
            outputFormat    = PlainTextOutputFormat.INSTANCE
          }.let {
            Template(context.toMap(), it, templateLocation)
          }

    }

  }

}


