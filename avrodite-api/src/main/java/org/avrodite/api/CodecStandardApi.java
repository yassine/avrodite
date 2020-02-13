package org.avrodite.api;

public interface CodecStandardApi<I extends InputByteBuffer, O extends OutputByteBuffer, C, V> {
  Class<I> inputApi();
  Class<O> outputApi();
  Class<C> typeCodecApi();
  Class<V> valueCodecApi();
}
