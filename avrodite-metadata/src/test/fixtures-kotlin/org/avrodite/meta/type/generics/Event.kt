package org.avrodite.meta.type.generics

class KBody
class KHeader

open class KEvent<H, B>(val header: H, val body: B)

class KFixtureEvent(header: KHeader, body: KBody) : KEvent<KHeader, KBody>(header, body){
  var hello : String? = null
}
