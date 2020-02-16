package org.avrodite.avro.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;

public class JacksonJsonBenchmarkState {
  public final ObjectMapper mapper = new ObjectMapper();
  public final EquityMarketPriceEvent model = EquityMarketPriceEvent.create();
}
