package org.avrodite.avro

import org.apache.avro.io.BinaryData
import org.apache.avro.io.DecoderFactory
import spock.lang.Specification

import java.util.stream.IntStream

class AvroInputByteBufferTest extends Specification {

  def "readArrayEnd : It should throw an end of input exception message when there is not enough binary data to process"() {
    when:
    def input = new AvroInputByteBuffer([] as byte[])
    input.readArrayEnd()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }

  def "readArrayEnd : It should throw an unexpected input if the next byte is not 0"() {
    when:
    def input = new AvroInputByteBuffer([5] as byte[])
    input.readArrayEnd()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.unexpectedInput(0, 5).message
  }

  def "readArrayEnd : It should move to the next byte or to the end if the next byte is 0"() {
    when:
    def input = new AvroInputByteBuffer([0] as byte[])
    input.readArrayEnd()
    then:
    input.cursor == input.end
  }

  def "readArrayStart : It should return the array length"() {
    when:
    def input = new AvroInputByteBuffer([2] as byte[])
    then:
    input.readArrayStart() == 1
  }

  def "readArrayStart : It should throw an exception if the next data doesn't map to an int"() {
    when:
    def input = new AvroInputByteBuffer([-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1] as byte[])
    input.readArrayStart()

    then:
    def e = thrown(AvroCodecException.class)
    e.message == AvroCodecException.API.unexpectedNumber().message
  }

  def "readInt : It should read the next binary data as an int"() {
    given:
    def binDataInt0 = [0, 0, 0, 0, 0] as byte[]//0
    def binDataInt1 = [1, 0, 0, 0, 0] as byte[]//-1
    def binDataInt2 = [2, 0, 0, 0, 0] as byte[]//1
    def binDataInt3 = [-2, -1, -1, -1, 15] as byte[] //MAX_INTEGER
    def binDataInt4 = [-1, -1, -1, -1, 15] as byte[] //MIN_INTEGER

    def input = new AvroInputByteBuffer([] as byte[])
    input.reset(binDataInt0)
    def int0 = input.readInt()
    input.reset(binDataInt1)
    def int1 = input.readInt()
    input.reset(binDataInt2)
    def int2 = input.readInt()
    input.reset(binDataInt3)
    def int3 = input.readInt()
    input.reset(binDataInt4)
    def int4 = input.readInt()

    expect:
    int0 == DecoderFactory.get().binaryDecoder(binDataInt0, null).readInt()
    int1 == DecoderFactory.get().binaryDecoder(binDataInt1, null).readInt()
    int2 == DecoderFactory.get().binaryDecoder(binDataInt2, null).readInt()
    int3 == DecoderFactory.get().binaryDecoder(binDataInt3, null).readInt()
    int4 == DecoderFactory.get().binaryDecoder(binDataInt4, null).readInt()
    int0 == 0
    int1 == -1
    int2 == 1
    int3 == Integer.MAX_VALUE
    int4 == Integer.MIN_VALUE
  }

  def "readInt : It should throw an end of input exception message when there is not enough binary data to process"() {
    when:
    def input = new AvroInputByteBuffer([] as byte[])
    input.readInt()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }


  def "readInt : It should throw an exception if the next data doesn't map to an int"() {
    when:
    def input = new AvroInputByteBuffer([-1, -1, -1, -1, -1, -1, -1, -1] as byte[])
    input.readInt()
    then:
    def e = thrown(AvroCodecException.class)
    e.message == AvroCodecException.API.unexpectedNumber().message
  }

  def "readFloat : It should read the next binary data as an float"() {
    given:
    def binDataFloat0 = [0, 0, 0, 0] as byte[]//0
    def binDataFloat1 = [0, 0, -128, -65] as byte[]//-1
    def binDataFloat2 = [0, 0, -128, 63] as byte[]//1
    def binDataFloat3 = [-1, -1, 127, 127] as byte[] //MAX_FLOAT
    def binDataFloat4 = [1, 0, 0, 0] as byte[] //MIN_FLOAT

    def input = new AvroInputByteBuffer([] as byte[])
    input.reset(binDataFloat0)
    def float0 = input.readFloat()
    input.reset(binDataFloat1)
    def float1 = input.readFloat()
    input.reset(binDataFloat2)
    def float2 = input.readFloat()
    input.reset(binDataFloat3)
    def float3 = input.readFloat()
    input.reset(binDataFloat4)
    def float4 = input.readFloat()
    expect:
    float0 == DecoderFactory.get().binaryDecoder(binDataFloat0, null).readFloat()
    float1 == DecoderFactory.get().binaryDecoder(binDataFloat1, null).readFloat()
    float2 == DecoderFactory.get().binaryDecoder(binDataFloat2, null).readFloat()
    float3 == DecoderFactory.get().binaryDecoder(binDataFloat3, null).readFloat()
    float4 == DecoderFactory.get().binaryDecoder(binDataFloat4, null).readFloat()
    float0 == 0
    float1 == -1
    float2 == 1
    float3 == Float.MAX_VALUE
    float4 == Float.MIN_VALUE
  }

  def "readFloat : It should throw an end of input exception message when there is not enough binary data to process"() {
    when:
    def input = new AvroInputByteBuffer([] as byte[])
    input.readFloat()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }

  def "readLong : It should read the next binary data as a long"() {
    given:
    def binDataLong0 = [0, 0, 0, 0, 0] as byte[]//0
    def binDataLong1 = [1, 0, 0, 0, 0] as byte[]//-1
    def binDataLong2 = [2, 0, 0, 0, 0] as byte[]//1
    def binDataLong3 = [-2, -1, -1, -1, -1, -1, -1, -1, -1, 1] as byte[] //MAX_LONG
    def binDataLong4 = [-1, -1, -1, -1, -1, -1, -1, -1, -1, 1] as byte[] //MIN_LONG

    def input = new AvroInputByteBuffer([] as byte[])
    input.reset(binDataLong0)
    def long0 = input.readLong()
    input.reset(binDataLong1)
    def long1 = input.readLong()
    input.reset(binDataLong2)
    def long2 = input.readLong()
    input.reset(binDataLong3)
    def long3 = input.readLong()
    input.reset(binDataLong4)
    def long4 = input.readLong()

    expect:
    long0 == DecoderFactory.get().binaryDecoder(binDataLong0, null).readLong()
    long1 == DecoderFactory.get().binaryDecoder(binDataLong1, null).readLong()
    long2 == DecoderFactory.get().binaryDecoder(binDataLong2, null).readLong()
    long3 == DecoderFactory.get().binaryDecoder(binDataLong3, null).readLong()
    long4 == DecoderFactory.get().binaryDecoder(binDataLong4, null).readLong()
    long0 == 0
    long1 == -1
    long2 == 1
    long3 == Long.MAX_VALUE
    long4 == Long.MIN_VALUE
  }

  def "readLong : It should throw an end of input exception message when there is not enough binary data to process"() {
    when:
    def input = new AvroInputByteBuffer([] as byte[])
    input.readLong()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }

  def "readLong : It should throw an exception if the next data doesn't map to an long"() {
    when:
    def input = new AvroInputByteBuffer([-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1] as byte[])
    input.readLong()
    then:
    def e = thrown(AvroCodecException.class)
    e.message == AvroCodecException.API.unexpectedNumber().message
  }

  def "readLongRandomly"() {
    given:
    def values = new ArrayList<Long>()
    def expected = new ArrayList<Long>()
    IntStream.range(0, 1000000).boxed()
      .map({ new Random().nextLong() })
      .forEach({
        expected.add(it)
        def target1 = new byte[64]
        BinaryData.encodeLong(it, target1, 0)
        values.add(new AvroInputByteBuffer(target1).readLong())
      })
    expect:
    values == expected
  }

  def "rewind : It should move the cursor to the beginning of the input"() {
    when:
    def input = new AvroInputByteBuffer([-2, -1, -1, -1, -1, -1, -1, -1, -1, 1] as byte[])
    input.readLong()
    input.rewind()
    then:
    input.cursor == 0
  }

  def "readString: It should read the next encoded String from the binary input buffer"() {
    when:
    def input = new AvroInputByteBuffer([20, 114, 101, 97, 100, 83, 116, 114, 105, 110, 103] as byte[])
    def result = input.readString()
    then:
    result == "readString"
  }

  def "readString: It should throw an exception if remaining the buffer length is smaller than the string length"() {
    when:
    def input = new AvroInputByteBuffer([22, 114, 101, 97, 100, 83, 116, 114, 105, 110, 103] as byte[])
    input.readString()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }

  def "readStrings: It should throw an exception if remaining the string length is negative"() {
    when:
    def input = new AvroInputByteBuffer([1] as byte[])
    input.readString()
    then:
    def error = thrown(AvroCodecException.class)
    error.message == AvroCodecException.API.endOfInput().message
  }

}
