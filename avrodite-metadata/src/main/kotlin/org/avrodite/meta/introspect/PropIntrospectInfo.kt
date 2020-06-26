package org.avrodite.meta.introspect

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

class PropIntrospectInfo (
  val name: String,
  val prop: KProperty<*>,
  val type: KType,
  val rawType: KClass<*>,
  val owner: KClass<*>,
  val ownerType: KType,
  val writable: Boolean
)
