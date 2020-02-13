package org.avrodite.fixtures.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true) @ToString @EqualsAndHashCode
public class Equity {
  private String ticker;
  private double price;
  private long volume;
  private double variation;
}
