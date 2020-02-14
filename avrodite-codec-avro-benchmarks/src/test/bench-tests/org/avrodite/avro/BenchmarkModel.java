package org.avrodite.avro;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.avrodite.avro.value.v1_8.AvroStandardV18.AVRO_1_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.stream.IntStream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.avrodite.Avrodite;
import org.avrodite.fixtures.event.Equity;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.fixtures.event.EquityOrder;
import org.avrodite.fixtures.event.EventMeta;
import org.avrodite.proto.EquityEvents;
import org.avrodite.proto.required.EquityEventsNonNullableFields;
import org.avrodite.tools.AvroditeTools;
import org.avrodite.tools.compiler.Compilation;
import org.avrodite.tools.core.bean.TypeInfo;

public class BenchmarkModel {
  final boolean nullableFields;
  final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
  final ByteArrayInputStream byteArrayInputStream;
  final AvroOutputByteBuffer bufferOut = new AvroOutputByteBuffer(new byte[1024]);
  final AvroInputByteBuffer bufferIn;
  final Avrodite<AvroStandard, AvroCodec<?>> avroCodecManager;
  final Schema eventSchema;
  final Schema metaSchema;
  final Schema equitySchema;
  final Schema equityOrderSchema;
  final AvroCodec<EquityMarketPriceEvent> avroditeCodec;
  final GenericData.Record equityRecord;
  final GenericData.Record eventRecord;
  final GenericData.Record metaRecord;
  final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  final GenericDatumWriter<GenericData.Record> datumWriter;
  final GenericDatumReader<GenericData.Record> datumReader;
  final BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
  final EquityEventsNonNullableFields.EquityMarketPriceEvent nonNullableFieldsEvent = EquityEventsNonNullableFields.EquityMarketPriceEvent.newBuilder()
    .setMeta(
      EquityEventsNonNullableFields.EventMeta.newBuilder()
        .setCorrelation(model.getMeta().getCorrelation())
        .setId(model.getMeta().getId())
        .setParentId(model.getMeta().getParentId())
    ).setTarget(
      EquityEventsNonNullableFields.Equity.newBuilder()
        .setPrice(model.getTarget().getPrice())
        .setVolume(model.getTarget().getVolume())
        .setTicker(model.getTarget().getTicker())
        .setVariation(model.getTarget().getVariation())
    ).addAllAsk(
      model.getAsk().stream()
        .map(ask -> EquityEventsNonNullableFields.EquityOrder.newBuilder()
          .setCount(ask.getCount())
          .setQuantity(ask.getQuantity())
          .setPrice(ask.getPrice())
          .build()
        )
        .collect(toList())
    ).addAllBid(
      model.getBid().stream()
        .map(ask -> EquityEventsNonNullableFields.EquityOrder.newBuilder()
          .setCount(ask.getCount())
          .setQuantity(ask.getQuantity())
          .setPrice(ask.getPrice())
          .build()
        )
        .collect(toList())
    ).build();
  final EquityEvents.EquityMarketPriceEvent NULLABLE_FIELDS_EVENT = EquityEvents.EquityMarketPriceEvent.newBuilder()
    .setMeta(
      EquityEvents.EventMeta.newBuilder()
        .setCorrelation(model.getMeta().getCorrelation())
        .setId(model.getMeta().getId())
        .setParentId(model.getMeta().getParentId())
    ).setTarget(
      EquityEvents.Equity.newBuilder()
        .setPrice(model.getTarget().getPrice())
        .setVolume(model.getTarget().getVolume())
        .setTicker(model.getTarget().getTicker())
        .setVariation(model.getTarget().getVariation())
    ).addAllAsk(
      model.getAsk().stream()
        .map(ask -> EquityEvents.EquityOrder.newBuilder()
          .setCount(ask.getCount())
          .setQuantity(ask.getQuantity())
          .setPrice(ask.getPrice())
          .build()
        )
        .collect(toList())
    ).addAllBid(
      model.getBid().stream()
        .map(ask -> EquityEvents.EquityOrder.newBuilder()
          .setCount(ask.getCount())
          .setQuantity(ask.getQuantity())
          .setPrice(ask.getPrice())
          .build()
        )
        .collect(toList())
    ).build();

  final byte[] serializedModels = new byte[] {0, 0, 72, 99, 98, 51, 57, 51, 55, 50, 51, 45, 102, 53,
    101, 101, 45, 52, 55, 53, 57, 45, 97, 48, 98, 54, 45, 102, 100, 55, 50, 54, 102, 51, 49, 101, 100,
    55, 57, 0, 72, 97, 48, 50, 53, 97, 51, 97, 54, 45, 51, 53, 56, 56, 45, 52, 49, 97, 97, 45, 97, 97,
    57, 101, 45, 102, 52, 99, 99, 48, 52, 54, 51, 53, 55, 53, 99, 0, 72, 52, 100, 98, 101, 97, 97, 50,
    99, 45, 52, 102, 100, 49, 45, 52, 101, 99, 98, 45, 56, 54, 100, 52, 45, 50, 101, 99, 51, 50, 50,
    55, 55, 57, 98, 97, 49, 0, 0, 12, 84, 73, 67, 75, 69, 82, 0, 4, 86, 14, 45, -78, 13, 95, 64, 0,
    -36, -105, -125, 120, 0, -57, -70, -72, -115, 6, -16, -122, -65, 0, 10, 0, 2, 0, 78, -76, -85,
    -112, -14, -27, 94, 64, 0, -128, -75, 24, 0, 4, 0, -104, 18, 73, -12, 50, -66, 94, 64, 0, -64,
    -49, 36, 0, 6, 0, -29, 112, -26, 87, 115, -106, 94, 64, 0, -128, -22, 48, 0, 8, 0, 45, -49, -125,
    -69, -77, 110, 94, 64, 0, -64, -124, 61, 0, 10, 0, 119, 45, 33, 31, -12, 70, 94, 64, 0, -128, -97,
    73, 0, 0, 10, 0, 2, 0, -71, -9, 112, -55, 113, 53, 95, 64, 0, -96, -100, 1, 0, 4, 0, 112, -103, -45,
    101, 49, 93, 95, 64, 0, -64, -72, 2, 0, 6, 0, 36, 59, 54, 2, -15, -124, 95, 64, 0, -32, -44, 3, 0, 8,
    0, -37, -36, -104, -98, -80, -84, 95, 64, 0, -128, -15, 4, 0, 10, 0, -112, 126, -5, 58, 112, -44, 95,
    64, 0, -96, -115, 6, 0};
  final byte[] nonNullableSerializedModel = new byte[] {
    72, 50, 97, 52, 57, 97, 98, 54, 49, 45, 50, 48, 100, 100, 45, 52, 101, 51, 56, 45, 56, 56, 98,
    51, 45, 99, 48, 57, 53, 99, 100, 48, 50, 53, 99, 55, 56, 72, 100, 97, 100, 101, 98, 100, 54,
    101, 45, 100, 99, 56, 100, 45, 52, 100, 97, 49, 45, 57, 54, 56, 50, 45, 57, 102, 50, 98, 48, 50,
    55, 54, 48, 98, 100, 50, 72, 99, 51, 54, 53, 54, 98, 48, 54, 45, 48, 54, 53, 100, 45, 52, 56, 49,
    97, 45, 57, 53, 102, 99, 45, 50, 50, 99, 97, 100, 55, 48, 53, 53, 53, 49, 101, 12, 84, 73, 67,
    75, 69, 82, 4, 86, 14, 45, -78, 13, 95, 64, -36, -105, -125, 120, -57, -70, -72, -115, 6, -16,
    -122, -65, 10, 2, 78, -76, -85, -112, -14, -27, 94, 64, -128, -75, 24, 4, -104, 18, 73, -12, 50,
    -66, 94, 64, -64, -49, 36, 6, -29, 112, -26, 87, 115, -106, 94, 64, -128, -22, 48, 8, 45, -49,
    -125, -69, -77, 110, 94, 64, -64, -124, 61, 10, 119, 45, 33, 31, -12, 70, 94, 64, -128, -97, 73,
    0, 10, 2, -71, -9, 112, -55, 113, 53, 95, 64, -96, -100, 1, 4, 112, -103, -45, 101, 49, 93, 95,
    64, -64, -72, 2, 6, 36, 59, 54, 2, -15, -124, 95, 64, -32, -44, 3, 8, -37, -36, -104, -98, -80,
    -84, 95, 64, -128, -15, 4, 10, -112, 126, -5, 58, 112, -44, 95, 64, -96, -115, 6, 0
  };

  final byte[] protobufBytes = new byte[] {
    10, 114, 10, 36, 52, 48, 100, 54, 56, 97, 48, 101, 45, 48, 48, 99, 53, 45, 52, 50, 56, 97, 45,
    57, 102, 99, 55, 45, 56, 52, 57, 98, 99, 98, 48, 53, 99, 99, 101, 49, 18, 36, 52, 98, 51, 98,
    101, 52, 50, 53, 45, 101, 53, 50, 99, 45, 52, 50, 51, 97, 45, 97, 57, 49, 99, 45, 56, 97, 50,
    101, 56, 53, 101, 49, 98, 102, 51, 100, 26, 36, 52, 102, 98, 97, 54, 56, 51, 56, 45, 52, 54,
    48, 49, 45, 52, 101, 55, 100, 45, 97, 48, 97, 55, 45, 101, 51, 100, 98, 98, 56, 53, 50, 50, 100,
    55, 49, 18, 31, 10, 6, 84, 73, 67, 75, 69, 82, 17, 4, 86, 14, 45, -78, 13, 95, 64, 24, -18, -53,
    -127, 60, 33, -57, -70, -72, -115, 6, -16, -122, -65, 26, 14, 8, 1, 17, -71, -9, 112, -55, 113,
    53, 95, 64, 24, -112, 78, 26, 15, 8, 2, 17, 112, -103, -45, 101, 49, 93, 95, 64, 24, -96, -100,
    1, 26, 15, 8, 3, 17, 36, 59, 54, 2, -15, -124, 95, 64, 24, -80, -22, 1, 26, 15, 8, 4, 17, -37,
    -36, -104, -98, -80, -84, 95, 64, 24, -64, -72, 2, 26, 15, 8, 5, 17, -112, 126, -5, 58, 112, -44,
    95, 64, 24, -48, -122, 3, 34, 15, 8, 1, 17, 78, -76, -85, -112, -14, -27, 94, 64, 24, -64, -102,
    12, 34, 15, 8, 2, 17, -104, 18, 73, -12, 50, -66, 94, 64, 24, -32, -89, 18, 34, 15, 8, 3, 17, -29,
    112, -26, 87, 115, -106, 94, 64, 24, -128, -75, 24, 34, 15, 8, 4, 17, 45, -49, -125, -69, -77, 110,
    94, 64, 24, -96, -62, 30, 34, 15, 8, 5, 17, 119, 45, 33, 31, -12, 70, 94, 64, 24, -64, -49, 36
  };

  final byte[] protobufBytesNonNull = new byte[] {
    10, 114, 10, 36, 98, 97, 54, 57, 99, 100, 48, 53, 45, 50, 101, 55, 99, 45, 52, 54, 49, 48, 45, 57,
    48, 50, 51, 45, 98, 98, 48, 101, 52, 57, 53, 51, 101, 54, 48, 55, 18, 36, 54, 49, 99, 97, 101, 100,
    51, 51, 45, 52, 50, 50, 57, 45, 52, 52, 56, 51, 45, 57, 55, 52, 101, 45, 55, 53, 53, 55, 57, 98,
    55, 51, 54, 56, 97, 50, 26, 36, 51, 54, 51, 57, 56, 97, 49, 53, 45, 55, 53, 100, 51, 45, 52, 50,
    48, 56, 45, 98, 50, 57, 48, 45, 57, 54, 56, 56, 49, 48, 57, 102, 97, 99, 49, 53, 18, 31, 10, 6,
    84, 73, 67, 75, 69, 82, 17, 4, 86, 14, 45, -78, 13, 95, 64, 24, -18, -53, -127, 60, 33, -57, -70,
    -72, -115, 6, -16, -122, -65, 26, 14, 8, 1, 17, -71, -9, 112, -55, 113, 53, 95, 64, 24, -112, 78,
    26, 15, 8, 2, 17, 112, -103, -45, 101, 49, 93, 95, 64, 24, -96, -100, 1, 26, 15, 8, 3, 17, 36, 59,
    54, 2, -15, -124, 95, 64, 24, -80, -22, 1, 26, 15, 8, 4, 17, -37, -36, -104, -98, -80, -84, 95,
    64, 24, -64, -72, 2, 26, 15, 8, 5, 17, -112, 126, -5, 58, 112, -44, 95, 64, 24, -48, -122, 3, 34,
    15, 8, 1, 17, 78, -76, -85, -112, -14, -27, 94, 64, 24, -64, -102, 12, 34, 15, 8, 2, 17, -104, 18,
    73, -12, 50, -66, 94, 64, 24, -32, -89, 18, 34, 15, 8, 3, 17, -29, 112, -26, 87, 115, -106, 94,
    64, 24, -128, -75, 24, 34, 15, 8, 4, 17, 45, -49, -125, -69, -77, 110, 94, 64, 24, -96, -62, 30,
    34, 15, 8, 5, 17, 119, 45, 33, 31, -12, 70, 94, 64, 24, -64, -49, 36
  };

  final byte[] testTarget;

  final ByteArrayInputStream protobufIs = new ByteArrayInputStream(protobufBytes);
  final ByteArrayOutputStream protobufOs = new ByteArrayOutputStream(protobufBytes.length);

  final ObjectReader reader;
  final ObjectWriter writer;
  //JSON
  final ObjectMapper mapper = new ObjectMapper();

  public BenchmarkModel(boolean nullableFields) {
    testTarget = nullableFields ? serializedModels : nonNullableSerializedModel;
    this.byteArrayInputStream = new ByteArrayInputStream(testTarget);
    bufferIn = new AvroInputByteBuffer(testTarget);
    this.nullableFields = nullableFields;
    avroCodecManager = Avrodite.builder(AVRO_1_8)
      .include(AvroditeTools.compiler(AVRO_1_8).discover(EquityMarketPriceEvent.class.getPackage().getName())
        .nullableFieldPredicate((Class<?> contextClass, Type contextType, Field field, TypeInfo genericInfo) -> nullableFields)
        .compile()
        .stream()
        .map(Compilation::define)
        .collect(toList())
      ).build();
    eventSchema = avroCodecManager.getBeanCodecAs(EquityMarketPriceEvent.class, AvroCodec.class).getSchema();
    metaSchema = avroCodecManager.getBeanCodecAs(EventMeta.class, AvroCodec.class).getSchema();
    equitySchema = avroCodecManager.getBeanCodecAs(Equity.class, AvroCodec.class).getSchema();
    equityOrderSchema = avroCodecManager.getBeanCodecAs(EquityOrder.class, AvroCodec.class).getSchema();
    avroditeCodec = avroCodecManager.getBeanCodecAs(EquityMarketPriceEvent.class, AvroCodec.class);
    equityRecord = new GenericData.Record(requireNonNull(equitySchema));
    eventRecord = new GenericData.Record(requireNonNull(eventSchema));
    metaRecord = new GenericData.Record(requireNonNull(metaSchema));

    datumWriter = new GenericDatumWriter<>(eventSchema);
    datumReader = new GenericDatumReader<>(eventSchema);

    equityRecord.put("price", model.getTarget().getPrice());
    equityRecord.put("ticker", model.getTarget().getTicker());
    equityRecord.put("variation", model.getTarget().getVariation());
    equityRecord.put("volume", model.getTarget().getVolume());
    eventRecord.put("meta", metaRecord);
    eventRecord.put("target", equityRecord);
    metaRecord.put("correlation", model.getMeta().getCorrelation());
    metaRecord.put("id", model.getMeta().getId());
    metaRecord.put("parentId", model.getMeta().getParentId());

    eventRecord.put("bid", IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        GenericData.Record record = new GenericData.Record(requireNonNull(equityOrderSchema));
        record.put("price", model.getTarget().getPrice() * (1 - i * 0.005));
        record.put("count", i);
        record.put("quantity", (long) (100000 * (1 + i)));
        return record;
      }).collect(toList())
    );

    eventRecord.put("ask", IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        GenericData.Record record = new GenericData.Record(requireNonNull(equityOrderSchema));
        record.put("price", model.getTarget().getPrice() * (1 + i * 0.005));
        record.put("count", i);
        record.put("quantity", (long) (100000 * (1 + i - 1)));
        return record;
      }).collect(toList())
    );

    try {
      ObjectMapper mapper = new ObjectMapper(new AvroFactory());
      AvroSchema schemaWrapper = new AvroSchema(eventSchema);
      reader = mapper.readerFor(EquityMarketPriceEvent.class).with(schemaWrapper);
      writer = mapper.writer(schemaWrapper);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
