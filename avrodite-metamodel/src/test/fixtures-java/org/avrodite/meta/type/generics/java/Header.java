package org.avrodite.meta.type.generics.java;

import java.util.Map;

public class Header {
  private Map<String, String> meta;

  public Map<String, String> getMeta() {
    return meta;
  }

  public void setMeta(Map<String, String> meta) {
    this.meta = meta;
  }
}
