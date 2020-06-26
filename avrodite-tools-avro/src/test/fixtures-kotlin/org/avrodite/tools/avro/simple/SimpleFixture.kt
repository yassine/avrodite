package org.avrodite.tools.avro.simple

class SimpleFixture(
  val array: Array<String> = arrayOf()
) {
  var o: OtherFixture? = null
}

class OtherFixture(val hello: String)
