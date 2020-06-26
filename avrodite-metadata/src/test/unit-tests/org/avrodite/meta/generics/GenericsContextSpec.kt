package org.avrodite.meta.generics

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

@ExperimentalStdlibApi
class GenericsContextSpec: Spek({

  describe("resolve Suite") {

    it("it should resolve implemented interfaces generics specialization") {
      expectThat(GenericsContext.of(typeOf<FixtureDao>()).resolve(WriteDao::class)?.typeParameters?.map(Any::toString))
        .isEqualTo(listOf(typeOf<Int>().toString()))

      expectThat(GenericsContext.of(typeOf<FixtureDao>()).resolve(ReadDao::class)?.typeParameters?.map(Any::toString))
        .isEqualTo(listOf(typeOf<Double>().toString()))
    }

    it("it should resolve supertypes generics specialization") {
      expectThat(GenericsContext.of(typeOf<FixtureDao>()).resolve(MyDao::class)?.typeParameters?.map(Any::toString))
        .isEqualTo(listOf(typeOf<Double>().toString(), typeOf<Int>().toString()))
    }

    it("it should resolve properties generics specialization") {
      expectThat(GenericsContext.of(typeOf<FixtureDao>()).resolveProp(MyDao::class.memberProperties.first { it.name == "prop" }).let { it.toString() })
        .isEqualTo("kotlin.Double?")
    }

  }


}){
  companion object {
    interface WriteDao<in T> {
      fun save(u : T)
    }
    interface ReadDao<out T> {
      fun find() : T
    }
    class FixtureDao(d: Double, i: Int) : MyDao<Double, Int>(d, i)
    open class MyDao<D : Number, I: Number>(val d: D, val i: I) : ReadDao<D>, WriteDao<I> {
      var prop: D? = null
      override fun find(): D  = TODO("Not yet implemented")
      override fun save(u: I) = TODO("Not yet implemented")
    }
  }
}

