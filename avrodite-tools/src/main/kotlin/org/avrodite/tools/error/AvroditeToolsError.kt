package org.avrodite.tools.error


class AvroditeToolsError(message: String?, cause: Exception?) : Exception(message, cause) {
  constructor(message: String): this(message, null)

  companion object {

    fun illegalNullArgument(name: String): AvroditeToolsError
      = AvroditeToolsError("Argument '$name' can't be null")

    fun illegalNullField(name: String): AvroditeToolsError
      = AvroditeToolsError("Field '$name' can't be null")



  }

}
