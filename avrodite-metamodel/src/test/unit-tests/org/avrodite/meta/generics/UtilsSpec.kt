package org.avrodite.meta.generics

import org.avrodite.meta.generics.Utils.isAssignable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.reflect.typeOf

@ExperimentalStdlibApi
class UtilsSpec : Spek({

  describe("isAssignable Suite") {

    it("4") {
      expectThat(isAssignable(typeOf<ReadDao<Int>>(), typeOf<MyDao<Number>>()))
        .isEqualTo(false)
      expectThat(isAssignable(typeOf<ReadDao<Number>>(), typeOf<MyDao<Int>>()))
        .isEqualTo(true)
      expectThat(isAssignable(typeOf<WriteDao<Int>>(), typeOf<MyDao<Number>>()))
        .isEqualTo(true)
      expectThat(isAssignable(typeOf<WriteDao<Number>>(), typeOf<MyDao<Int>>()))
        .isEqualTo(false)
    }

  }

})

interface WriteDao<in T> {
  fun save(u : T)
}

interface ReadDao<out T> {
  fun find() : T
}

class DoubleDao(d: Double) : MyDao<Double>(d){
  var prop: String? = null
}

open class MyDao<D : Number>(val d: D) : ReadDao<D>, WriteDao<D> {

  override fun find(): D {
    TODO("Not yet implemented")
  }

  override fun save(u: D) {
    TODO("Not yet implemented")
  }

}
