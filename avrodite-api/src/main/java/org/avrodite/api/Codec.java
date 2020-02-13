package org.avrodite.api;

import org.avrodite.api.error.CodecException;

public interface Codec<B, I extends InputByteBuffer, O extends OutputByteBuffer, S extends CodecStandard<I, O, ? extends Codec<?, I, O, S>, ?>> {

  byte[] encode(B bean) throws CodecException;
  void encode(B bean, O buffer) throws CodecException;
  void decode(B bean, byte[] data) throws CodecException;
  void decode(B bean, I buffer) throws CodecException;
  S standard();

}
