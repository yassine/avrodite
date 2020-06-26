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
        .fold(hashMapOf<String, KType>()) { map, prop -> map.apply { this[prop.name] = GenericsContext.of(type).resolveProp(prop)!! } }
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

  fun createType(clazz: KClass<*>): KType = KTypeSupport(clazz)

  fun extractKClass(type: KType) : KClass<*>
    = type.classifier
    ?.takeIf { it is KClass<*> } ?.let { it as KClass<*> }
    ?: extractKClass( (type.classifier as KTypeParameter).upperBounds[0] )

}

internal class KTypeSupport(private val clazz: KClass<*>) : KType {
  override val annotations: List<Annotation>
    get() = clazz.annotations
  override val arguments: List<KTypeProjection>
    get() = clazz.typeParameters.map { parameter ->
      KTypeProjection(parameter.variance, parameter.upperBounds.takeIf { it.isNotEmpty() }?.let { it[0] })
    }
  override val classifier: KClassifier?
    get() = clazz
  override val isMarkedNullable: Boolean
    get() = false
  override fun toString(): String
    = clazz.qualifiedName!!
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
