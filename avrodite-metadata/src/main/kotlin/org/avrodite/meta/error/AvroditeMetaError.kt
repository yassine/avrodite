package org.avrodite.meta.error

import kotlin.reflect.KClassifier

class AvroditeMetaError(message: String?, cause: Exception?) : Exception(message, cause) {
  constructor(message: String): this(message, null)

  companion object {
    fun unsupportedType(kClassifier: KClassifier?)
      = AvroditeMetaError("unsupported classifier : '$kClassifier'")
  }
}
