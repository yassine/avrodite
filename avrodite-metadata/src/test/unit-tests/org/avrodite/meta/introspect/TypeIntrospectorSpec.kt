package org.avrodite.meta.introspect

import org.avrodite.meta.type.TypeInfo
import org.avrodite.meta.type.generics.KFixtureEvent
import org.avrodite.meta.type.generics.java.FixtureEvent
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.reflect.full.withNullability

object TypeIntrospectorSpec : Spek({

  describe("kotlin: classes introspection") {
    val introspector = TypeIntrospector(KFixtureEvent::class)
    val props = introspector.findProps()

    it("should include props from supertypes"){
      expectThat(props.map{ it.name }.toSet())
        .isEqualTo(hashSetOf("body", "header", "hello"))
    }

    it("props of generic types should be resolved"){
      expectThat(props.map{ it.type.withNullability(false).toString() }.toSet())
        .isEqualTo(hashSetOf("org.avrodite.meta.type.generics.KBody", "org.avrodite.meta.type.generics.KHeader", "kotlin.String"))
    }
  }

  describe("java: classes introspection") {
    val introspector = TypeIntrospector(FixtureEvent::class)
    val props = introspector.findProps()

    it("should include props from supertypes"){
      expectThat(props.map{ it.name }.toSet())
        .isEqualTo(hashSetOf("header", "body", "headerSimple", "prop"))
    }

    it("props of generic types should be resolved"){
      expectThat(props.map { TypeInfo(it.type).signature() }.toSet())
        .isEqualTo(
          hashSetOf(
            "kotlin.collections.List<org.avrodite.meta.type.generics.java.Body>",
            "kotlin.collections.List<org.avrodite.meta.type.generics.java.Header>",
            "kotlin.String",
            "org.avrodite.meta.type.generics.java.Header"
          )
        )
    }

  }

})
