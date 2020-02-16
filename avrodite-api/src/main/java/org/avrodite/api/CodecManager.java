package org.avrodite.api;

import java.lang.reflect.Type;

public interface CodecManager<S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> {
  <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Class<B> beanType);
  <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Type beanType);
}
