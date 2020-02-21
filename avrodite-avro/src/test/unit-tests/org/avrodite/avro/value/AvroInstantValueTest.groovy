package org.avrodite.avro.value

import org.apache.avro.io.BinaryData
import org.avrodite.avro.AvroInputByteBuffer
import org.avrodite.avro.AvroOutputByteBuffer
import org.avrodite.avro.v1_8.InstantCodecV18
import org.avrodite.avro.v1_9.InstantCodecV19
import spock.lang.Specification

import java.time.Instant

import static org.assertj.core.api.Assertions.assertThat

class AvroInstantValueTest extends Specification {


  def "test encoding v1.8"() {
    given:
    def instant = Instant.now()
    def codec = new InstantCodecV18()
    def output = new AvroOutputByteBuffer(new byte[64])
    def data = new byte[64]
    codec.encode(instant, output)
    BinaryData.encodeLong(instant.toEpochMilli(), data, 0)

    expect:
    assertThat(data).startsWith(output.toByteArray())
  }

  def "test encoding v1.9"() {
    given:
    def instant = Instant.now()
    def codec = new InstantCodecV19()
    def output4 = new AvroOutputByteBuffer(new byte[64])
    def data = new byte[64]
    codec.encode(instant, output4)
    BinaryData.encodeLong(instant.toEpochMilli(), data, 0)

    expect:
    assertThat(data).startsWith(output4.toByteArray())
  }

  def "test decoding v1.9"() {
    given:
    def instant = Instant.now()
    def data = new byte[64]
    BinaryData.encodeLong(instant.toEpochMilli(), data, 0)
    def codec = new InstantCodecV19()
    def input = new AvroInputByteBuffer(data)
    def result = codec.decode(input)

    expect:
    instant.toEpochMilli() == result.toEpochMilli()
  }

  def "test decoding v1.8"() {
    given:
    def instant = Instant.now()
    def data = new byte[64]
    BinaryData.encodeLong(instant.toEpochMilli(), data, 0)
    def codec = new InstantCodecV18()
    def input = new AvroInputByteBuffer(data)
    def result = codec.decode(input)

    expect:
    instant.toEpochMilli() == result.toEpochMilli()
  }

}
