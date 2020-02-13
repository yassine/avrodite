package org.avrodite.fixtures.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain = true)
@EqualsAndHashCode @ToString
public class EventMeta {
  private String id;
  private String parentId;
  private String correlation;
  //private Instant instant;
}
