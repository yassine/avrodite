package org.avrodite.meta.type.include

import org.avrodite.meta.type.valuetype.MetaTypeValueTypeModel

class ModelA(val modelB: ModelB, val modelC: ModelC, val valueModel: MetaTypeValueTypeModel, val int: Int, val string: String)

open class ModelA2 {
  var modelB: ModelB? = null
  var modelC: ModelC? = null
  var valueModel: MetaTypeValueTypeModel? = null
  var int: Int = 0
  var string: String? = null
}

class ModelB(val modelC: ModelC, val int: Int)
class ModelC(val string: String)

open class ChildModelA2 : ModelA2()

class GrandChildModelA2 : ChildModelA2()
