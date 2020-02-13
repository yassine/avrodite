package org.avrodite.fixtures.discovery;

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
  private IgnoredInterface shouldNotBeDiscovered;
}
