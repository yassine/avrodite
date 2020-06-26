package org.avrodite.api

import org.avrodite.error.AvroditeException
import kotlin.reflect.KClass

interface Input
interface Output

interface Codec<M : Any, in I:Input, in O:Output, out F:Format<I, O, Codec<*, I, O, F, V>, V>, out V : ValueCodec<*, I, O, F, Codec<*, I, O, F, V>>> {
  @Throws(AvroditeException::class)
  fun encode(m : M, output: O)
  @Throws(AvroditeException::class)
  fun decode(m : M, input: I): M
  @Throws(AvroditeException::class)
  fun decode(input: I): M
}

interface ValueCodec<V : Any, in I: Input, in O: Output, out F: Format<I, O, C, ValueCodec<*, I, O, F, C>>, out C: Codec<*, I, O, F, ValueCodec<*, I, O, F, C>>> {
  fun encode(output: O, value: V)
  fun decode(input: I): V
}

interface Format<in I:Input, in O:Output, out C:Codec<*, I, O, Format<I, O, C, V>, V>, out V : ValueCodec<*, I, O, Format<I, O, C, V>, C>> {
  fun api()     : FormatApi<I, O, C, V>
  fun name()    : String
  fun version() : String
}

interface FormatApi<in I : Any, in O : Any, out C : Any, out V : Any> {
  fun codec()  : KClass<out C>
  fun input()  : KClass<in I>
  fun output() : KClass<in O>
  fun value()  : KClass<out V>
}

;
