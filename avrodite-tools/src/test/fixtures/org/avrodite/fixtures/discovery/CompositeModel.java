package org.avrodite.fixtures.discovery;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompositeModel<P, Q> {
  private P param1;
  private Q param2;
}
