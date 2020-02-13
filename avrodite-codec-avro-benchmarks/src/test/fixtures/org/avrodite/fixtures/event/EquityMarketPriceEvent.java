package org.avrodite.fixtures.event;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EquityMarketPriceEvent extends AbstractEvent<EventMeta, Equity> {

  private List<EquityOrder> bid;
  private List<EquityOrder> ask;

  public static EquityMarketPriceEvent create(){
    double price = 124.214;

    EquityMarketPriceEvent event = new EquityMarketPriceEvent();
    EventMeta meta = new EventMeta();
    Equity equity = new Equity();
    meta.setCorrelation(randomUUID().toString());
    meta.setId(randomUUID().toString());
    meta.setParentId(randomUUID().toString());
    equity.setPrice(124.214);
    equity.setTicker("TICKER");
    equity.setVolume(125855214L);
    equity.setVariation(-0.0112);
    event.setMeta(meta);
    event.setTarget(equity);
    event.setBid(IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        EquityOrder order = new EquityOrder();
        order.setPrice(price * (1 - i * 0.005) );
        order.setCount(i);
        order.setQuantity(100000 * (1 + i));
        return order;
      }).collect(Collectors.toList())
    );
    event.setAsk(IntStream.range(1, 6)
      .boxed()
      .map(i -> {
        EquityOrder order = new EquityOrder();
        order.setPrice(price * (1 + i * 0.005) );
        order.setCount(i);
        order.setQuantity(10000 * (1 + i - 1));
        return order;
      }).collect(Collectors.toList())
    );

    return event;
  }

}
