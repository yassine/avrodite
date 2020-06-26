package org.avrodite.avro.error

import org.avrodite.error.AvroditeException


sealed class AvroditeAvroException (message: String?, cause: Exception?) : AvroditeException(message, cause) {

  constructor(message: String) : this(message, null)

  companion object {

    fun unexpectedNumber(): AvroditeAvroIOException
      = AvroditeAvroIOException("Unexpected Number.")

    fun endOfInput(): AvroditeAvroIOException
      = AvroditeAvroIOException("Unexpected end of input")

    fun illegalArrayEnd(number: Int, fieldName: String, type: String): AvroditeAvroException
      = AvroditeAvroIOException("Unexpected end of array marker. Expected 0 but was '${number}' @ field '${fieldName}' of type '${type}'")

  }

  class AvroditeAvroIOException(message: String) : AvroditeAvroException(message)

}


