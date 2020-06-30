package org.avrodite.meta.type.discovery;

public class CompositeModel<P, Q> {
  private P param1;
  private Q param2;

  public P getParam1() {
    return param1;
  }

  public void setParam1(P param1) {
    this.param1 = param1;
  }

  public Q getParam2() {
    return param2;
  }

  public void setParam2(Q param2) {
    this.param2 = param2;
  }
}
