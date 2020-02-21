package org.avrodite.avro;

import static java.lang.Double.longBitsToDouble;
import static java.lang.Float.intBitsToFloat;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.avrodite.api.InputByteBuffer;

public class AvroInputByteBuffer implements InputByteBuffer {

  byte[] data;
  int cursor;
  int end;

  public AvroInputByteBuffer(byte[] data, int end) {
    this.data = data;
    this.cursor = 0;
    this.end = end;
  }

  public AvroInputByteBuffer(byte[] data) {
    this(data, data.length);
  }

  public void rewind() {
    cursor = 0;
  }

  @SuppressWarnings("DuplicatedCode") //duplication here yields performance
  public int readInt() throws AvroCodecException {
    ensureRemaining(1);
    int position = cursor;
    int result = 0;
    byte current;
    int shift = 0;
    do {
      current = data[position];
      result |= (current & 0x7F) << shift;
      if ( (current & 0x80) == 0 ) {
        cursor = position + 1;
        return (result >>> 1) ^ -(result & 1);
      }
      shift += 7;
      position++;
    } while (shift < 32);
    throw AvroCodecException.API.unexpectedNumber();
  }

  @SuppressWarnings("DuplicatedCode") //duplication here yields performance
  public long readLong() throws AvroCodecException {
    ensureRemaining(1);
    int position = cursor;
    long result = 0;
    byte current;
    int shift = 0;
    do {
      current = data[position];
      result |= (current & 0x7F) << shift;
      if ((current & 0x80) == 0) {
        cursor = position + 1;
        return (result >>> 1) ^ -(result & 1);
      }
      shift += 7;
      position++;
    } while (shift < 25);
    //need to cast to long
    do {
      current = data[position];
      result |= ((long) (current & 0x7F)) << shift;
      if ((current & 0x80) == 0) {
        cursor = position + 1;
        return (result >>> 1) ^ -(result & 1);
      }
      shift += 7;
      position++;
    } while (shift < 64);
    throw AvroCodecException.API.unexpectedNumber();
  }

  public double readDouble() throws AvroCodecException {
    ensureRemaining(8);
    long n = (((long) data[cursor++]) & 0xff)
      | ((((long) data[cursor++]) & 0xff) << 8)
      | ((((long) data[cursor++]) & 0xff) << 16)
      | ((((long) data[cursor++]) & 0xff) << 24)
      | ((((long) data[cursor++]) & 0xff) << 32)
      | ((((long) data[cursor++]) & 0xff) << 40)
      | ((((long) data[cursor++]) & 0xff) << 48)
      | ((((long) data[cursor++]) & 0xff) << 56);
    return longBitsToDouble(n);
  }

  public float readFloat() throws AvroCodecException {
    ensureRemaining(4);
    int n = ((data[cursor++]) & 0xff)
      | (((data[cursor++]) & 0xff) << 8)
      | ((((data[cursor++]) & 0xff) << 16)
      | ((data[cursor++]) & 0xff) << 24);
    return intBitsToFloat(n);
  }

  public String readString() throws AvroCodecException {
    int length = (int) readLong();
    if (cursor + length > data.length || length < 0) {
      throw AvroCodecException.API.endOfInput();
    }
    String result = new String(data, cursor, length, UTF_8);
    cursor += length;
    return result;
  }

  public void reset(byte[] newData) {
    cursor = 0;
    end = newData.length - 1;
    if (this.data.length < newData.length) {
      this.data = new byte[2 * newData.length];
    }
    arraycopy(newData, 0, this.data, 0, newData.length);
  }

  public int readArrayStart() throws AvroCodecException {
    //do no thing.
    return readInt();
  }

  public int readArrayEnd() throws AvroCodecException {
    ensureCurrent((byte) 0);
    cursor++;
    return 0;
  }

  private void ensureRemaining(int len) throws AvroCodecException {
    if (cursor + len > data.length) {
      throw AvroCodecException.API.endOfInput();
    }
  }

  private void ensureCurrent(byte b) throws AvroCodecException {
    if (end == cursor) {
      throw AvroCodecException.API.endOfInput();
    }
    if (data[cursor] != b) {
      throw AvroCodecException.API.unexpectedInput(b, data[cursor]);
    }
  }

}
