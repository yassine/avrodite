package org.avrodite.meta

import org.avrodite.meta.scope.include.*
import org.avrodite.meta.type.discovery.*
import org.avrodite.meta.scope.valuetype.ValueTypeModel
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import kotlin.reflect.KClass

object MetaManagerSpec : Spek({

  describe("Scope Suite") {

    val classes = MetaManager.of {
      scope {
        include(ValidModelG::class.java.`package`.name)
        excludedClasses(InPackageExcludedClass::class)
        valueTypePredicate { it == ValueTypeModel::class }
      }
      targets {
        add(ValidModelG::class)
      }
    }.index()

    it("should include classes from packages within scope") {
      expectThat(classes.values.map { it.typeInfo.packageName() }
        .all { it.startsWith(ValidModelG::class.java.`package`.name) }).isTrue()
    }

    it("exclude classes marked as so") {
      expectThat(classes.values.map { it.typeInfo.type.classifier as KClass<*> }
        .all { it != InPackageExcludedClass::class }).isTrue()
    }

  }

  describe("Auto-Discovery Suite") {

    val index = MetaManager.of {
      scope {
        include(KDiscoveryRoot::class.java.`package`.name)
      }
      targets {
        add(KDiscoveryRoot::class)
      }
    }.index()

    it("it should discover types through fields inspection") {
      expectThat(index.values.map { it.typeInfo.signature() }.toSet())
        .contains(setOf(
          "org.avrodite.meta.type.discovery.KModelA",
          "org.avrodite.meta.type.discovery.KModelC",
          "org.avrodite.meta.type.discovery.KModelB",
          "org.avrodite.meta.type.discovery.KDiscoveryRoot",
          "org.avrodite.meta.type.discovery.KModelE",
          "org.avrodite.meta.type.discovery.KCompositeModel<org.avrodite.meta.type.discovery.KModelA, org.avrodite.meta.type.discovery.KModelB>",
          "org.avrodite.meta.type.discovery.KModelD",
          "org.avrodite.meta.type.discovery.KModelF"
        ))
    }

    it("should exclude non-constructible types") {
      expectThat(index.values.map { it.typeInfo.signature() }.toSet().none {
        it != "org.avrodite.meta.type.discovery.KIgnoredInterface"
      }).isFalse()
    }

  }

})
