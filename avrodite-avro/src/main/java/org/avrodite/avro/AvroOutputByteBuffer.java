package org.avrodite.avro;

import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.avro.io.BinaryData.encodeDouble;
import static org.apache.avro.io.BinaryData.encodeFloat;
import static org.apache.avro.io.BinaryData.encodeInt;
import static org.apache.avro.io.BinaryData.encodeLong;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.avrodite.api.OutputByteBuffer;

@SuppressWarnings("unused")
@Accessors(fluent = true)
public class AvroOutputByteBuffer implements OutputByteBuffer {

  @Getter(AccessLevel.PACKAGE)
  byte[] data;
  int cursor;

  public AvroOutputByteBuffer(byte[] data) {
    this.data = data;
    this.cursor = 0;
  }

  public void reset() {
    this.cursor = 0;
  }

  public byte[] toByteArray() {
    byte[] result = new byte[cursor];
    arraycopy(data, 0, result, 0, cursor);
    return result;
  }

  public void ensure(int requirement) {
    if (cursor + requirement + 1 > data.length) {
      byte[] larger = new byte[2 * data.length];
      arraycopy(data, 0, larger, 0, cursor);
      data = larger;
    }
  }

  public void ensureLong() {
    ensure(9);
  }

  public void writeArrayStart() {
    //do no thing.
  }

  public void writeArrayEnd() {
    writeZero();
  }

  private void writeZero() {
    ensure(1);
    data[cursor++] = 0;
  }

  public void writeByte(byte b) {
    ensure(1);
    data[cursor++] = b;
  }

  public void writeBytes(byte[] bytes) {
    ensure(bytes.length);
    arraycopy(bytes, 0, data, cursor, bytes.length);
    cursor += bytes.length;
  }

  public void writeString(String s) {
    byte[] bytes = s.getBytes(UTF_8);
    ensure(bytes.length + 5);
    writeLongNoCheck(bytes.length);
    arraycopy(bytes, 0, data, cursor, bytes.length);
    cursor += bytes.length;
  }

  public void writeInt(int number) {
    ensure(5);
    writeIntNoCheck(number);
  }

  @SuppressWarnings("DuplicatedCode")
  public void writeIntNoCheck(int number) {
    cursor += encodeInt(number, data, cursor);
  }

  public void writeLong(long number) {
    ensure(9);
    writeLongNoCheck(number);
  }

  @SuppressWarnings("DuplicatedCode")
  public void writeLongNoCheck(long number) {
    ensure(9);
    cursor += encodeLong(number, data, cursor);
  }

  public void writeFloat(float number) {
    ensure(8);
    cursor += encodeFloat(number, data, cursor);
  }

  public void writeDouble(double number) {
    ensure(8);
    cursor += encodeDouble(number, data, cursor);
  }

}
