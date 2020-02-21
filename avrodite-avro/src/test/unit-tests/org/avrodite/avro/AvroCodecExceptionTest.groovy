package org.avrodite.avro


import spock.lang.Specification

class AvroCodecExceptionTest extends Specification {

  def "exceptions api propagate error correctly"(){
    given:
    def message = "propagated"
    def error = AvroCodecException.API.error(new RuntimeException(message))

    expect:
    error instanceof AvroCodecException
    error.cause.message == message
    error.cause instanceof RuntimeException
  }

}
