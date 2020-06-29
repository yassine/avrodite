package org.avrodite.tools.compiler

import java.lang.reflect.Method

object Utils {

  private const val DEFINE_CLASS_METHOD_NAME: String = "defineClass"
  private const val DEFINE_CLASS_METHOD_ARG_COUNT = 4

  fun defineClass(classLoader: ClassLoader, fqName: String, byteCode: ByteArray): Class<*>?
    = declaredMethods(classLoader.javaClass)
        .firstOrNull {
          method -> method.name == DEFINE_CLASS_METHOD_NAME && method.parameters.size == DEFINE_CLASS_METHOD_ARG_COUNT
        }
        ?.let {
          it.isAccessible = true
          it.invoke(classLoader, fqName, byteCode, 0, byteCode.size) as Class<*>?
        }

  fun declaredMethods(clazz: Class<*>): List<Method> {
    val superMethods = clazz.superclass.takeIf { it != Object::class.java }
      ?.let { declaredMethods(it) } ?: listOf()
    val superMethodsIndex = indexMethods(superMethods)
    val currentMethodsIndex = indexMethods(clazz.declaredMethods.toList())
    val mergedMethodsIndex = mutableMapOf<String, Method>()
    superMethodsIndex.entries.forEach { mergedMethodsIndex[it.key] = it.value }
    currentMethodsIndex.entries.forEach { mergedMethodsIndex[it.key] = it.value }
    return mergedMethodsIndex.values.toList()
  }

  private fun indexMethods(methods: List<Method>): Map<String, Method>
    = methods.map { (it.name + it.parameters.map { p -> p.type.name }.joinToString(",")) to it }
        .associateBy({ it.first }, { it.second })

}
