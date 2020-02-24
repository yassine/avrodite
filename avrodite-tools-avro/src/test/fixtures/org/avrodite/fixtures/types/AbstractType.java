package org.avrodite.fixtures.types;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class AbstractType<P extends AbstractType, M> {
  private M meta;
  private boolean test;
  private Instant instant;
  private float[] floats;
  private int count;
  private P parent;
}
