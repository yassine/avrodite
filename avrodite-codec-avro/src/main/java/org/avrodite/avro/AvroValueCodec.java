package org.avrodite.avro;

import org.apache.avro.Schema;
import org.avrodite.api.ValueCodec;
import org.avrodite.api.error.CodecException;

public interface AvroValueCodec<V> extends ValueCodec<V, AvroInputByteBuffer, AvroOutputByteBuffer, AvroStandard> {
  V decode(AvroInputByteBuffer buffer) throws CodecException;
  void encode(V value, AvroOutputByteBuffer buffer) throws CodecException;
  Schema schema();
}
