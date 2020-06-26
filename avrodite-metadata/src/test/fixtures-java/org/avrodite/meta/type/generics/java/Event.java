package org.avrodite.meta.type.generics.java;

import java.util.List;


public abstract class Event<H, B> {
  private final List<H> header;
  private final List<B> body;
  private H headerSimple;

  public Event(List<H> header, List<B> body) {
    this.header = header;
    this.body = body;
  }

  public List<H> getHeader() {
    return header;
  }

  public List<B> getBody() {
    return body;
  }

  public H getHeaderSimple() {
    return headerSimple;
  }

  public void setHeaderSimple(H headerSimple) {
    this.headerSimple = headerSimple;
  }
}
