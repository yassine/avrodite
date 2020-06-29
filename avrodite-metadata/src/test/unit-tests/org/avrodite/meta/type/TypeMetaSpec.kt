package org.avrodite.meta.type

import org.avrodite.meta.MetaScope.Companion.of
import org.avrodite.meta.scope.valuetype.ValueTypeModel
import org.avrodite.meta.type.include.*
import org.avrodite.meta.type.valuetype.*

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

object TypeMetaSpec : Spek({

  val scope = of {
    include("org.avrodite.meta.type.include")
    exclude(InPackageExcludedClass::class)
    valueTypePredicate { it == ValueTypeModel::class || it == MetaTypeValueTypeModel::class }
  }

  describe("when type meta is defined using a type and scope") {

    it("constructor parameters should be within scope") {
      val metaInfo = TypeMeta(ModelA::class, scope)
      metaInfo.constructorParams.all {
        scope.isTypeOfInterest(it.typeInfo)
      }
    }

    it("constructor parameters should be within order of declaration (default behavior)") {
      val metaInfo = TypeMeta(ModelA::class, scope)
      expectThat(metaInfo.constructorParams.map { it.parameter.name })
        .isEqualTo(listOf("modelB", "modelC", "valueModel", "int", "string"))
    }

    it("fields should be within scope") {
      val metaInfo = TypeMeta(ModelA2::class, scope)
      metaInfo.props.all {
        scope.isTypeOfInterest(it.typeInfo)
      }
    }

    it("super classes fields should be included") {
      val metaInfo = TypeMeta(GrandChildModelA2::class, scope)
      expectThat(metaInfo.props.map { it.field.name }.toSet())
        .isEqualTo(
          setOf(
            ModelA2::modelB.name,
            ModelA2::modelC.name,
            ModelA2::valueModel.name,
            ModelA2::int.name,
            ModelA2::string.name
          )
        )
    }

    it("fields names are propagated") {
      val metaInfo = TypeMeta(ModelA2::class, scope)
      expectThat(metaInfo.props.map { it.field.name }.toSet())
        .isEqualTo(
          setOf(
            ModelA2::modelB.name,
            ModelA2::modelC.name,
            ModelA2::valueModel.name,
            ModelA2::int.name,
            ModelA2::string.name
          )
        )
    }

    it("should ignore field having excluded types") {
      val metaInfo = TypeMeta(ModelZ::class, scope)
      expectThat(metaInfo.props).isEmpty()
    }

    it("should ignore fields bound to excluded types through data-structures ") {
      val metaInfo = TypeMeta(ModelZ1::class, scope)
      expectThat(metaInfo.props).isEmpty()
    }

  }

})
