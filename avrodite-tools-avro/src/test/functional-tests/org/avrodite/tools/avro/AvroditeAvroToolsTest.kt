package org.avrodite.tools.avro

import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.avrodite.avro.AvroInput
import org.avrodite.avro.AvroOutput
import org.avrodite.avro.AvroValueCodec
import org.avrodite.meta.MetaManager
import org.avrodite.tools.AvroditeTools
import org.avrodite.tools.avro.event.EquityMarketPriceEvent

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.Instant

object AvroditeAvroToolsTest : Spek({

  describe("some") {

    it("should be somewhat saze") {
      val registry = AvroSchemaTools.createValueCodecRegistry()
      val types = registry.keys
      val result = MetaManager.of {
        scope {
          include("org.avrodite.tools.avro.event")
          valueTypePredicate {
            kClass -> types.any { it == kClass.qualifiedName }
          }
        }
        targets { add(EquityMarketPriceEvent::class) }
      }
    }

    it("should scan") {
      AvroditeTools.avro.kotlin {
        meta {
          scope {
            include("org.avrodite.tools.avro.event")
          }
          targets { add(EquityMarketPriceEvent::class) }
        }
        compiler {

        }
      }
    }

  }

})


class TestValueCodec: AvroValueCodec<Instant> {

  override fun schema(): Schema {
    return SchemaBuilder.builder().longType()
  }

  override fun encode(output: AvroOutput, value: Instant) {
    TODO("Not yet implemented")
  }

  override fun decode(input: AvroInput): Instant {
    TODO("Not yet implemented")
  }

}

