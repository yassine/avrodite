package org.avrodite.avro.state;

import static java.util.stream.Collectors.toList;
import static org.avrodite.avro.v1_8.AvroStandardV18.AVRO_1_8;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.avrodite.Avrodite;
import org.avrodite.avro.AvroCodec;
import org.avrodite.avro.AvroInputByteBuffer;
import org.avrodite.avro.AvroOutputByteBuffer;
import org.avrodite.avro.AvroStandard;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;
import org.avrodite.tools.AvroditeTools;
import org.avrodite.tools.compiler.Compilation;
import org.avrodite.tools.core.bean.TypeInfo;

public class AvroditeBenchmarkState {

  public final AvroOutputByteBuffer bufferOut = new AvroOutputByteBuffer(new byte[1024]);
  public final AvroInputByteBuffer bufferIn;
  public final AvroCodec<EquityMarketPriceEvent> avroditeCodec;
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  public final Avrodite<AvroStandard, AvroCodec<?>> avroCodecManager;

  public AvroditeBenchmarkState(byte[] data, boolean nullableFields) {
    avroCodecManager = Avrodite.builder(AVRO_1_8)
      .include(AvroditeTools.compiler(AVRO_1_8).discover(EquityMarketPriceEvent.class.getPackage().getName())
        .nullableFieldPredicate((Class<?> contextClass, Type contextType, Field field, TypeInfo genericInfo) -> nullableFields)
        .compile()
        .stream()
        .map(Compilation::define)
        .collect(toList())
      ).build();
    avroditeCodec = avroCodecManager.getBeanCodec(EquityMarketPriceEvent.class);
    bufferIn = new AvroInputByteBuffer(data);
  }

}
