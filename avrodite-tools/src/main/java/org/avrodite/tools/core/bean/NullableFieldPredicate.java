package org.avrodite.tools.core.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@FunctionalInterface
public interface NullableFieldPredicate {
  boolean test(Class<?> contextClass, Type contextType, Field field, TypeInfo typeInfo);
}
