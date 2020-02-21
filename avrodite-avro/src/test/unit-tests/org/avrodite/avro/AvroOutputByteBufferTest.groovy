package org.avrodite.avro

import org.apache.avro.io.BinaryData
import org.apache.avro.io.EncoderFactory
import spock.lang.Specification

import java.util.stream.IntStream

import static org.assertj.core.api.Assertions.assertThat

class AvroOutputByteBufferTest extends Specification {

  def "ensure"() {
    given:
    def buffer = new AvroOutputByteBuffer(new byte[5])
    and:
    buffer.ensure(10)
    expect:
    buffer.data.length == 10
  }

  def "writeArrayStart"() {
    given:
    def data = new byte[10]
    new Random().nextBytes(data)

    and:
    def buffer = new AvroOutputByteBuffer(data)
    def cursor = buffer.cursor
    buffer.writeArrayStart()

    expect:
    //existing data is not altered/modified in any ways
    buffer.data == data
    buffer.cursor == cursor

  }

  def "writeBytes"() {
    given:
    def buffer = new AvroOutputByteBuffer(new byte[5])
    def bytes = [1, 2, 3, 4, 5] as byte[]

    and:
    buffer.writeBytes(bytes)

    expect:
    assertThat(buffer.data).startsWith(bytes)
  }

  def "writeString"() {
    given:
    def output1 = new AvroOutputByteBuffer(new byte[64])
    def baos = new ByteArrayOutputStream(64)
    def encoder = EncoderFactory.get().binaryEncoder(baos, null)
    output1.writeString("writeString")
    encoder.writeString("writeString")
    encoder.flush()
    def target = baos.toByteArray()

    expect:
    assertThat(output1.data).startsWith(target)
  }


  def "writeDoubleRandomly"() {
    given:
    def values = new ArrayList<byte[]>()
    def expected = new ArrayList<byte[]>()
    IntStream.range(0, 100000).boxed()
      .map({ new Random().nextDouble() })
      .forEach({
        def output1 = new AvroOutputByteBuffer(new byte[64])
        def target1 = new byte[64]
        output1.writeDouble(it)
        BinaryData.encodeDouble(it, target1, 0)
        values.add(output1.data)
        expected.add(target1)
      })
    expect:
    values == expected
  }

  def "writeDouble"() {
    when:
    def output1 = new AvroOutputByteBuffer(new byte[64])
    def target1 = new byte[64]
    output1.writeDouble(0)
    BinaryData.encodeDouble(0, target1, 0)

    def output2 = new AvroOutputByteBuffer(new byte[64])
    def target2 = new byte[64]
    output2.writeDouble(-1)
    BinaryData.encodeDouble(-1, target2, 0)

    def output3 = new AvroOutputByteBuffer(new byte[64])
    def target3 = new byte[64]
    output3.writeDouble(1)
    BinaryData.encodeDouble(1, target3, 0)

    def output4 = new AvroOutputByteBuffer(new byte[64])
    def target4 = new byte[64]
    output4.writeDouble(Double.MIN_VALUE)
    BinaryData.encodeDouble(Double.MIN_VALUE, target4, 0)

    def output5 = new AvroOutputByteBuffer(new byte[64])
    def target5 = new byte[64]
    output5.writeDouble(Double.MAX_VALUE)
    BinaryData.encodeDouble(Double.MAX_VALUE, target5, 0)

    then:
    output1.data == target1
    output2.data == target2
    output3.data == target3
    output4.data == target4
    output5.data == target5

  }

  def "writeFloat"() {
    when:
    def output1 = new AvroOutputByteBuffer(new byte[64])
    def target1 = new byte[64]
    output1.writeFloat(0)
    BinaryData.encodeFloat(0, target1, 0)

    def output2 = new AvroOutputByteBuffer(new byte[64])
    def target2 = new byte[64]
    output2.writeFloat(-1)
    BinaryData.encodeFloat(-1, target2, 0)

    def output3 = new AvroOutputByteBuffer(new byte[64])
    def target3 = new byte[64]
    output3.writeFloat(1)
    BinaryData.encodeFloat(1, target3, 0)

    def output4 = new AvroOutputByteBuffer(new byte[64])
    def target4 = new byte[64]
    output4.writeFloat(Float.MIN_VALUE)
    BinaryData.encodeFloat(Float.MIN_VALUE, target4, 0)

    def output5 = new AvroOutputByteBuffer(new byte[64])
    def target5 = new byte[64]
    output5.writeFloat(Float.MAX_VALUE)
    BinaryData.encodeFloat(Float.MAX_VALUE, target5, 0)

    then:
    output1.data == target1
    output2.data == target2
    output3.data == target3
    output4.data == target4
    output5.data == target5

  }

  def "writeLong"() {
    when:
    def output1 = new AvroOutputByteBuffer(new byte[64])
    def target1 = new byte[64]
    output1.writeLong(0)
    BinaryData.encodeLong(0, target1, 0)

    def output2 = new AvroOutputByteBuffer(new byte[64])
    def target2 = new byte[64]
    output2.writeLong(-1)
    BinaryData.encodeLong(-1, target2, 0)

    def output3 = new AvroOutputByteBuffer(new byte[64])
    def target3 = new byte[64]
    output3.writeLong(1)
    BinaryData.encodeLong(1, target3, 0)

    def output4 = new AvroOutputByteBuffer(new byte[64])
    def target4 = new byte[64]
    output4.writeLong(Long.MIN_VALUE)
    BinaryData.encodeLong(Long.MIN_VALUE, target4, 0)

    def output5 = new AvroOutputByteBuffer(new byte[64])
    def target5 = new byte[64]
    output5.writeLong(Long.MAX_VALUE)
    BinaryData.encodeLong(Long.MAX_VALUE, target5, 0)

    then:
    output1.data == target1
    output2.data == target2
    output3.data == target3
    output4.data == target4
    output5.data == target5

  }

  def "writeLongRandomly"() {
    given:
    def values = new ArrayList<byte[]>()
    def expected = new ArrayList<byte[]>()
    IntStream.range(0, 100000).boxed()
      .map({ new Random().nextLong() })
      .forEach({
        def output1 = new AvroOutputByteBuffer(new byte[64])
        def target1 = new byte[64]
        output1.writeLong(it)
        BinaryData.encodeLong(it, target1, 0)
        values.add(output1.data)
        expected.add(target1)
      })
    expect:
    values == expected
  }

  def "writeInt"() {
    when:
    def output1 = new AvroOutputByteBuffer(new byte[64])
    def target1 = new byte[64]
    output1.writeInt(0)
    BinaryData.encodeInt(0, target1, 0)

    def output2 = new AvroOutputByteBuffer(new byte[64])
    def target2 = new byte[64]
    output2.writeInt(-1)
    BinaryData.encodeInt(-1, target2, 0)

    def output3 = new AvroOutputByteBuffer(new byte[64])
    def target3 = new byte[64]
    output3.writeInt(1)
    BinaryData.encodeInt(1, target3, 0)

    def output4 = new AvroOutputByteBuffer(new byte[64])
    def target4 = new byte[64]
    output4.writeInt(Integer.MIN_VALUE)
    BinaryData.encodeInt(Integer.MIN_VALUE, target4, 0)

    def output5 = new AvroOutputByteBuffer(new byte[64])
    def target5 = new byte[64]
    output5.writeInt(Integer.MAX_VALUE)
    BinaryData.encodeInt(Integer.MAX_VALUE, target5, 0)

    then:
    output1.data == target1
    output2.data == target2
    output3.data == target3
    output4.data == target4
    output5.data == target5

  }

  def "writeIntRandomly"() {
    given:
    def values = new ArrayList<byte[]>()
    def expected = new ArrayList<byte[]>()
    IntStream.range(0, 100000).boxed()
      .map({ new Random().nextInt() })
      .forEach({
        def actualBytes = new AvroOutputByteBuffer(new byte[64])
        def expectedBytes = new byte[64]
        actualBytes.writeInt(it)
        BinaryData.encodeInt(it, expectedBytes, 0)
        values.add(actualBytes.data)
        expected.add(expectedBytes)
      })
    expect:
    values == expected
  }

}
