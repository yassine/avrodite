package org.avrodite.tools.avro

import org.avrodite.avro.AvroCodec
import org.avrodite.avro.io.ByteArrayAvroInput
import org.avrodite.avro.io.ByteArrayAvroOutput
import org.avrodite.avro.io.CodecProvider
import org.avrodite.tools.avro.event.EquityMarketPriceEvent
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExperimentalStdlibApi
object AvroditeAvroToolsTest : Spek({

  // fixture serialization from version 0.1.0
  val serializationReference = byteArrayOf(
    72, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45, 56, 56, 98, 51,
    45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 72, 99, 51, 54, 53, 54, 98, 48, 54, 45, 48,
    54, 53, 100, 45, 52, 56, 49, 97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97, 100, 55, 48, 53, 53, 53,
    49, 101, 72, 100, 97, 100, 101, 98, 100, 54, 101, 45, 100, 99, 56, 100, 45, 52, 100, 97, 49, 45,
    57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50, 55, 54, 48, 98, 100, 50, 12, 84, 73, 67, 75, 69, 82,
    4, 86, 14, 45, -78, 13, 95, 64, -36, -105, -125, 120, -57, -70, -72, -115, 6, -16, -122, -65, 10,
    2, 78, -76, -85, -112, -14, -27, 94, 64, -128, -75, 24, 4, -104, 18, 73, -12, 50, -66, 94, 64,
    -64, -49, 36, 6, -29, 112, -26, 87, 115, -106, 94, 64, -128, -22, 48, 8, 45, -49, -125, -69, -77,
    110, 94, 64, -64, -124, 61, 10, 119, 45, 33, 31, -12, 70, 94, 64, -128, -97, 73, 0, 10, 2, -71,
    -9, 112, -55, 113, 53, 95, 64, -96, -100, 1, 4, 112, -103, -45, 101, 49, 93, 95, 64, -64, -72, 2,
    6, 36, 59, 54, 2, -15, -124, 95, 64, -32, -44, 3, 8, -37, -36, -104, -98, -80, -84, 95, 64, -128,
    -15, 4, 10, -112, 126, -5, 58, 112, -44, 95, 64, -96, -115, 6, 0
  )

  val codec : AvroCodec<EquityMarketPriceEvent> = CodecProvider.avroCodec()

  describe("avrodite serde test") {

    it("codecs built through Avrodite should serialize correctly") {
      val output = ByteArrayAvroOutput(ByteArray(1024){ 0.toByte() })
      val serialization = codec.encode(EquityMarketPriceEvent.createEvent(), output).let { output.toByteArray() }
      expectThat(serialization).isEqualTo(serializationReference)
    }

    it("codecs built through Avrodite should deserialize correctly") {

      val input = ByteArrayAvroInput(serializationReference)
      val deserialization = codec.decode(input)
      val reference = EquityMarketPriceEvent.createEvent()

      expectThat(deserialization.meta.correlation).isEqualTo(reference.meta.correlation)
      expectThat(deserialization.meta.id).isEqualTo(reference.meta.id)
      expectThat(deserialization.meta.parentId).isEqualTo(reference.meta.parentId)

      expectThat(deserialization.target.price).isEqualTo(reference.target.price)
      expectThat(deserialization.target.ticker).isEqualTo(reference.target.ticker)
      expectThat(deserialization.target.variation).isEqualTo(reference.target.variation)
      expectThat(deserialization.target.volume).isEqualTo(reference.target.volume)

      expectThat(deserialization.ask.size).isEqualTo(reference.ask.size)
      expectThat(deserialization.bid.size).isEqualTo(reference.bid.size)

      expectThat(deserialization.ask.map { it.count }).isEqualTo(reference.ask.map { it.count })
      expectThat(deserialization.ask.map { it.price }).isEqualTo(reference.ask.map { it.price })
      expectThat(deserialization.ask.map { it.quantity }).isEqualTo(reference.ask.map { it.quantity })

      expectThat(deserialization.bid.map { it.count }).isEqualTo(reference.bid.map { it.count })
      expectThat(deserialization.bid.map { it.price }).isEqualTo(reference.bid.map { it.price })
      expectThat(deserialization.bid.map { it.quantity }).isEqualTo(reference.bid.map { it.quantity })
    }

  }

})
