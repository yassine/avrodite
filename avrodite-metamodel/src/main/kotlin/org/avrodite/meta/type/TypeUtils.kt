package org.avrodite.meta.type

import org.avrodite.meta.MetaScope
import org.avrodite.meta.generics.GenericsContext
import org.avrodite.meta.generics.Utils.isAssignable
import org.avrodite.meta.introspect.PropIntrospectInfo
import org.avrodite.meta.introspect.TypeIntrospector
import kotlin.reflect.*
import kotlin.reflect.full.*
import mu.KotlinLogging

object TypeUtils {

  @JvmStatic
  private val logger = KotlinLogging.logger{}

  internal fun propsOfInterest(clazz: KClass<*>, type: KType, scope: MetaScope) : List<PropIntrospectInfo>
    = TypeIntrospector(type, clazz).findProps()
        .filter { scope.isTypeOfInterest(TypeInfo(it.type, scope)) }

  fun <T : Any> findMainConstructor(metaScope: MetaScope, type: KType, clazz: KClass<T>): KFunction<T>?
    = clazz.memberProperties
        .filter { it !is KMutableProperty<*> }
        .associateBy ({ it.name }, { GenericsContext.of(type).resolveProp(it)!! })
        .let { map ->
          findMatchingAllNamesAndTypes(metaScope, map, type)
            ?: findMatchingMaxNamesAndTypes(metaScope, map, type)
            ?: findMatching<T>(metaScope, map, type).firstOrNull().also {
              logger.debug("couldn't find consutructor for $clazz : \n$map")
            }
        }

  fun typeToString(type: KType): String
    = type.takeIf { type.classifier is KClass<*> }
        ?.let { (type.classifier as KClass<*>) }
        ?.let { it ->
          it.qualifiedName + ( type.arguments
            .takeIf { it.isNotEmpty() }
            ?.let { arg ->
              arg.map { it.type }.joinToString(", ") { it?.let { typeToString(it) } ?: "*" }.let { "<$it>" }
            }
            ?: "" )
      }?.let { sig ->
        type.isMarkedNullable.takeIf { it }?.let { "${sig}?" } ?: sig
      } ?: type.toString()

  fun extractKClass(type: KType) : KClass<*>
    = type.classifier
    ?.takeIf { it is KClass<*> } ?.let { it as KClass<*> }
    ?: extractKClass( (type.classifier as KTypeParameter).upperBounds[0] )

}

private fun <T : Any> findMatchingAllNamesAndTypes(metaScope: MetaScope, index: Map<String, KType>,
                                                   type: KType): KFunction<T>?
  = findMatching<T>(metaScope, index, type)
    .filter { constructor ->
      index.keys.all { constructor.parameters.map(KParameter::name).contains(it) }
    }
    .firstOrNull()

private fun <T : Any> findMatchingMaxNamesAndTypes(metaScope: MetaScope, indexNameByType: Map<String, KType>,
                                                   type: KType): KFunction<T>?
  = findMatching<T>(metaScope, indexNameByType, type).lastOrNull()

private fun <T : Any> findMatching(metaScope: MetaScope, indexNameByType: Map<String, KType>,
                                   type: KType): Sequence<KFunction<T>>
  = GenericsContext.of(type).constructors().asSequence()
      .filter { constructor -> constructor.parameters.all { it.type.classifier is KClass<*> } }
      .map {
        @Suppress("UNCHECKED_CAST")
        it as KFunction<T>
      }
      .filter { constructor ->
        constructor.parameters
          .map { TypeInfo(it.type, metaScope) }
          .all(metaScope::isTypeOfInterest)
      }
      .filter { constructor ->
        constructor.parameters.all { param ->
          ( indexNameByType.containsKey(param.name) && indexNameByType[param.name]?.let { isAssignable(param.type, it) } ?: false )
            || indexNameByType.isEmpty()
        }
      }
      .sortedBy { it.parameters.size }
