package org.avrodite.avro.io

import org.avrodite.avro.AvroInput
import org.avrodite.avro.error.AvroditeAvroException

class ByteArrayAvroInput(private var data : ByteArray, private var end: Int) : AvroInput{

  constructor(data : ByteArray) : this(data, data.size)

  private var cursor: Int = 0

  override fun readInt(): Int {
    try {
      var position = cursor
      var result = 0
      var current: Byte
      var shift = 0
      do {
        current = data[position++]
        result = result or (current.toInt() and 0x7F shl shift)
        if ((current.toInt() and 0x80) == 0) {
          cursor = position
          return result ushr 1 xor -(result and 1)
        }
        shift += 7
      } while (shift < 32)
      throw AvroditeAvroException.unexpectedNumber()
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  override fun readLong(): Long {
    try {
      var position = cursor
      var result : Long = 0
      var current: Int
      var shift = 0
      do {
        current = data[position++].toInt()
        result = result or (current.toLong() and 0x7F shl shift)
        if ((current and 0x80) == 0) {
          cursor = position
          return result ushr 1 xor -(result and 1)
        }
        shift += 7
      } while (shift < 64)
      throw AvroditeAvroException.unexpectedNumber()
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  override fun readDouble(): Double {
    try {
      val n = ((data[cursor++].toLong() and 0xff)
                or (data[cursor++].toLong() and 0xff shl 8)
                or (data[cursor++].toLong() and 0xff shl 16)
                or (data[cursor++].toLong() and 0xff shl 24)
                or (data[cursor++].toLong() and 0xff shl 32)
                or (data[cursor++].toLong() and 0xff shl 40)
                or (data[cursor++].toLong() and 0xff shl 48)
                or (data[cursor++].toLong() and 0xff shl 56))
      return Double.fromBits(n)
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  override fun readFloat(): Float {
    try {
      val n: Int = ( (data[cursor++].toInt() and 0xff)
                     or ((data[cursor++].toInt() and 0xff) shl 8)
                     or ((data[cursor++].toInt() and 0xff) shl 16)
                     or ((data[cursor++].toInt() and 0xff) shl 24) )
      return Float.fromBits(n)
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  override fun readString(): String {
    try {
      val length = readLong().toInt()
      val result = String(data, cursor, length, Charsets.UTF_8)
      cursor += length
      return result
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  override fun readByte(): Byte {
    try {
      return data[cursor++]
    } catch (e : IndexOutOfBoundsException) {
      throw AvroditeAvroException.endOfInput()
    }
  }

  fun rewind() = also {
    cursor = 0
  }

  fun reset(data: ByteArray)
    = also {
        cursor = 0
        this.data = data
      }

}
