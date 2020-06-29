package org.avrodite.meta.type

import org.avrodite.meta.MetaScope
import org.avrodite.meta.type.TypeCategory.Companion.typeCategoryOf
import org.avrodite.meta.type.TypeUtils.typeToString
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

class TypeInfo(val type: KType, val metaScope: MetaScope? = null) {

  val category: TypeCategory = typeCategoryOf(this.metaScope, type.classifier as? KClass<*> ?: error("f") )
  val composite: TypeInfo?

  init {
    composite = when (category) {
      TypeCategory.ARRAY, TypeCategory.LIST, TypeCategory.SET
        -> type.arguments.asSequence()
            .map { it.type }
            .filterNotNull()
            .map { TypeInfo(it, metaScope) }
            .firstOrNull()
      TypeCategory.MAP
        -> type.arguments.asSequence()
              .map { it.type }
              .filter {
                type.arguments.size == 2
                  && type.arguments[0].type?.classifier is KClass<*>
                  && (type.arguments[0].type?.classifier as KClass<*>).qualifiedName == String::class.qualifiedName
              }
              .map { type.arguments[1].type }
              .filterNotNull()
              .map { TypeInfo(it, metaScope) }
              .firstOrNull()
      else
        -> null
    }
  }

  fun eraseNullability() : TypeInfo
    = TypeInfo(
      type.isMarkedNullable.takeIf { it }?.let { type.withNullability(false) }
                ?: type, metaScope = metaScope)

  /*
    This is being used by codecs sources, it is tied Kotlin for now
    TODO extract this to support other target languages signatures (core java)
   */
  fun signature(): String
    = when (category) {
        TypeCategory.MAP -> "kotlin.collections.Map<kotlin.String, ${composite!!.signature()}>"
        TypeCategory.ARRAY -> "Array<${composite!!.signature()}>"
        TypeCategory.LIST -> "kotlin.collections.List<${composite!!.signature()}>"
        TypeCategory.SET -> "kotlin.collections.Set<${composite!!.signature()}>"
        else -> {
          typeToString(type)
        }
      }

  fun simpleName() : String
    = (type.classifier as KClass<*>).simpleName!!

  fun packageName()
    = ((type.classifier as KClass<*>).qualifiedName!!).split(".")
        .let {
          it.filterIndexed { index, _ -> index != it.lastIndex }
        }.joinToString(".")

  fun isNative() : Boolean
    = category in arrayOf (
        TypeCategory.STRING,
        TypeCategory.BYTE,
        TypeCategory.SHORT,
        TypeCategory.INT,
        TypeCategory.LONG,
        TypeCategory.FLOAT,
        TypeCategory.DOUBLE,
        TypeCategory.BOOLEAN
      )

  fun isNullable() : Boolean
    = type.isMarkedNullable

}

