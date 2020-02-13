package org.avrodite.api;

public interface Configurable<C extends Codec<?, I, O, S>, I extends InputByteBuffer, O extends OutputByteBuffer, S extends CodecStandard<I, O, C, ?>> {
  void configure(CodecManager<S, C> codecManager);
}
