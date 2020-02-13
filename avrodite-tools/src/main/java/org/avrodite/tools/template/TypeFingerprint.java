package org.avrodite.tools.template;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.avrodite.tools.core.bean.Utils;

public enum TypeFingerprint {
  STRING,
  //tuple
  ARRAY,
  ITERABLE,
  LIST,
  QUEUE,
  SET,
  //number
  DOUBLE,
  FLOAT,
  INT,
  LONG,
  SHORT,
  BOOLEAN,
  MAP,
  TYPE;

  public static TypeFingerprint of(Type type) {
    if (type instanceof GenericArrayType) {
      return ARRAY;
    }
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;
      Class<?> rawType = (Class<?>) parameterizedType.getRawType();
      if (Iterable.class.isAssignableFrom(rawType)) {
        return iterableFingerprintOf(rawType);
      } else if (Map.class.isAssignableFrom(rawType)) {
        return MAP;
      }
    }
    if (type instanceof Class) {
      Class<?> rawType = (Class<?>) type;
      if (rawType.equals(String.class)) {
        return STRING;
      } else if (rawType.equals(boolean.class) || rawType.equals(Boolean.class)) {
        return BOOLEAN;
      } else if (rawType.isArray()) {
        return ARRAY;
      } else if (Utils.NUMERIC_TYPES.stream().anyMatch(rawType::equals)) {
        return numberFingerprintOf(rawType);
      } else if (Map.class.isAssignableFrom(rawType)) {
        return MAP;
      }
    }
    return TYPE;
  }

  private static TypeFingerprint iterableFingerprintOf(Class<?> rawType) {
    if (List.class.equals(rawType)) {
      return LIST;
    } else if (Queue.class.equals(rawType)) {
      return QUEUE;
    } else if (Set.class.equals(rawType)) {
      return SET;
    }
    return ITERABLE;
  }

  private static TypeFingerprint numberFingerprintOf(Class<?> rawType) {
    if (rawType.equals(long.class) || rawType.equals(Long.class)) {
      return LONG;
    }
    if (rawType.equals(double.class) || rawType.equals(Double.class)) {
      return DOUBLE;
    }
    if (rawType.equals(float.class) || rawType.equals(Float.class)) {
      return FLOAT;
    }
    if (rawType.equals(short.class) || rawType.equals(Short.class)) {
      return SHORT;
    }
    return INT;
  }
}
