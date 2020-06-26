package org.avrodite.api

import kotlin.reflect.KClass
import kotlin.reflect.KType

interface CodecManager<I:Input, O:Output, out C:Codec<*, I, O, Format<I, O, C, V>, V>, V : ValueCodec<*, I, O, Format<I, O, C, V>, C>> {
  fun <B : Any> getCodec(clazz: KClass<B>) : C
  fun <B : Any> getCodec(type: KType) : C
}
