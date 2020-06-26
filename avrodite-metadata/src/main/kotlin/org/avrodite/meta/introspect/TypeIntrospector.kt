package org.avrodite.meta.introspect

import org.avrodite.meta.type.TypeUtils
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class TypeIntrospector(val type: KType, val rawType: KClass<*>) {

  constructor(rawType: KClass<*>) : this(TypeUtils.createType(rawType), rawType)

  private val getterPrefix = "get"
  private val setterPrefix = "set"

  fun findProps(): List<PropIntrospectInfo> {
    val props = mutableListOf<PropContext>()
    val propsIndex: HashMap<String, PropContext> = hashMapOf()
    val propIntrospectIndex: HashMap<String, PropIntrospectInfo> = hashMapOf()
    recursiveProps(type, rawType, props, propsIndex)

    //discover fields via kotlin reflect
    props.filter { it.prop.getter.visibility == KVisibility.PUBLIC }
      .filter { it.prop.returnType.classifier is KClass<*> }
      .forEach {
        PropIntrospectInfo(it.prop.name, it.prop, it.prop.returnType, it.prop.returnType.classifier as KClass<*>, it.owner, it.ownerType, it.prop is KMutableProperty)
          .apply {
            propIntrospectIndex[name] = this
          }
      }
    //discover fields via JavaBeans get/set convention (this adds better support for java classes)
    val getterMap = getterMap(propsIndex)
    val setterMap = setterMap(propsIndex)
    getterMap
      .filter { !propIntrospectIndex.containsKey(it.key) }
      .forEach { entry ->
        getterMap[entry.key]
          ?.takeIf { it.returnType.classifier is KClass<*> }
          ?.also { getter ->
            propsIndex[entry.key]?.let {
              PropIntrospectInfo(it.prop.name, it.prop, getter.returnType, getter.returnType.classifier as KClass<*>, it.owner, it.ownerType, setterMap.containsKey(it.prop.name))
            }?.apply { propIntrospectIndex[name] = this }
          }
      }

    return props.mapNotNull { propIntrospectIndex[it.prop.name] }
  }

  private fun getterMap(propsIndex: HashMap<String, PropContext>): Map<String, KFunction<*>> {
    return rawType.memberFunctions
      .asSequence()
      .filter { it.parameters.size == 1 }
      .filter { it.name.startsWith(getterPrefix, true) }
      .filter { propsIndex.containsKey(extractPropName(it)) }
      .fold(hashMapOf(), { map, current -> map.also { map[extractPropName(current)] = current } })
  }

  private fun setterMap(propsIndex: HashMap<String, PropContext>): Map<String, KFunction<*>> {
    return rawType.memberFunctions
      .asSequence()
      .filter { it.parameters.size == 2 }
      .filter { it.name.startsWith(setterPrefix, true) }
      .filter { propsIndex.containsKey(extractPropName(it)) }
      .fold(hashMapOf(), { map, current -> map.also { map[extractPropName(current)] = current } })
  }

}

private class PropContext(
  val prop: KProperty<*>,
  val owner: KClass<*>,
  val ownerType: KType
)

private fun recursiveProps(type: KType, rawType: KClass<*>, props: MutableList<PropContext>, propsIndex: HashMap<String, PropContext>) {
  rawType.supertypes
    .forEach {
      recursiveProps(it, it.classifier as KClass<*>, props, propsIndex)
    }
  rawType.memberProperties
    .map { PropContext(it, rawType, type) }
    .forEach { propContext ->
      propsIndex[propContext.prop.name] = propContext
      props.indexOfFirst { it.prop.name == propContext.prop.name }
        .takeIf { it > -1 }
        ?.apply { props[this] = propContext }
        ?: props.add(propContext)
    }
}

private fun extractPropName(function: KFunction<*>): String {
  return function.name.substring(3, 4).toLowerCase() + function.name.substring(4, function.name.length)
}
