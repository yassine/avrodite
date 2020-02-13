package org.avrodite.avro;

import org.apache.avro.Schema;
import org.avrodite.api.Codec;
import org.avrodite.api.error.CodecException;

public interface AvroCodec<B> extends Codec<B, AvroInputByteBuffer, AvroOutputByteBuffer, AvroStandard> {

  Schema getSchema();

  @Override
  byte[] encode(B bean) throws CodecException;

  @Override
  void decode(B bean, byte[] data) throws CodecException;

  @Override
  void decode(B bean, AvroInputByteBuffer buffer) throws CodecException;

  @Override
  void encode(B bean, AvroOutputByteBuffer buffer) throws CodecException;
}
