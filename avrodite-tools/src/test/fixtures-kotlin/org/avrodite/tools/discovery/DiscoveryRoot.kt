package org.avrodite.tools.discovery

abstract class KAbstractDiscoveryTestRoot<A, B, C, D, E, F> {
  var component: A? = null
  var children: List<B>? = null
  var grandGrandChildren: Set<Array<List<C?>>>? = null
  var longNumber: Long? = null
  var longNumber2: Long = 0
  var shortNumber: Short? = null
  var shortNumber2: Short = 0
  var doubleNumber: Double? = null
  var doubleNumber2: Double = 0.0
  var floatNumber: Float? = null
  var floatNumber2 = 0f
  var intNumber: Int? = null
  var intNumber2 = 0
  var string: String? = null

  //maps
  var map: Map<String, D>? = null
  var map2: Map<String, List<Array<E>>>? = null
  var arr: Array<F>? = null
}

class KDiscoveryRoot : KAbstractDiscoveryTestRoot<KModelF, KModelE, KModelD, KModelC, KModelB, KModelA>() {
  var matrix: IntArray? = null
  var composite: List<Map<String, KCompositeModel<KModelA, KModelB>>>? = null
  var logical = false
  var nativeInt = 0
}

class KCompositeModel<P, Q> {
  var param1: P? = null
  var param2: Q? = null
}
