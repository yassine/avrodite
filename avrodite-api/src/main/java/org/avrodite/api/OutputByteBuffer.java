package org.avrodite.api;

import org.avrodite.api.error.CodecException;

public interface OutputByteBuffer {
  void writeArrayStart() throws CodecException;
  void writeArrayEnd() throws CodecException;
  void writeByte(byte b) throws CodecException;
  void writeBytes(byte[] bytes) throws CodecException;
  void writeString(String s) throws CodecException;
  void writeInt(int number) throws CodecException;
  void writeLong(long number) throws CodecException;
}
