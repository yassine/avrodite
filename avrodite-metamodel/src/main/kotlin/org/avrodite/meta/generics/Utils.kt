package org.avrodite.meta.generics

import org.avrodite.meta.error.AvroditeMetaError
import kotlin.reflect.*

object Utils {

  // can 'type' cast as 'target'
  fun isAssignable(target: KType, type: KType): Boolean
    = when (target.classifier) {
      is KClass<*> -> when (type.classifier) {
        is KClass<*>
          -> (type.classifier == target.classifier) || (
              isAssignable(target.classifier as KClass<*>, type.classifier as KClass<*>)
                && ( GenericsContext.of(type).resolve(target.classifier as KClass<*>)?.typeParameters
                      ?.let { typeResolvedGenerics ->
                        val targetArgsTypes = target.arguments.map { it.type!! }
                        val targetArgsVariance = (target.classifier as KClass<*>).typeParameters.map { it.variance }
                        typeResolvedGenerics.mapIndexed {
                          index, type -> when(targetArgsVariance[index]) {
                            KVariance.IN -> isAssignable(type, targetArgsTypes[index])
                            KVariance.OUT -> isAssignable(targetArgsTypes[index], type)
                            KVariance.INVARIANT -> type.classifier == targetArgsTypes[index].classifier
                          }
                        }.all { it }
                      }
                      ?: false
                   )
              )
        is KTypeParameter
          -> (type.classifier as KTypeParameter).upperBounds.any { isAssignable(target.classifier as KClass<*>, it) }
        else -> throw AvroditeMetaError.unsupportedType(type.classifier)
      }

      is KTypeParameter -> when (type.classifier) {
        is KClass<*>
          -> (target.classifier as KTypeParameter).upperBounds.any { isAssignable(it, type.classifier as KClass<*>) }
        is KTypeParameter
          -> (target.classifier as KTypeParameter).upperBounds.any { u -> (type.classifier as KTypeParameter).upperBounds.any { isAssignable(u, it) } }
        else -> throw AvroditeMetaError.unsupportedType(type.classifier)
      }

      else -> throw AvroditeMetaError.unsupportedType(type.classifier)
    }

  fun isAssignable(target: KType, type: KClass<*>): Boolean
    = when (target.classifier) {
      is KClass<*>
        -> isAssignable(target.classifier as KClass<*>, type)
      is KTypeParameter
        -> (target.classifier as KTypeParameter).upperBounds.any { isAssignable(it.classifier as KClass<*>, type) }
      else -> throw AvroditeMetaError.unsupportedType(target.classifier)
    }

  fun isAssignable(target: KClass<*>, type: KClass<*>): Boolean
    = (target == type).takeIf { it }
        ?: type.supertypes.takeIf { it.isNotEmpty() }?.any { isAssignable(target, it) } ?: false

  fun isAssignable(target: KClass<*>, type: KType): Boolean
    = when (type.classifier) {
      is KClass<*> -> isAssignable(target, type.classifier as KClass<*>)
      is KTypeParameter -> (type.classifier as KTypeParameter).upperBounds.any { isAssignable(target, it) }
      else -> throw AvroditeMetaError.unsupportedType(type.classifier)
    }

}
