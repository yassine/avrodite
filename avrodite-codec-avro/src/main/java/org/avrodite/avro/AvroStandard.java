package org.avrodite.avro;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.avrodite.Avrodite;
import org.avrodite.api.CodecStandard;
import org.avrodite.api.CodecStandardApi;
import org.avrodite.avro.value.v1_9.AvroStandardV19;

@Accessors(fluent = true)
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AvroStandard implements CodecStandard<AvroInputByteBuffer, AvroOutputByteBuffer, AvroCodec<?>, AvroValueCodec<?>> {

  public static final String JAVA_TYPE_SCHEMA_PROP = "org.avrodite.avro.javaType";
  private static final String AVRO_NAME = "AVRO";

  @Getter
  private final String version;

  @Override
  public String name() {
    return AVRO_NAME;
  }

  @Override
  public CodecStandardApi<AvroInputByteBuffer, AvroOutputByteBuffer, AvroCodec<?>, AvroValueCodec<?>> api() {
    return API;
  }

  public static Avrodite.AvroditeBuilder<AvroStandard, AvroCodec<?>> avrodite() {

    return Avrodite.builder(AvroStandardV19.AVRO_1_9);
  }

  private static final CodecStandardApi<AvroInputByteBuffer, AvroOutputByteBuffer, AvroCodec<?>, AvroValueCodec<?>> API = new CodecStandardApi<AvroInputByteBuffer, AvroOutputByteBuffer, AvroCodec<?>, AvroValueCodec<?>>() {
    @Override
    public Class<AvroInputByteBuffer> inputApi() {
      return AvroInputByteBuffer.class;
    }

    @Override
    public Class<AvroOutputByteBuffer> outputApi() {
      return AvroOutputByteBuffer.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<AvroCodec<?>> typeCodecApi() {
      return (Class) AvroCodec.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<AvroValueCodec<?>> valueCodecApi() {
      return (Class) AvroValueCodec.class;
    }
  };

}
