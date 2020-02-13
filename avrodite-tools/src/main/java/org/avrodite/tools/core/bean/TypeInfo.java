package org.avrodite.tools.core.bean;

import java.lang.reflect.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ru.vyarus.java.generics.resolver.GenericsResolver;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

@Getter @RequiredArgsConstructor @Accessors(fluent = true) @EqualsAndHashCode(of = "signature")
public class TypeInfo {
  private final Type type;
  private final Class<?> rawType;
  private final String signature;

  public GenericsContext resolver(){
    return GenericsResolver.resolve(rawType).inlyingType(type);
  }

  public String toString(){
    return signature;
  }

}
