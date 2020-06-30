package org.avrodite.avro.io

import org.avrodite.avro.AvroCodec
import org.avrodite.tools.AvroditeTools
import org.avrodite.tools.avro.avro
import org.avrodite.tools.avro.event.EquityMarketPriceEvent
import org.avrodite.tools.compiler.Utils
import kotlin.reflect.typeOf

object CodecProvider {

  @ExperimentalStdlibApi
  fun avroCodec() : AvroCodec<EquityMarketPriceEvent> {

    val fsCompilationResult = AvroditeTools.avro.kotlin {
      meta {
        scope {
          include("org.avrodite.tools.avro.event")
        }
        targets { add(EquityMarketPriceEvent::class) }
      }
    }

    val classes = fsCompilationResult.results.entries
      .map {
        val classLoader = Thread.currentThread().contextClassLoader
        Utils.defineClass(classLoader, it.key, it.value)!!
      }
    val output = ByteArrayAvroOutput(ByteArray(1024) { 0.toByte() })
    return classes.filter { it.simpleName != "Companion" }
      .map { it.newInstance() as AvroCodec<*> }
      .first { it.target() == typeOf<EquityMarketPriceEvent>() }
      .let { it as AvroCodec<EquityMarketPriceEvent> }

  }

}
