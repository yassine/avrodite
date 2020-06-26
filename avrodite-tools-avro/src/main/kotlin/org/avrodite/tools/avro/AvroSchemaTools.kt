package org.avrodite.tools.avro

import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.avrodite.avro.AvroFormat
import org.avrodite.avro.AvroValueCodec
import org.avrodite.meta.MetaManager
import org.avrodite.meta.generics.GenericsContext
import org.avrodite.meta.type.MemberMeta
import org.avrodite.meta.type.TypeCategory.*
import org.avrodite.meta.type.TypeMeta
import org.avrodite.tools.template.Utils.hashSha
import org.avrodite.tools.utils.ScanUtils.classPathScannerWith
import org.avrodite.tools.utils.ScanUtils.classesImplementing
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

object AvroSchemaTools {

  @Suppress("MemberVisibilityCanBePrivate")
  const val SCHEMA_TYPE_PROP = "org.avrodite.avro.type"

  fun createValueCodecRegistry(loaders: Set<ClassLoader> = setOf()) : Map<String, AvroValueCodec<*>>
    = classPathScannerWith(loaders)
        .let { scanner ->
          classesImplementing(scanner, AvroFormat.api().value().java)
            .map { it.kotlin.createType() }
            .map { it to (GenericsContext(it).resolve(AvroFormat.api().value())?.typeParameters ?: listOf()) }
            .filter { it.second.isNotEmpty() }
            .map { it.first to it.second.first() }
        }.associateBy({ (it.second.classifier as KClass<*>).qualifiedName!! }, { it -> it.first.classifier!!.let { it as KClass<*> }.let { it.java.newInstance() as AvroValueCodec<*> } })

  fun createSchemaRegistry(metaManager: MetaManager, valueCodecIndex: Map<String, AvroValueCodec<*>> = createValueCodecRegistry()): Map<String, String> {
    val index = metaManager.index()
    val schemaBuilder = SchemaBuilder.builder()
    val context = SchemaRegistryContext(schemaBuilder, metaManager, valueCodecIndex)
    //scan for value codecs TODO
    val schemaIndex = mutableMapOf<String, Schema>()
    index.entries.forEach { schemaIndex[it.key] = defineSchema(it.value, context) }
    return schemaIndex.entries
      .map { it.key to it.value.toString(true) }
      .associateBy({ it.first }, { it.second })
  }

  private fun defineSchema(typeMeta: TypeMeta, context: SchemaRegistryContext): Schema {
    val schemaName = schemaName(typeMeta)
    if (context.definedTypes.containsKey(schemaName)) {
      return context.base.type(schemaName)
    }
    context.definedTypes[schemaName] = true
    val recordBuilder = context.base.record(schemaName).also { it.prop(SCHEMA_TYPE_PROP, typeMeta.typeInfo.signature()) }
    val fieldAssembler = recordBuilder.fields()
    (typeMeta.constructorParams + typeMeta.props).forEach {
      fieldAssembler.name(it.name).type(defineField(it, context)).noDefault()
    }
    return fieldAssembler.endRecord()
  }

  private fun defineField(memberMeta: MemberMeta, context: SchemaRegistryContext): Schema = when (memberMeta.typeInfo.category) {
    LIST, ARRAY, SET -> {
      val compositeSchema = defineSchema(context.metaManager.index()[memberMeta.typeInfo.composite?.signature()]!!, context)
      context.base.array().items(
        memberMeta.typeInfo.isNullable().takeIf { it }
          ?.let { context.base.nullable().type(compositeSchema) }
          ?: compositeSchema
      )
    }
    MAP -> {
      val compositeSchema = defineSchema(context.metaManager.index()[memberMeta.typeInfo.composite?.signature()]!!, context)
      context.base.map().values(compositeSchema)
    }
    TYPE -> defineSchema(context.metaManager.index()[memberMeta.typeInfo.signature()]
      ?: error("type not found"), context)
    VALUE -> (context.valueCodecIndex[(memberMeta.typeInfo.type.classifier as KClass<*>).qualifiedName] ?: error("value codec not found")).schema()
    BOOLEAN -> context.base.booleanType()
    BYTE -> context.base.bytesType()
    DOUBLE -> context.base.doubleType()
    FLOAT -> context.base.floatType()
    INT -> context.base.intType()
    LONG -> context.base.longType()
    SHORT -> context.base.intType()
    STRING -> context.base.stringType()
  }

  private fun schemaName(type: TypeMeta) = "${type.rawType.qualifiedName}__${hashSha(type.typeInfo.signature()).subSequence(0, 8)}"

  internal class SchemaRegistryContext(val base: SchemaBuilder.TypeBuilder<Schema>,
                                       val metaManager: MetaManager,
                                       val valueCodecIndex: Map<String, AvroValueCodec<*>>,
                                       val definedTypes: MutableMap<String, Boolean> = mutableMapOf())

}
