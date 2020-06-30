package org.avrodite.meta

import org.avrodite.meta.type.TypeDiscoverer
import org.avrodite.meta.type.TypeMeta
import kotlin.reflect.KClass

class MetaManager internal constructor(val scope: MetaScope, private val index: Map<String, TypeMeta>) {

  fun index(): Map<String, TypeMeta> = index.toMap()

  companion object {

    fun of(init: Builder.() -> Unit): MetaManager = Builder().also { it.init() }.create()

    class Builder {

      private val classes: MutableSet<KClass<*>> = hashSetOf()
      private val scopeBuilder: MetaScope.Companion.Builder = MetaScope.Companion.Builder()

      fun scope(builder: MetaScope.Companion.Builder.() -> Unit) = also { builder(scopeBuilder) }

      fun targets(receiver: MutableSet<KClass<*>>.() -> Unit) = also {
        hashSetOf<KClass<*>>()
          .also { receiver(it) }
          .also { classes.addAll(it) }
      }

      fun create(): MetaManager = MetaScope(scopeBuilder.build())
        .let { scope ->
          TypeDiscoverer(scope).discoverTypes(classes)
            .filter { scope.isTypeOfInterest(it.typeInfo) }
            .associateBy { it.typeInfo.signature() }
            .let { MetaManager(scope, it) }
        }

    }

  }
}
