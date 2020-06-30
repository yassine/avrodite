package org.avrodite.meta

import org.avrodite.meta.type.TypeCategory.*
import org.avrodite.meta.type.TypeCategory.Companion.typeCategoryOf
import org.avrodite.meta.type.TypeInfo
import org.avrodite.meta.type.TypeUtils.findMainConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class MetaScope(private val config: MetaScopeConfig) {

  fun isValueType(clazz: KClass<*>): Boolean = config.valueTypePredicate(clazz)

  fun isTypeOfInterest(clazz: KClass<*>): Boolean = isTypeOfInterest(TypeInfo(clazz.createType()))

  fun isTypeOfInterest(typeInfo: TypeInfo): Boolean = when (typeInfo.category) {
    VALUE, STRING, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN
      -> true
    MAP, LIST, ARRAY, SET
      -> typeInfo.composite?.let { isTypeOfInterest(it) } ?: false
    else
      -> isClassOfInterest(typeInfo.type, typeInfo.type.classifier as KClass<*>, config)
  }

  private fun isClassOfInterest(type: KType, clazz: KClass<*>, config: MetaScopeConfig): Boolean
      //ok if it's not excluded specifically (exclusions always take highest precedence)
    = config.excludedClasses.all { it != clazz }
        && (
          // ok if it's not a type (primitive, data-structure, value types etc.)
          typeCategoryOf(this, clazz) != TYPE
            || (
              // ok if it's within a package under the scope (and)
              config.packages.filter { clazz.qualifiedName?.startsWith(it) ?: false }.any()
              // (and) is constructible using types within the scope
              && findMainConstructor(this, type, clazz) != null
            )
        )

  companion object {

    fun of(init: Builder.() -> Unit): MetaScope = MetaScope(Builder().also { it.init() }.build())

    class MetaScopeConfig(val packages: Set<String>, val excludedClasses: Set<KClass<*>>, val valueTypePredicate: (KClass<*>) -> Boolean)

    class Builder {

      private val excludedClasses = mutableSetOf<KClass<*>>()
      private val packages = mutableSetOf<String>()
      private var valueTypePredicate: (KClass<*>) -> Boolean = { _ -> false }

      fun exclude(vararg types: KClass<*>): Builder = apply { excludedClasses.addAll(types) }
      fun include(vararg pkg: String): Builder = apply { packages.addAll(pkg) }
      fun include(pkg: String): Builder = apply { packages.add(pkg) }
      fun valueTypePredicate(predicate: (KClass<*>) -> Boolean) = also { valueTypePredicate = predicate }

      fun build(): MetaScopeConfig = MetaScopeConfig(packages, excludedClasses, valueTypePredicate)

    }

  }
}
