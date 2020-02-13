package org.avrodite.fixtures.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @EqualsAndHashCode
public class EquityOrder {
  private int count;
  private double price;
  private long quantity;
}
