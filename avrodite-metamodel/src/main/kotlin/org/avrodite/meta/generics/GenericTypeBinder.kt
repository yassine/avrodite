package org.avrodite.meta.generics

import org.avrodite.meta.type.TypeUtils.typeToString
import kotlin.reflect.*

class GenericTypeBinder(private val genericType: KType, private val bindings: Map<String, KType>) : KType by genericType {

  override val classifier: KClassifier?
    get() = genericType
      .takeIf { it.classifier is KTypeParameter }
      ?.let { bindings[(it.classifier as KTypeParameter).name]?.classifier }
      ?: genericType.classifier

  override val arguments: List<KTypeProjection>
    get() = genericType.arguments
      .map { arg ->
        arg.takeIf { it.type?.classifier is KTypeParameter }
          ?.let {
            KTypeProjection(
              KVariance.INVARIANT,
              bindings[(it.type?.classifier as KTypeParameter).name]
            )
          }
          ?: arg.type?.let { s ->
            KTypeProjection(
              arg.variance,
              GenericTypeBinder(s, bindings)
            )
          } ?: arg
      }

  override fun toString(): String
    = typeToString(this)

}
