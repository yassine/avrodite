package org.avrodite.tools.core.bean

import spock.lang.Specification

class UtilsSpec extends Specification {

  def "A type is of interest if it is of common primitive type (numeric or String)"() {
    expect:
    Utils.isOfInterest(double.class, double.class, double.class, double.class, [] as Set, [] as Set)
    Utils.isOfInterest(Double.class, Double.class, Double.class, Double.class, [] as Set, [] as Set)
    Utils.isOfInterest(float.class, float.class, float.class, float.class, [] as Set, [] as Set)
    Utils.isOfInterest(Float.class, Float.class, Float.class, Float.class, [] as Set, [] as Set)
    Utils.isOfInterest(int.class, int.class, int.class, int.class, [] as Set, [] as Set)
    Utils.isOfInterest(Integer.class, Integer.class, Integer.class, Integer.class, [] as Set, [] as Set)
    Utils.isOfInterest(short.class, short.class, short.class, short.class, [] as Set, [] as Set)
    Utils.isOfInterest(Short.class, Short.class, Short.class, Short.class, [] as Set, [] as Set)
    Utils.isOfInterest(String.class, String.class, String.class, String.class, [] as Set, [] as Set)
  }

}
