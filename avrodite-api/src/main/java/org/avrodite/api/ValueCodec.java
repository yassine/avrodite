package org.avrodite.api;

import org.avrodite.api.error.CodecException;

public interface ValueCodec<V, I extends InputByteBuffer, O extends OutputByteBuffer, S extends CodecStandard<I, O, ?, ? extends ValueCodec<? , I, O, S>>> {
  V decode(I buffer) throws CodecException;
  void encode(V value, O buffer) throws CodecException;
  S standard();
}
