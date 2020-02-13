package org.avrodite.api;

public interface CodecStandard<I extends InputByteBuffer, O extends OutputByteBuffer, C extends Codec<?, I, O, ? extends CodecStandard<I, O, C, V>>, V extends ValueCodec<?, I, O, ? extends CodecStandard<I, O, C, V>>> {
  String name();
  String version();
  CodecStandardApi<I, O, C, V> api();
}
