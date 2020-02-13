package org.avrodite.fixtures.event;

import static java.util.UUID.randomUUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter @Setter @EqualsAndHashCode(callSuper = true) @Accessors(chain = true)
@ToString(callSuper = true)
public class EquityMarketPriceEvent extends AbstractEvent<EventMeta, Equity> {

  public static EquityMarketPriceEvent create(){
    return (EquityMarketPriceEvent) new EquityMarketPriceEvent()
      .setMeta(new EventMeta()
        .setCorrelation(randomUUID().toString())
        .setId(randomUUID().toString())
        .setParentId(randomUUID().toString())
      ).setTarget(new Equity()
        .setPrice(124.214)
        .setTicker("TICKER")
        .setVolume(125855214L)
        .setVariation(-0.0112)
      );
  }

}
