package org.avrodite.meta.scope.include

import org.avrodite.meta.scope.exclude.ModelInNonIncludedPackage

class InPackageExcludedClass

//has a constructor dependency on a type from non included package
class InvalidModelA(val modelInNonIncludedPackage: ModelInNonIncludedPackage)

//type with deps via constructor type parameters
class InvalidModelB(val list: List<ModelInNonIncludedPackage>, val array: Array<ModelInNonIncludedPackage>, val set: Set<ModelInNonIncludedPackage>, val map: Map<String, ModelInNonIncludedPackage>)

//has a constructor dependency on a type from non included package
class InvalidModelC(val inPackageExcludedClass: InPackageExcludedClass)

//type with deps via constructor type parameters
class InvalidModelD(val list: List<InPackageExcludedClass>, val array: Array<InPackageExcludedClass>, val set: Set<InPackageExcludedClass>, val map: Map<String, InPackageExcludedClass>)
