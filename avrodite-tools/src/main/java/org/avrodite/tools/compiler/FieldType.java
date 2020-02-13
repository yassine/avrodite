package org.avrodite.tools.compiler;

import org.avrodite.tools.template.TypeFingerprint;

public enum FieldType {
  BOOLEAN,
  INT,
  LONG,
  FLOAT,
  DOUBLE,
  SHORT,
  BYTE,
  STRING,
  UNION,
  TYPE,
  ARRAY,
  MAP;

  public static FieldType of(TypeFingerprint typeFingerprint) {
    switch (typeFingerprint) {
      case BOOLEAN:
        return BOOLEAN;
      case SHORT:
      case INT:
        return INT;
      case LONG:
        return LONG;
      case FLOAT:
        return FLOAT;
      case DOUBLE:
        return DOUBLE;
      case ITERABLE:
      case LIST:
      case SET:
      case QUEUE:
      case ARRAY:
        return ARRAY;
      case MAP:
        return MAP;
      case STRING:
        return STRING;
      default:
        return TYPE;
    }
  }
}
