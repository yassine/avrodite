package org.avrodite.avro.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;

public class JacksonJsonBenchmarkState {
  public static final byte[] SERIALIZED_JSON_BYTES;

  static {
    try {
      SERIALIZED_JSON_BYTES = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResource("event-fixture.json"));
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }

  public final ObjectMapper mapper = new ObjectMapper();
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
  public final ByteArrayInputStream is = new ByteArrayInputStream(SERIALIZED_JSON_BYTES);
  public final ByteArrayOutputStream os = new ByteArrayOutputStream(2048);

}
