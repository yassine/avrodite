package org.avrodite.meta.type

import org.avrodite.meta.MetaScope
import kotlin.reflect.KClass

enum class TypeCategory {
  STRING,
  //tuple
  ARRAY,
  LIST,
  SET,
  //number
  BYTE,
  DOUBLE,
  FLOAT,
  INT,
  LONG,
  SHORT,
  BOOLEAN,
  MAP,
  VALUE,
  TYPE;

  companion object {
    fun typeCategoryOf(metaScope: MetaScope?, classifier: KClass<*>): TypeCategory {
      if(metaScope?.isValueType(classifier) == true)
        return VALUE
      return when (classifier.qualifiedName) {
        Array<Any>::class.qualifiedName, IntArray::class.qualifiedName, FloatArray::class.qualifiedName,
        LongArray::class.qualifiedName, DoubleArray::class.qualifiedName, ShortArray::class.qualifiedName,
        BooleanArray::class.qualifiedName
          -> ARRAY
        Byte::class.qualifiedName -> BYTE
        Boolean::class.qualifiedName -> BOOLEAN
        Double::class.qualifiedName -> DOUBLE
        Float::class.qualifiedName  -> FLOAT
        Int::class.qualifiedName  -> INT
        List::class.qualifiedName -> LIST
        Long::class.qualifiedName -> LONG
        Map::class.qualifiedName  -> MAP
        Set::class.qualifiedName  -> SET
        Short::class.qualifiedName  -> SHORT
        String::class.qualifiedName -> STRING
        else -> TYPE
      }
    }



  }
}


