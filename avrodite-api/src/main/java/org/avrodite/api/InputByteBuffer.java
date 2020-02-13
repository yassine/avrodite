package org.avrodite.api;

import org.avrodite.api.error.CodecException;

public interface InputByteBuffer {
  int readInt() throws CodecException;
  long readLong() throws CodecException;
  double readDouble() throws CodecException;
  float readFloat() throws CodecException;
  String readString() throws CodecException;
  int readArrayStart() throws CodecException;
  int readArrayEnd() throws CodecException;
}
