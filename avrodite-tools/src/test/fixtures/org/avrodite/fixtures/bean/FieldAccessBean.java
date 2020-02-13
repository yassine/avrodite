package org.avrodite.fixtures.bean;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class FieldAccessBean {
  @Getter @Setter
  private String accessible1;
  @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE)
  private String accessible2;
  String accessible3;
  public int accessible4;
  @Getter @Setter
  private boolean accessible5;

  private String inaccessible1;
  protected String inaccessible2;
  @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED)
  protected String inaccessible3;

}
