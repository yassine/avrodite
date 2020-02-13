package org.avrodite.avro.value;

import static com.machinezoo.noexception.Exceptions.sneak;
import static java.time.Instant.ofEpochMilli;
import static org.avrodite.avro.AvroStandard.JAVA_TYPE_SCHEMA_PROP;

import java.time.Instant;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.avrodite.api.error.CodecException;
import org.avrodite.avro.AvroInputByteBuffer;
import org.avrodite.avro.AvroOutputByteBuffer;
import org.avrodite.avro.AvroValueCodec;

@Accessors(fluent = true)
public abstract class InstantCodec implements AvroValueCodec<Instant> {

  @Getter
  private final Schema schema = sneak().get(() -> {
    Schema s = SchemaBuilder.builder().longType();
    s.addProp(JAVA_TYPE_SCHEMA_PROP, Instant.class.getName());
    return s;
  });

  @Override
  public Instant decode(AvroInputByteBuffer buffer) throws CodecException {
    return ofEpochMilli(buffer.readLong());
  }

  @Override
  public void encode(Instant value, AvroOutputByteBuffer buffer) throws CodecException {
    buffer.writeLong(value.toEpochMilli());
  }

}
