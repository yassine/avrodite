package org.avrodite.avro.io

import org.apache.avro.io.BinaryData
import org.apache.avro.io.EncoderFactory
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.*
import java.util.UUID.randomUUID
import kotlin.math.abs
import kotlin.text.Charsets.UTF_8

object ByteArrayAvroOutputSpec : Spek({

  describe("writeInt") {
    it("should encode random int according to AVRO spec") {
      val random = Random()
      val cornerCases = intArrayOf(0, -1, 1, Int.MAX_VALUE, Int.MIN_VALUE)
      val randomValues = IntArray(10000) { random.nextInt() }
      val randomNegativeValues = IntArray(10000) { -random.nextInt() }
      for (value in cornerCases + randomValues + randomNegativeValues) {
        val stream = ByteArrayAvroOutput(ByteArray(32) { 0 })
        val expected = ByteArray(32) { 0 }.also { BinaryData.encodeInt(value, it, 0) }
        val actual = stream.also { it.writeInt(value) }.toByteArray()
        expectThat(actual).isEqualTo(expected.slice(actual.indices).toByteArray())
      }
    }
  }

  describe("writeLong") {
    it("should encode random long values according to AVRO spec") {
      val random = Random()
      val cornerCases = longArrayOf(0, -1, 1, Long.MAX_VALUE, Long.MIN_VALUE)
      val randomValues = LongArray(10000) { random.nextLong() }
      val randomNegativeValues = LongArray(10000) { -random.nextLong() }
      for (value in cornerCases + randomValues + randomNegativeValues) {
        val stream = ByteArrayAvroOutput(ByteArray(32) { 0 })
        val expected = ByteArray(32) { 0 }.also { BinaryData.encodeLong(value, it, 0) }
        val actual = stream.also { it.writeLong(value) }.toByteArray()
        expectThat(actual).isEqualTo(expected.slice(actual.indices).toByteArray())
      }
    }
  }

  describe("writeDouble") {
    it("should encode random double values according to AVRO spec") {
      val random = Random()
      val cornerCases = doubleArrayOf(0.0, -1.0, 1.0, Double.MAX_VALUE, Double.MIN_VALUE)
      val randomValues = DoubleArray(10000) { random.nextDouble() }
      val randomNegativeValues = DoubleArray(10000) { -random.nextDouble() }
      for (value in cornerCases + randomValues + randomNegativeValues) {
        val stream = ByteArrayAvroOutput(ByteArray(32) { 0 })
        val expected = ByteArray(32) { 0 }.also { BinaryData.encodeDouble(value, it, 0) }
        val actual = stream.also { it.writeDouble(value) }.toByteArray()
        expectThat(actual).isEqualTo(expected.slice(actual.indices).toByteArray())
      }
    }
  }

  describe("writeFloat") {
    it("should encode random double values according to AVRO spec") {
      val random = Random()
      val cornerCases = floatArrayOf(0.0f, -1.0f, 1.0f, Float.MAX_VALUE, Float.MIN_VALUE)
      val randomValues = FloatArray(10000) { random.nextFloat() }
      for (value in cornerCases + randomValues) {
        val stream = ByteArrayAvroOutput(ByteArray(16) { 0 })
        val expected = ByteArray(16) { 0 }.also { BinaryData.encodeFloat(value, it, 0) }
        val actual = stream.also { it.writeFloat(value) }.toByteArray()
        expectThat(actual).isEqualTo(expected.slice(actual.indices).toByteArray())
      }
    }
  }

  describe("writeString") {

    fun randomString(): String {
      val count = 1 + abs(Random().nextInt(10))
      val string = (1 until count).map { randomUUID() }.fold(randomUUID().toString()) { x, y -> x + y }
      val substringIndex = abs(Random().nextInt(string.length))
      return string.substring(substringIndex)
    }

    it("should encode random double values according to AVRO spec") {
        for (value in 0 until 10000) {
          val target = randomString()
          val expected = target.toByteArray(UTF_8)
          val stream  = ByteArrayAvroOutput(ByteArray(expected.size) { 0 })
          val baos    = ByteArrayOutputStream(expected.size + 5)
          val encoder = EncoderFactory.get().binaryEncoder(baos, null)
          encoder.writeString(target)
          encoder.flush()
          stream.writeString(target)
          expectThat(stream.toByteArray()).isEqualTo(baos.toByteArray())
        }
    }

  }

})
