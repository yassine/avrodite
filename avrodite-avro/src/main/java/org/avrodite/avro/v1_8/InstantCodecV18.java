package org.avrodite.avro.v1_8;

import lombok.experimental.Accessors;
import org.avrodite.avro.AvroStandard;

@Accessors(fluent = true)
public class InstantCodecV18 extends org.avrodite.avro.value.InstantCodec {

  @Override
  public AvroStandard standard() {
    return AvroStandardV18.AVRO_1_8;
  }

}
