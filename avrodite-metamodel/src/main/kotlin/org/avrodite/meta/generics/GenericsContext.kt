package org.avrodite.meta.generics

import org.avrodite.meta.type.TypeUtils.extractKClass
import kotlin.reflect.*
import kotlin.reflect.full.memberProperties

class GenericsContext constructor(private val base: KType) {

  private val clazz = extractKClass(base)
  val typeParameters = base.arguments.map { it.type!! }
  val genericRawParameters = clazz.typeParameters.foldIndexed(hashMapOf<String, KType>()) {
    index, map, cur -> map.also { map[cur.name] = typeParameters[index] }
  }

  fun resolveProp(prop: KProperty<*>) : KType?
    = clazz.memberProperties
        .filter { it.name == prop.name }
        .map { GenericTypeBinder(it.returnType, genericRawParameters) }
        .firstOrNull()
        ?: clazz.supertypes.asSequence()
            .map { GenericTypeBinder(it, genericRawParameters) }
            .map { GenericsContext(it) }
            .map { it.resolveProp(prop) }
            .filterNotNull()
            .firstOrNull()

  fun constructors() : List<KFunction<*>>
    = clazz.constructors
        .map {
          BoundKFunction(it, it.parameters.map { BoundKParameter(it, GenericTypeBinder(it.type, genericRawParameters)) })
        }

  fun resolve(contract: KType) : GenericsContext?
    = resolve(extractKClass(contract))

  fun resolve(contract: KClass<*>) : GenericsContext?
    = clazz.takeIf { it == contract }
      ?.let { GenericsContext(BoundKType(base, typeParameters)) }
      ?: clazz.supertypes.asSequence()
          .map { GenericsContext(GenericTypeBinder(it, genericRawParameters)).resolve(contract) }
          .filterNotNull()
          .firstOrNull()

  companion object {
    fun of(type: KType) = GenericsContext(type)
  }

}


