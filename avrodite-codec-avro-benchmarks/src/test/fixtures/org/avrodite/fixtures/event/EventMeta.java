package org.avrodite.fixtures.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public class EventMeta {
  private String id;
  private String parentId;
  private String correlation;
}
