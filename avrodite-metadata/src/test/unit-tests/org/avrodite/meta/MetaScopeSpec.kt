package org.avrodite.meta

import org.avrodite.meta.MetaScope.Companion.of
import org.avrodite.meta.scope.exclude.ModelInNonIncludedPackage
import org.avrodite.meta.scope.include.*
import org.avrodite.meta.scope.valuetype.ValueTypeModel
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

object MetaScopeSpec : Spek({

  val scope = of {
    include("org.avrodite.meta.scope.include")
    excludedClasses(InPackageExcludedClass::class)
    valueTypePredicate { it == ValueTypeModel::class }
  }

  describe("when a scope is configured with a context package, a type is in scope if : ") {
    it("have constructor params with types from included package") {
      expectThat(scope.isTypeOfInterest(ValidModelA::class)).isTrue()
    }
    it("have constructor params of primitive type or from included package") {
      expectThat(scope.isTypeOfInterest(ValidModelB::class)).isTrue()
    }
    it("have no constructor params") {
      expectThat(scope.isTypeOfInterest(ValidModelC::class)).isTrue()
    }
    it("have a constructor with a type arguments from included packages") {
      expectThat(scope.isTypeOfInterest(ValidModelD::class)).isTrue()
    }
    it("type with primitive constructor args") {
      expectThat(scope.isTypeOfInterest(ValidModelE::class)).isTrue()
    }
    it("type with primitive array constructor args") {
      expectThat(scope.isTypeOfInterest(ValidModelF::class)).isTrue()
    }
    it("have a constructor with a data structure type arguments from included packages") {
      expectThat(scope.isTypeOfInterest(ValidModelG::class)).isTrue()
    }

    it("have a constructor with a value type") {
      expectThat(scope.isTypeOfInterest(ValidModelH::class)).isTrue()
    }

    it("have a constructor with a data structure type arguments of value type") {
      expectThat(scope.isTypeOfInterest(ValidModelI::class)).isTrue()
    }
  }

  describe("when a scope is configured with a context package, a type is out of scope if : ") {
    // out of scope types
    it("it should exclude types that : are in a different package") {
      expectThat(scope.isTypeOfInterest(ModelInNonIncludedPackage::class)).isFalse()
    }
    it("it should exclude types that : depends by constructor on a type in a different package") {
      expectThat(scope.isTypeOfInterest(InvalidModelA::class)).isFalse()
    }
    it("it should exclude types that : depends by constructor on a data-structure with type parameters in a different package") {
      expectThat(scope.isTypeOfInterest(InvalidModelB::class)).isFalse()
    }
    it("it should exclude types that : depends by constructor on an explicitly excluded type") {
      expectThat(scope.isTypeOfInterest(InvalidModelC::class)).isFalse()
    }
    it("it should exclude types that : depends by constructor on a data-structure with type parameters of an explicitly excluded class") {
      expectThat(scope.isTypeOfInterest(InvalidModelD::class)).isFalse()
    }
  }

})
