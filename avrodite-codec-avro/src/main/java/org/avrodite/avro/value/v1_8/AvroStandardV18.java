package org.avrodite.avro.value.v1_8;

import org.avrodite.avro.AvroStandard;

public class AvroStandardV18 extends AvroStandard {
  private static final String VERSION = "1.8.1";
  public static final AvroStandardV18 AVRO_1_8 = new AvroStandardV18();

  private AvroStandardV18() {
    super(VERSION);
  }

}
