package org.avrodite.avro.state;

import static java.util.stream.Collectors.toList;
import static org.avrodite.avro.v1_8.AvroStandardV18.AVRO_1_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.apache.avro.Schema;
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

public class JacksonAvroBenchMarkState {
  public final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(2048);
  public final ByteArrayInputStream byteArrayInputStream;
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  public final Avrodite<AvroStandard, AvroCodec<?>> avroCodecManager;
  public final Schema eventSchema;
  public final Schema metaSchema;
  public final Schema equitySchema;
  public final Schema equityOrderSchema;
  public final ObjectReader reader;
  public final ObjectWriter writer;

  public JacksonAvroBenchMarkState(byte[] data, boolean nullableFields) {
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
