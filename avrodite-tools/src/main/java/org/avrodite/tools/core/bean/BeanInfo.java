package org.avrodite.tools.core.bean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode(of = {"signature"})
@ToString(of = "signature")
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanInfo {
  private final List<FieldInfo> fields;
  private final Type target;
  private final Class<?> targetRaw;
  private final Set<Class<?>> dependencies;
  private final String signature;
}
