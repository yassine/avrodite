package org.avrodite.avro.v1_9;

import org.avrodite.avro.AvroStandard;

public class AvroStandardV19 extends AvroStandard {
  public static final AvroStandardV19 AVRO_1_9 = new AvroStandardV19();
  private static final String VERSION = "1.9.1";
  private AvroStandardV19() {
    super(VERSION);
  }
}
