package org.avrodite.tools.avro

import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.io.EncoderFactory
import org.avrodite.Avrodite
import org.avrodite.avro.AvroCodec
import org.avrodite.avro.AvroOutputByteBuffer
import org.avrodite.avro.AvroStandard
import org.avrodite.avro.v1_9.AvroStandardV19
import org.avrodite.fixtures.event.Equity
import org.avrodite.fixtures.event.EquityMarketPriceEvent
import org.avrodite.fixtures.event.EventMeta
import org.avrodite.tools.AvroditeTools
import spock.lang.Specification

import java.util.stream.Collectors

class EventSpec extends Specification {

  def "event encode spec"() {
    given:
    BUFFER_OUT.reset()
    def avrodite_bytes = AVRODITE_CODEC.encode(MODEL)
    DATUM_WRITER.write(EVENT_RECORD, EncoderFactory.get().binaryEncoder(BYTE_ARRAY_OUTPUT_STREAM, BINARY_ENCODER))
    BINARY_ENCODER.flush()
    //using Avro's core implementation as reference
    def avro_bytes = BYTE_ARRAY_OUTPUT_STREAM.toByteArray()

    expect:
    avrodite_bytes == avro_bytes
  }

  def "event decode spec"() {
    given:
    def model = new EquityMarketPriceEvent()
    AVRODITE_CODEC.decode(model, SERIALIZED_EXPECTED_MODEL)

    expect:
    model == DESERIALIZED_EXPECTED_MODEL
  }

  def BYTE_ARRAY_OUTPUT_STREAM = new ByteArrayOutputStream(256)
  def BUFFER_OUT = new AvroOutputByteBuffer(new byte[256])
  Avrodite<AvroStandard, AvroCodec<?>> AVRO_CODEC_MANAGER
  def EVENT_SCHEMA
  def META_SCHEMA
  def EQUITY_SCHEMA
  def AVRODITE_CODEC
  def EQUITY_RECORD
  def EVENT_RECORD
  def META_RECORD
  def MODEL
  def DATUM_WRITER
  def DATUM_READER
  def BINARY_ENCODER
  def SERIALIZED_EXPECTED_MODEL = [0, 0, 72, 50, 56, 48, 52, 48, 97, 100, 50, 45, 52, 56, 99, 102, 45, 52, 56, 53, 51,
                                   45, 97, 48, 97, 54, 45, 100, 48, 54, 55, 48, 101, 98, 55, 56, 49, 50, 102, 0, 72, 97, 49, 102, 52, 97,
                                   51, 57, 51, 45, 49, 56, 51, 97, 45, 52, 98, 52, 102, 45, 57, 102, 97, 54, 45, 51, 57, 98, 53, 99, 101,
                                   100, 52, 52, 57, 101, 53, 0, 72, 56, 97, 52, 56, 49, 54, 54, 98, 45, 56, 100, 102, 49, 45, 52, 53, 98,
                                   54, 45, 56, 100, 53, 99, 45, 49, 54, 98, 52, 98, 49, 52, 98, 48, 53, 54, 102, 0, 0, 12, 84, 73, 67, 75,
                                   69, 82, 0, 4, 86, 14, 45, -78, 13, 95, 64, 0, -36, -105, -125, 120, 0, -57, -70, -72, -115, 6, -16, -122,
                                   -65] as byte[]

  def DESERIALIZED_EXPECTED_MODEL = new EquityMarketPriceEvent()
    .setMeta(new EventMeta()
      .setCorrelation("8a48166b-8df1-45b6-8d5c-16b4b14b056f")
      .setId("28040ad2-48cf-4853-a0a6-d0670eb7812f")
      .setParentId("a1f4a393-183a-4b4f-9fa6-39b5ced449e5")
    ).setTarget(new Equity()
    .setPrice(124.214)
    .setTicker("TICKER")
    .setVolume(125855214L)
    .setVariation(-0.0112)
  )

  def setup() {

    AvroStandardV19.avrodite().build();
    AVRO_CODEC_MANAGER = Avrodite.builder(AvroStandardV19.AVRO_1_9)
      .include(
        AvroditeTools.compiler(AvroStandardV19.AVRO_1_9)
          .discover(EquityMarketPriceEvent.class.package.name)
          .addClassLoader(Thread.currentThread().contextClassLoader)
          .addClassPathItems("/tmp/test")
          .exclude(Object.class)
          .compile().stream()
          .map({ it.define() })
          .map({ (AvroCodec<?>) it.newInstance()})
          .collect(Collectors.toList())
      ).build()
    EVENT_SCHEMA = AVRO_CODEC_MANAGER.<EquityMarketPriceEvent, AvroCodec<EquityMarketPriceEvent>>getCodec(EquityMarketPriceEvent.class).getSchema()
    META_SCHEMA = AVRO_CODEC_MANAGER.<EventMeta, AvroCodec<EventMeta>> getCodec(EventMeta.class).getSchema()
    EQUITY_SCHEMA = AVRO_CODEC_MANAGER.<Equity, AvroCodec<Equity>> getCodec(Equity.class).getSchema()
    AVRODITE_CODEC = (AvroCodec<EquityMarketPriceEvent>) AVRO_CODEC_MANAGER.getCodec(EquityMarketPriceEvent.class)
    EQUITY_RECORD = new GenericData.Record((Schema) Objects.requireNonNull(EQUITY_SCHEMA))
    EVENT_RECORD = new GenericData.Record((Schema) Objects.requireNonNull(EVENT_SCHEMA))
    META_RECORD = new GenericData.Record((Schema) Objects.requireNonNull(META_SCHEMA))
    MODEL = EquityMarketPriceEvent.create()
    DATUM_WRITER = new GenericDatumWriter<>((Schema) EVENT_SCHEMA)
    DATUM_READER = new GenericDatumReader<>((Schema) EVENT_SCHEMA)
    BINARY_ENCODER = EncoderFactory.get().binaryEncoder(BYTE_ARRAY_OUTPUT_STREAM, null)
    EQUITY_RECORD.put("price", MODEL.target.price)
    EQUITY_RECORD.put("ticker", MODEL.target.ticker)
    EQUITY_RECORD.put("variation", MODEL.target.variation)
    EQUITY_RECORD.put("volume", MODEL.target.volume)
    EVENT_RECORD.put("meta", META_RECORD)
    EVENT_RECORD.put("target", EQUITY_RECORD)
    META_RECORD.put("correlation", MODEL.meta.correlation)
    META_RECORD.put("id", MODEL.meta.id)
    META_RECORD.put("parentId", MODEL.meta.parentId)
  }


}
