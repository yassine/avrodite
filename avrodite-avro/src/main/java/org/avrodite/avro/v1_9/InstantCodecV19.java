package org.avrodite.avro.v1_9;

import lombok.experimental.Accessors;
import org.avrodite.avro.AvroStandard;

@Accessors(fluent = true)
public class InstantCodecV19 extends org.avrodite.avro.value.InstantCodec {

  @Override
  public AvroStandard standard() {
    return AvroStandardV19.AVRO_1_9;
  }

}
