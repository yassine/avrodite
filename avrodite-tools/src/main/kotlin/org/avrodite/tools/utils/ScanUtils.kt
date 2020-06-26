package org.avrodite.tools.utils

import io.github.classgraph.ClassGraph

object ScanUtils {

  fun classPathScannerWith(classLoaders: Set<ClassLoader>, whitelistPackages: Set<String> = setOf()): ClassGraph
    = ClassGraph().also {
        whitelistPackages.forEach { pkg -> it.whitelistPackages(pkg) }
        classLoaders.forEach { loader -> it.addClassLoader(loader) }
      }

  fun classesImplementing(classGraph: ClassGraph, clazz: Class<*>) : Set<Class<*>>
    = classGraph.enableClassInfo().scan()
        .getClassesImplementing(clazz.name)
        .map { Class.forName(it.name) }
        .toSet()

}
