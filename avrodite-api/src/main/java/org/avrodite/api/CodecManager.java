package org.avrodite.api;

public interface CodecManager<S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> {
  <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Class<B> beanClass);
  <B, U extends Codec<B, ?, ?, S>> U getBeanCodecAs(Class<B> beanClass, Class<? super U> codecImplementation);
}
