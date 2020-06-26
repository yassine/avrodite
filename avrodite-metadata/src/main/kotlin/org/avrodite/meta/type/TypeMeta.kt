package org.avrodite.meta.type

import org.avrodite.meta.MetaScope
import org.avrodite.meta.introspect.Utils.findGetter
import org.avrodite.meta.introspect.Utils.findSetter
import org.avrodite.meta.type.TypeUtils.createType
import org.avrodite.meta.type.TypeUtils.findMainConstructor
import org.avrodite.meta.type.TypeUtils.propsOfInterest
import java.util.UUID.randomUUID
import kotlin.reflect.*
import kotlin.reflect.jvm.javaType

open class MemberMeta(val typeInfo: TypeInfo, val name: String)

class ParamMeta(typeInfo: TypeInfo, val parameter: KParameter)
  : MemberMeta(typeInfo, parameter.name ?: "unknown"+ randomUUID())
class PropMeta(typeInfo: TypeInfo, val field: KProperty<*>, val getter: KFunction<*>?, val setter: KFunction<*>?)
  : MemberMeta(typeInfo, field.name)
{

  fun target() : TypeInfo
    = recursiveTarget(typeInfo)

  private fun recursiveTarget(typeInfo: TypeInfo) : TypeInfo
    = typeInfo.composite
        ?.let { recursiveTarget(it) }
        ?: typeInfo

}

class TypeMeta(val type: KType, val scope: MetaScope) {

  constructor(clazz: KClass<*>, metaScope: MetaScope) : this(createType(clazz), metaScope)

  val constructorParams: List<ParamMeta>
  val props: List<PropMeta>
  val rawType: KClass<*> = type.classifier as KClass<*>
  val typeInfo: TypeInfo = TypeInfo(type, scope)

  init {
    constructorParams = findMainConstructor(scope, type, rawType)?.parameters
                          ?.map { ParamMeta(TypeInfo(it.type, scope), it) }
                          ?: error("No constructor found for type '${type}'")
    props = propsOfInterest(rawType, type, scope).asSequence()
      .filter { prop -> constructorParams.map { it.name }.all { prop.name != it } }
      .filter { it.writable }
      .map { PropMeta(TypeInfo(it.type, scope), it.prop, findGetter(it.rawType, it.prop), findSetter(it.rawType, it.prop)) }
      .toList()
  }

}
