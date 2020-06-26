package org.avrodite.avro

import org.apache.avro.Schema
import org.avrodite.api.*
import org.avrodite.avro.error.AvroditeAvroException.AvroditeAvroIOException
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal const val AVRO_NAME: String = "avro"
internal const val AVRO_VERSION: String = "1.9.0"

interface AvroCodec<M : Any> : Codec<M, AvroInput, AvroOutput, AvroFormat, AvroValueCodec<*>> {
  fun schema(): Schema
}

interface AvroValueCodec<V : Any> : ValueCodec<V, AvroInput, AvroOutput, AvroFormat, AvroCodec<*>> {
  fun schema(): Schema
}

object AvroFormat : Format<AvroInput, AvroOutput, AvroCodec<*>, AvroValueCodec<*>> {
  override fun api(): FormatApi<AvroInput, AvroOutput, AvroCodec<*>, AvroValueCodec<*>> = AvroFormatApi
  override fun name(): String    = AVRO_NAME
  override fun version(): String = AVRO_VERSION
}

interface AvroCodecManager : CodecManager<AvroInput, AvroOutput, AvroCodec<*>, AvroValueCodec<*>> {
  override fun <B : Any> getCodec(clazz: KClass<B>): AvroCodec<B>
  override fun <B : Any> getCodec(type: KType): AvroCodec<B>
}

interface AvroInput : Input {
  @Throws(AvroditeAvroIOException::class)
  fun readInt(): Int

  @Throws(AvroditeAvroIOException::class)
  fun readLong(): Long

  @Throws(AvroditeAvroIOException::class)
  fun readDouble(): Double

  @Throws(AvroditeAvroIOException::class)
  fun readFloat(): Float

  @Throws(AvroditeAvroIOException::class)
  fun readString(): String

  @Throws(AvroditeAvroIOException::class)
  fun readByte(): Byte
}

interface AvroOutput : Output {
  @Throws(AvroditeAvroIOException::class)
  fun writeInt(num: Int)

  @Throws(AvroditeAvroIOException::class)
  fun writeLong(num: Long)

  @Throws(AvroditeAvroIOException::class)
  fun writeDouble(num: Double)

  @Throws(AvroditeAvroIOException::class)
  fun writeFloat(num: Float)

  @Throws(AvroditeAvroIOException::class)
  fun writeString(string: String)

  @Throws(AvroditeAvroIOException::class)
  fun writeByte(byte: Byte)
}

object AvroFormatApi : FormatApi<AvroInput, AvroOutput, AvroCodec<*>, AvroValueCodec<*>> {
  override fun codec()  = AvroCodec::class
  override fun value()  = AvroValueCodec::class
  override fun input()  = AvroInput::class
  override fun output() = AvroOutput::class
}
