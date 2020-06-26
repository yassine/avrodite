package org.avrodite.avro.io

import org.avrodite.avro.AvroOutput

class ByteArrayAvroOutput(private var data: ByteArray) : AvroOutput {
  var cursor: Int = 0
    private set

  private fun ensure(remaining: Int){
    if (cursor + remaining > data.size + 1) {
      data = data.copyOf( 2 * data.size)
    }
  }

  override fun writeInt(num: Int) {
    ensure(5)
    var encodedNum = num shl 1 xor (num shr 31)
    var position = cursor
    if (encodedNum and 0x7F.inv() != 0) {
      data[position++] = (encodedNum or 0x80).toByte()
      encodedNum = encodedNum ushr 7
      if (encodedNum > 0x7f) {
        data[position++] = (encodedNum or 0x80).toByte()
        encodedNum = encodedNum ushr 7
        if (encodedNum > 0x7f) {
          data[position++] = (encodedNum or 0x80).toByte()
          encodedNum = encodedNum ushr 7
          if (encodedNum > 0x7f) {
            data[position++] = (encodedNum or 0x80).toByte()
            encodedNum = encodedNum ushr 7
          }
        }
      }
    }
    data[position++] = encodedNum.toByte()
    cursor = position
  }

  override fun writeLong(num: Long) {
    ensure(9)
    var encodedNum : Long = num shl 1 xor (num shr 63)
    var position = cursor
    if (encodedNum and 0x7FL.inv() != 0L) {
      data[position++] = (encodedNum or 0x80).toByte()
      encodedNum = encodedNum ushr 7
      if (encodedNum > 0x7f) {
        data[position++] = (encodedNum or 0x80).toByte()
        encodedNum = encodedNum ushr 7
        if (encodedNum > 0x7f) {
          data[position++] = (encodedNum or 0x80).toByte()
          encodedNum = encodedNum ushr 7
          if (encodedNum > 0x7f) {
            data[position++] = (encodedNum or 0x80).toByte()
            encodedNum = encodedNum ushr 7
            if (encodedNum > 0x7f) {
              data[position++] = (encodedNum or 0x80).toByte()
              encodedNum = encodedNum ushr 7
              if (encodedNum > 0x7f) {
                data[position++] = (encodedNum or 0x80).toByte()
                encodedNum = encodedNum ushr 7
                if (encodedNum > 0x7f) {
                  data[position++] = (encodedNum or 0x80).toByte()
                  encodedNum = encodedNum ushr 7
                  if (encodedNum > 0x7f) {
                    data[position++] = (encodedNum or 0x80).toByte()
                    encodedNum = encodedNum ushr 7
                    if (encodedNum > 0x7f) {
                      data[position++] = (encodedNum or 0x80).toByte()
                      encodedNum = encodedNum ushr 7
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    data[position++] = encodedNum.toByte()
    cursor = position
  }

  override fun writeDouble(num: Double) {
    ensure(8)
    val pos = cursor
    val l = num.toRawBits()
    var p1 = l.toInt()
    var p2 = (l ushr 32).toInt()
    data[pos] = p1.toByte()
    data[pos + 4] = p2.toByte()
    p1 = p1 ushr 8
    p2 = p2 ushr 8
    data[pos + 1] = p1.toByte()
    data[pos + 5] = p2.toByte()
    p1 = p1 ushr 8
    p2 = p2 ushr 8
    data[pos + 2] = p1.toByte()
    data[pos + 6] = p2.toByte()
    p1 = p1 ushr 8
    p2 = p2 ushr 8
    data[pos + 3] = p1.toByte()
    data[pos + 7] = p2.toByte()
    cursor += 8
  }

  override fun writeFloat(num: Float) {
    ensure(4)
    var pos = cursor
    var l = num.toRawBits()
    data[pos++] = l.toByte()
    l = l ushr 8
    data[pos++] = l.toByte()
    l = l ushr 8
    data[pos++] = l.toByte()
    l = l ushr 8
    data[pos] = l.toByte()
    cursor += 4
  }

  override fun writeString(string: String) {
    val bytes = string.toByteArray()
    ensure(bytes.size + 6)
    writeLong(bytes.size.toLong())
    bytes.copyInto(data, cursor)
    cursor += bytes.size
  }

  override fun writeByte(byte: Byte) {
    ensure(1)
    data[cursor++] = byte
  }

  fun rewind() {
    cursor = 0
  }

  fun toByteArray() : ByteArray{
    val copy = ByteArray(cursor)
    for(index in copy.indices) {
      copy[index] = data[index]
    }
    return copy
  }

}
