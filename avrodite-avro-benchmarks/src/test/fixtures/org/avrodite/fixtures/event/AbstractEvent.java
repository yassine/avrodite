package org.avrodite.fixtures.event;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public abstract class AbstractEvent<M extends EventMeta, T> {
  private M meta;
  private T target;
}
