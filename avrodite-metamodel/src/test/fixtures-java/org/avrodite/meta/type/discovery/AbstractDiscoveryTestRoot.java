package org.avrodite.meta.type.discovery;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class AbstractDiscoveryTestRoot<A, B, C, D, E, F> {
  private A component;
  private List<B> children;
  private Set<List<C>[]> grandGrandChildren;
  private Long longNumber;
  private long longNumber2;
  private Short shortNumber;
  private short shortNumber2;
  private Double doubleNumber;
  private double doubleNumber2;
  private Float floatNumber;
  private float floatNumber2;
  private Integer intNumber;
  private int intNumber2;
  private String string;
  //maps
  private Map<String, D> map;
  private Map<String, List<E[]>> map2;
  private F[] arr;


  public A getComponent() {
    return component;
  }

  public void setComponent(A component) {
    this.component = component;
  }

  public List<B> getChildren() {
    return children;
  }

  public void setChildren(List<B> children) {
    this.children = children;
  }

  public Set<List<C>[]> getGrandGrandChildren() {
    return grandGrandChildren;
  }

  public void setGrandGrandChildren(Set<List<C>[]> grandGrandChildren) {
    this.grandGrandChildren = grandGrandChildren;
  }

  public Long getLongNumber() {
    return longNumber;
  }

  public void setLongNumber(Long longNumber) {
    this.longNumber = longNumber;
  }

  public long getLongNumber2() {
    return longNumber2;
  }

  public void setLongNumber2(long longNumber2) {
    this.longNumber2 = longNumber2;
  }

  public Short getShortNumber() {
    return shortNumber;
  }

  public void setShortNumber(Short shortNumber) {
    this.shortNumber = shortNumber;
  }

  public short getShortNumber2() {
    return shortNumber2;
  }

  public void setShortNumber2(short shortNumber2) {
    this.shortNumber2 = shortNumber2;
  }

  public Double getDoubleNumber() {
    return doubleNumber;
  }

  public void setDoubleNumber(Double doubleNumber) {
    this.doubleNumber = doubleNumber;
  }

  public double getDoubleNumber2() {
    return doubleNumber2;
  }

  public void setDoubleNumber2(double doubleNumber2) {
    this.doubleNumber2 = doubleNumber2;
  }

  public Float getFloatNumber() {
    return floatNumber;
  }

  public void setFloatNumber(Float floatNumber) {
    this.floatNumber = floatNumber;
  }

  public float getFloatNumber2() {
    return floatNumber2;
  }

  public void setFloatNumber2(float floatNumber2) {
    this.floatNumber2 = floatNumber2;
  }

  public Integer getIntNumber() {
    return intNumber;
  }

  public void setIntNumber(Integer intNumber) {
    this.intNumber = intNumber;
  }

  public int getIntNumber2() {
    return intNumber2;
  }

  public void setIntNumber2(int intNumber2) {
    this.intNumber2 = intNumber2;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Map<String, D> getMap() {
    return map;
  }

  public void setMap(Map<String, D> map) {
    this.map = map;
  }

  public Map<String, List<E[]>> getMap2() {
    return map2;
  }

  public void setMap2(Map<String, List<E[]>> map2) {
    this.map2 = map2;
  }

  public F[] getArr() {
    return arr;
  }

  public void setArr(F[] arr) {
    this.arr = arr;
  }

}
