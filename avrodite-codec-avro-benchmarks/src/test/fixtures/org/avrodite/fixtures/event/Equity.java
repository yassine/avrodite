package org.avrodite.fixtures.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @EqualsAndHashCode
public class Equity {
  private String ticker;
  private double price;
  private long volume;
  private double variation;
}
