package org.avrodite.meta.introspect

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions

object Utils {
  fun findGetter(clazz: KClass<*>, prop: KProperty<*>): KFunction<*>? {
    val builder = StringBuilder()
    builder.append("get")
    builder.append(prop.name.subSequence(0, 1).map { it.toUpperCase() }[0])
    builder.append(prop.name.subSequence(1, prop.name.length))
    val getterName = "$builder"
    return clazz.memberFunctions
      .filter { it.name == getterName }
      .filter { it.parameters.size == 1 }
      .firstOrNull { it.returnType == prop.returnType }
  }

  fun findSetter(clazz: KClass<*>, prop: KProperty<*>): KFunction<*>? {
    val builder = StringBuilder()
    builder.append("set")
    builder.append(prop.name.subSequence(0, 1).map { it.toUpperCase() }[0])
    builder.append(prop.name.subSequence(1, prop.name.length))
    val setterName = "$builder"
    return clazz.memberFunctions.asSequence()
      .filter { it.name == setterName }
      .filter { it.parameters.size == 2 }
      .firstOrNull { it.parameters[1].type == prop.returnType }
  }
}
