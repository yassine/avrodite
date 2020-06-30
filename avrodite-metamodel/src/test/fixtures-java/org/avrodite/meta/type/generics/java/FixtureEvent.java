package org.avrodite.meta.type.generics.java;

import java.util.List;

public class FixtureEvent extends Event<Header, Body> {
  private String prop;

  public FixtureEvent(List<Header> header, List<Body> body) {
    super(header, body);
  }

  public String getProp() {
    return prop;
  }

  public void setProp(String prop) {
    this.prop = prop;
  }
}
