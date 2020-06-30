package org.avrodite.tools.avro

import org.avrodite.meta.MetaManager
import org.avrodite.meta.type.TypeInfo
import org.avrodite.tools.AvroditeTools
import org.avrodite.tools.avro.AvroSchemaTools.createSchemaRegistry
import org.avrodite.tools.avro.AvroSchemaTools.createValueCodecRegistry
import org.avrodite.tools.compiler.*
import org.avrodite.tools.template.Template
import org.avrodite.tools.template.TypeInfoContext
import org.avrodite.tools.template.TypeMetaContext
import org.avrodite.tools.template.Utils

object AvroditeToolsAvro {

  private const val AVRO_CODEC_PREFIX: String = "AvroCodec"

  fun kotlin(builder: KotlinBuilder.() -> Unit)
    = KotlinBuilder().also(builder).build()

  @Suppress("MemberVisibilityCanBePrivate") //used by freemarker
  fun codecName(info: TypeInfo): String
    = info.let{"${it.simpleName()}_${AVRO_CODEC_PREFIX}_${Utils.hashSha(it.eraseNullability().signature(), 8)}"}

  @Suppress("MemberVisibilityCanBePrivate") //used by freemarker
  fun codecFqName(info: TypeInfo): String
    = codecName(info).let { "${TypeInfoContext(info).namespace()}.${it}" }

  class KotlinBuilder {

    private val loaders = mutableSetOf<ClassLoader>()
    private val metaManagerBuilder: MetaManager.Companion.Builder = MetaManager.Companion.Builder()
    private val compilerBuilder = KotlinJVMCompiler.Companion.Builder()

    fun addClassLoader(loader: ClassLoader) : KotlinBuilder = also {
      loaders.add(loader)
    }

    fun meta(builder: MetaManager.Companion.Builder.() -> Unit) : KotlinBuilder = also {
      builder(metaManagerBuilder)
    }

    fun compiler(receiver: KotlinJVMCompiler.Companion.Builder.() -> Unit) = also { receiver(compilerBuilder) }

    fun build() : FSCompilationResult {
      // scan for value codecs
      val valueCodecIndex = createValueCodecRegistry(loaders)
      val valuesTypes = valueCodecIndex.keys
      // build the meta manager
      val metaManager = metaManagerBuilder.scope {
        valueTypePredicate { type -> valuesTypes.any { it == type.java.name } }
      }.create()
      // create the schema registry
      val schemaIndex = createSchemaRegistry(metaManager, valueCodecIndex)
      // generate the codecs sources
      val sourceContexts = metaManager.index()
        .map { (_, v) -> TypeMetaContext(v) }
        .map { it ->
          it to Template.of {
            templateLocation = "org/avrodite/tools/avro/kotlin/codec.ftl"
            withContextVar("meta", it)
            withContextVar("names", AvroditeToolsAvro)
            withContextVar("schema", schemaIndex[it.typeMeta.typeInfo.signature()]?.toByteArray(Charsets.UTF_8)?.joinToString(", ") { "$it" } ?: "")
            withContextVar("valueCodecs", valueCodecIndex.entries.map { it.key to it.value::class.java.name }.associateBy({it.first}) {it.second})
          }.render().replace("\n+","\n")
        }.map { (typeMetaContext, source) -> SourceContext(codecFqName(typeMetaContext.type.info), source) }
      // dispatch the sources to the kotlin compiler
      return compilerBuilder.build().compile(sourceContexts)
    }

  }

}

val AvroditeTools.avro: AvroditeToolsAvro
  get() = AvroditeToolsAvro
