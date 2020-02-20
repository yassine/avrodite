package org.avrodite.avro.value;

import static java.time.Instant.ofEpochMilli;
import static org.avrodite.avro.AvroStandard.JAVA_TYPE_SCHEMA_PROP;

import java.time.Instant;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.avrodite.api.error.CodecException;
import org.avrodite.avro.AvroInputByteBuffer;
import org.avrodite.avro.AvroOutputByteBuffer;
import org.avrodite.avro.AvroValueCodec;

@Accessors(fluent = true) @Slf4j
public abstract class InstantCodec implements AvroValueCodec<Instant> {

  @Getter
  private final Schema schema;

  public InstantCodec() {
    Schema helper = null;
    try {
      helper = SchemaBuilder.builder().longType();
      helper.addProp(JAVA_TYPE_SCHEMA_PROP, Instant.class.getName());
    }catch (Exception e){
      log.error(e.getMessage(), e);
    }
    schema = helper;
  }

  @Override
  public Instant decode(AvroInputByteBuffer buffer) throws CodecException {
    return ofEpochMilli(buffer.readLong());
  }

  @Override
  public void encode(Instant value, AvroOutputByteBuffer buffer) throws CodecException {
    buffer.writeLong(value.toEpochMilli());
  }

}
