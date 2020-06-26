package org.avrodite.avro.io

import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.avrodite.avro.error.AvroditeAvroException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectCatching
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.ByteArrayOutputStream
import java.util.*

object ByteArrayAvroInputSpec : Spek({

  describe("readInt Suite") {

    it("It should read the next binary data as an int (corner cases)") {
      // int corner cases
      val binDataInt0: ByteArray = byteArrayOf(0, 0, 0, 0, 0) //0
      val binDataInt1: ByteArray = byteArrayOf(1, 0, 0, 0, 0) //-1
      val binDataInt2: ByteArray = byteArrayOf(2, 0, 0, 0, 0) //1
      val binDataInt3: ByteArray = byteArrayOf(-2, -1, -1, -1, 15) //MAX_INTEGER
      val binDataInt4: ByteArray = byteArrayOf(-1, -1, -1, -1, 15) //MIN_INTEGER

      // actual values
      val input = ByteArrayAvroInput(byteArrayOf())

      expectThat(input.reset(binDataInt0).readInt())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binDataInt0, null).readInt())
      expectThat(input.reset(binDataInt1).readInt())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binDataInt1, null).readInt())
      expectThat(input.reset(binDataInt2).readInt())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binDataInt2, null).readInt())
      expectThat(input.reset(binDataInt3).readInt())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binDataInt3, null).readInt())
      expectThat(input.reset(binDataInt4).readInt())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binDataInt4, null).readInt())

    }

    it("It should read the next binary data as an int (1000 random test values)") {
      val byteArrayOutputStream = ByteArrayOutputStream(24)
      val byteArray = ByteArray(24) { 0 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      val random = Random(1000)
      val encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null)
      for (i in 0 until 10000) {
        val number = random.nextInt(Int.MAX_VALUE)
        byteArrayOutputStream.reset()
        encoder.writeInt(number)
        encoder.flush()
        byteArrayAvroInput.reset(byteArrayOutputStream.toByteArray())
        expectThat(byteArrayAvroInput.readInt()).isEqualTo(number)
      }
    }

    it("It should throw an io exception when next bin data isn't a valid int") {
      val byteArray = ByteArray(24) { -1 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readInt() }.describedAs(AvroditeAvroException.unexpectedNumber().message!!)
    }

    it("It should throw an io exception when there is no binary data left") {
      val byteArray = byteArrayOf()
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readInt() }.describedAs(AvroditeAvroException.endOfInput().message!!)
    }

  }

  describe("readLong Suite") {

    it("It should read the next binary data as an long (corner cases)") {
      // int corner cases
      val binData0: ByteArray = byteArrayOf(0, 0, 0, 0, 0) //0
      val binData1: ByteArray = byteArrayOf(1, 0, 0, 0, 0) //-1
      val binData2: ByteArray = byteArrayOf(2, 0, 0, 0, 0) //1
      val binData3: ByteArray = byteArrayOf(-2, -1, -1, -1, -1, -1, -1, -1, -1, 1) //MAX_INTEGER
      val binData4: ByteArray = byteArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, 1) //MIN_INTEGER

      // actual values
      val input = ByteArrayAvroInput(byteArrayOf())

      expectThat(input.reset(binData0).readLong())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData0, null).readLong())
      expectThat(input.reset(binData1).readLong())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData1, null).readLong())
      expectThat(input.reset(binData2).readLong())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData2, null).readLong())
      expectThat(input.reset(binData3).readLong())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData3, null).readLong())
      expectThat(input.reset(binData4).readLong())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData4, null).readLong())

    }

    it("It should read the next binary data as an long (1000 random test values)") {
      val byteArrayOutputStream = ByteArrayOutputStream(24)
      val byteArray = ByteArray(24) { 0 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      val random = Random()
      val encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null)
      for (i in 0 until 10000) {
        val number = random.nextLong()
        byteArrayOutputStream.reset()
        encoder.writeLong(number)
        encoder.flush()
        byteArrayAvroInput.reset(byteArrayOutputStream.toByteArray())
        expectThat(byteArrayAvroInput.readLong()).isEqualTo(number)
      }
    }

    it("It should throw an io exception when next bin data isn't a valid int") {
      val byteArray = ByteArray(24) { -1 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readInt() }.describedAs(AvroditeAvroException.unexpectedNumber().message!!)
    }

    it("It should throw an io exception when there is no binary data left") {
      val byteArray = byteArrayOf()
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readLong() }.describedAs(AvroditeAvroException.endOfInput().message!!)
    }

  }

  describe("readFloat Suite") {

    it("It should read the next binary data as a float (corner cases)") {
      // int corner cases
      val binData0: ByteArray = byteArrayOf(0, 0, 0, 0) //0
      val binData1: ByteArray = byteArrayOf(0, 0, -128, -65) //-1
      val binData2: ByteArray = byteArrayOf(0, 0, -128, 63) //1
      val binData3: ByteArray = byteArrayOf(-1, -1, 127, 127) //MAX_INTEGER
      val binData4: ByteArray = byteArrayOf(1, 0, 0, 0) //MIN_INTEGER

      // actual values
      val input = ByteArrayAvroInput(byteArrayOf())

      expectThat(input.reset(binData0).readFloat())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData0, null).readFloat())
      expectThat(input.reset(binData1).readFloat())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData1, null).readFloat())
      expectThat(input.reset(binData2).readFloat())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData2, null).readFloat())
      expectThat(input.reset(binData3).readFloat())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData3, null).readFloat())
      expectThat(input.reset(binData4).readFloat())
        .isEqualTo(DecoderFactory.get().binaryDecoder(binData4, null).readFloat())

    }

    it("It should read the next binary data as a float (1000 random test values)") {
      val byteArrayOutputStream = ByteArrayOutputStream(24)
      val byteArray = ByteArray(24) { 0 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      val random = Random(1000)
      val encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null)
      for (i in 0 until 10000) {
        val number = random.nextFloat()
        byteArrayOutputStream.reset()
        encoder.writeFloat(number)
        encoder.flush()
        byteArrayAvroInput.reset(byteArrayOutputStream.toByteArray())
        expectThat(byteArrayAvroInput.readFloat()).isEqualTo(number)
      }
    }

    it("It should throw an io exception when there is no binary data left") {
      val byteArray = byteArrayOf()
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readFloat() }.describedAs(AvroditeAvroException.endOfInput().message!!)
    }

  }

  describe("readDouble Suite") {

    it("It should read the next binary data as a double (1000 random test values)") {
      val byteArrayOutputStream = ByteArrayOutputStream(24)
      val byteArray = ByteArray(24) { 0 }
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      val random = Random(1000)
      val encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null)
      for (i in 0 until 10000) {
        val number = random.nextDouble()
        byteArrayOutputStream.reset()
        encoder.writeDouble(number)
        encoder.flush()
        byteArrayAvroInput.reset(byteArrayOutputStream.toByteArray())
        expectThat(byteArrayAvroInput.readDouble()).isEqualTo(number)
      }
    }

    it("It should throw an io exception when there is no binary data left") {
      val byteArray = byteArrayOf()
      val byteArrayAvroInput = ByteArrayAvroInput(byteArray)
      expectCatching { byteArrayAvroInput.readDouble() }.describedAs(AvroditeAvroException.endOfInput().message!!)
    }

  }

  describe("readString Suite") {

    fun randomString(): Pair<String, ByteArray> {
      val random = Random()
      val count = 1 + (random.nextInt(10))
      val string = (1 until count).map { UUID.randomUUID() }.fold(UUID.randomUUID().toString()) { x, y -> x + y }
      val substringIndex = (random.nextInt(string.length))
      val substring = string.substring(substringIndex)
      val baos    = ByteArrayOutputStream(substring.toByteArray(Charsets.UTF_8).size + 5)
      val encoder = EncoderFactory.get().binaryEncoder(baos, null)
      encoder.writeString(substring)
      encoder.flush()
      return substring to baos.toByteArray()
    }

    it("should read the next encoded String from the binary input buffer") {
      val stream = ByteArrayAvroInput(byteArrayOf(20, 114, 101, 97, 100, 83, 116, 114, 105, 110, 103))
      expectThat(stream.readString()).isEqualTo("readString")
      for(i in 1 until 10000) {
        val (string, bytes) = randomString()
        expectThat(ByteArrayAvroInput(bytes).readString()).isEqualTo(string)
      }
    }

    it("should throw an exception if remaining the buffer length is smaller than the string length") {
      val stream = ByteArrayAvroInput(byteArrayOf(22, 114, 101, 97, 100, 83, 116, 114, 105, 110, 103))
      expectCatching { stream.readString() }.describedAs(AvroditeAvroException.endOfInput().message!!)
    }



  }

})

