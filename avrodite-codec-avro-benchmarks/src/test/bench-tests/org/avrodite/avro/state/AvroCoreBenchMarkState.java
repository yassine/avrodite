package org.avrodite.avro.state;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.avrodite.avro.v1_8.AvroStandardV18.AVRO_1_8;

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
import org.avrodite.avro.AvroCodec;
import org.avrodite.avro.AvroStandard;
import org.avrodite.fixtures.event.Equity;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.fixtures.event.EquityOrder;
import org.avrodite.fixtures.event.EventMeta;
import org.avrodite.tools.AvroditeTools;
import org.avrodite.tools.compiler.Compilation;
import org.avrodite.tools.core.bean.TypeInfo;

public class AvroCoreBenchMarkState {

  public final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
  public final ByteArrayInputStream byteArrayInputStream;
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  public final Avrodite<AvroStandard, AvroCodec<?>> avroCodecManager;
  public final Schema eventSchema;
  public final Schema metaSchema;
  public final Schema equitySchema;
  public final Schema equityOrderSchema;
  public final GenericData.Record equityRecord;
  public final GenericData.Record eventRecord;
  public final GenericData.Record metaRecord;
  public final GenericDatumWriter<GenericData.Record> datumWriter;
  public final GenericDatumReader<GenericData.Record> datumReader;
  public final BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);

  public AvroCoreBenchMarkState(byte[] data, boolean nullableFields) {
    byteArrayInputStream = new ByteArrayInputStream(data);
    avroCodecManager = Avrodite.builder(AVRO_1_8)
      .include(AvroditeTools.compiler(AVRO_1_8).discover(EquityMarketPriceEvent.class.getPackage().getName())
        .nullableFieldPredicate((Class<?> contextClass, Type contextType, Field field, TypeInfo genericInfo) -> nullableFields)
        .compile()
        .stream()
        .map(Compilation::define)
        .collect(toList())
      ).build();
    eventSchema = avroCodecManager.<EquityMarketPriceEvent, AvroCodec<EquityMarketPriceEvent>>getBeanCodec(EquityMarketPriceEvent.class).getSchema();
    metaSchema = avroCodecManager.<EventMeta, AvroCodec<EventMeta>>getBeanCodec(EventMeta.class).getSchema();
    equitySchema = avroCodecManager.<Equity, AvroCodec<Equity>>getBeanCodec(Equity.class).getSchema();
    equityOrderSchema = avroCodecManager.<EquityOrder, AvroCodec<EquityOrder>>getBeanCodec(EquityOrder.class).getSchema();


    equityRecord = new GenericData.Record(requireNonNull(equitySchema));
    eventRecord = new GenericData.Record(requireNonNull(eventSchema));
    metaRecord = new GenericData.Record(requireNonNull(metaSchema));

    datumWriter = new GenericDatumWriter<>(eventSchema);
    datumReader = new GenericDatumReader<>(eventSchema);

    initRecordsData();

  }

  private void initRecordsData(){
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
  }
}
