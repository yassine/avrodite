package org.avrodite.meta.generics

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection

internal class BoundKParameter(private val delegate: KParameter, private val boundType: KType?) : KParameter by delegate {
  override val type: KType
    get() = boundType ?: delegate.type
}

internal class BoundKFunction<R>(private val delegate: KFunction<R>, private val boundParams: List<KParameter>) : KFunction<R> by delegate {
  override val parameters: List<KParameter>
    get() = boundParams
}

internal class BoundKType(private val delegate: KType, private val boundParams: List<KType>?) : KType by delegate {
  override val arguments: List<KTypeProjection>
    get() = boundParams
              ?.let { params -> delegate.arguments.mapIndexed { i, it ->  KTypeProjection(it.variance, params[i]) } }
              ?: delegate.arguments
}
