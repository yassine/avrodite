package org.avrodite.meta.scope.include

import org.avrodite.meta.scope.valuetype.ValueTypeModel


//has constructor params with types from included package
class ValidModelA(val validModelB: ValidModelB, val validModelC: ValidModelC)

//has fields of primitive type or from included package
class ValidModelB(val validModelC: ValidModelC, val intField: Int = 0)

//type with no deps and no fields
class ValidModelC

//type with deps via constructor type parameters
class ValidModelD(val models: List<ValidModelA>)

//type with primitive constructor args
class ValidModelE(val string: String, val int: Int, val float: Float, val double: Double, val short: Short, val boolean: Boolean)

//type with primitive arrays constructor args
class ValidModelF(val string: Array<String>, val int: Array<Int>, val float: Array<Float>, val double: Array<Double>, val short: Array<Short>, val boolean: Array<Boolean>)

//type with deps via constructor type parameters
class ValidModelG(val list: List<ValidModelA>, val array: Array<ValidModelA>, val set: Set<ValidModelA>, val map: Map<String, ValidModelA>)

//type with value type constructor dependency
class ValidModelH(val valueType: ValueTypeModel)

//type with constructor with data-structures of value type
class ValidModelI(val list: List<ValueTypeModel>, val array: Array<ValueTypeModel>, val set: Set<ValueTypeModel>, val map: Map<String, ValueTypeModel>)
