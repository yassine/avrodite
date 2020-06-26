package org.avrodite.meta.type

import org.avrodite.meta.MetaScope
import kotlin.reflect.KClass

class TypeDiscoverer(val scope: MetaScope) {

  fun discoverTypes(classes: Set<KClass<*>>) : Set<TypeMeta>{
    val map: MutableMap<String, TypeMeta?> = hashMapOf()
    classes.map { TypeMeta(it, scope) }
      .filter { !map.containsKey(it.typeInfo.signature()) }
      .forEach {
        map[it.typeInfo.signature()] = it
        discoverTypes(it, map)
      }
    return map.values.filterNotNull().toSet()
  }

  private fun discoverTypes(typeMeta: TypeMeta, map: MutableMap<String, TypeMeta?>) {
    ( typeMeta.props.map { it.typeInfo } + typeMeta.constructorParams.map { it.typeInfo } )
        .asSequence()
        .map { extractTargetType(it).eraseNullability() }
        .filter { it.category == TypeCategory.TYPE }
        .filter { !map.containsKey(it.signature()) }
        .map {
          TypeMeta(it.type, scope).apply {
            map[typeInfo.signature()] = this
          }
        }.toList()
        .forEach { discoverTypes(it, map) }
  }

  private fun extractTargetType(info: TypeInfo) : TypeInfo
    = info.composite?.let { extractTargetType(it) } ?: info

}
