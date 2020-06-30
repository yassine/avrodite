package org.avrodite.meta.type.include

import org.avrodite.meta.type.exclude.ModelInExcludedPackage

class InPackageExcludedClass

class ModelZ {
  var inPackageExcludedClass: InPackageExcludedClass? = null
  var modelInExcludedPackage: ModelInExcludedPackage? = null
}

class ModelZ1 {
  var inPackageExcludedClass: Array<InPackageExcludedClass>? = null
  var modelInExcludedPackage: Array<ModelInExcludedPackage>? = null
  var inPackageExcludedClassList: List<InPackageExcludedClass>? = null
  var modelInExcludedPackageList: List<ModelInExcludedPackage>? = null
  var inPackageExcludedClassSet: Set<InPackageExcludedClass>? = null
  var modelInExcludedPackageSet: Set<ModelInExcludedPackage>? = null
  var inPackageExcludedClassMap: Map<String, InPackageExcludedClass>? = null
  var modelInExcludedPackageMap: Map<String, ModelInExcludedPackage>? = null
}
