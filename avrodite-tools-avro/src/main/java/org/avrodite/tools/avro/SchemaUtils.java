package org.avrodite.tools.avro;

import static java.util.Collections.unmodifiableMap;
import static org.avrodite.tools.core.utils.CryptoUtils.hashSHA256;
import static org.avrodite.tools.core.utils.TypeUtils.typeToString;
import static org.avrodite.tools.core.utils.TypeUtils.typeTrace;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.avrodite.avro.AvroStandard;
import org.avrodite.avro.AvroValueCodec;
import org.avrodite.tools.compiler.FieldType;
import org.avrodite.tools.core.bean.BeanInfo;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.core.bean.FieldInfo;
import org.avrodite.tools.template.TypeFingerprint;

@UtilityClass
@Slf4j
public class SchemaUtils {

  public static Map<String, Schema> createSchemaRegistry(BeanManager beanManager) {
    Map<String, Schema> registry = new HashMap<>();
    SchemaBuilder.TypeBuilder<Schema> schemaBuilder = SchemaBuilder.builder();
    final Map<String, Boolean> definedTypes = new HashMap<>();
    beanManager.beans().forEach(beanInfo -> {
      final String schemaName = schemaName(beanInfo.getTargetRaw(), beanInfo.getTarget());
      if (!definedTypes.containsKey(schemaName)) {
        registry.put(beanInfo.getSignature(),
          defineSchema(schemaBuilder, beanManager, beanInfo, definedTypes, registry));
      }
    });
    registry.values().forEach(schema -> log.debug(schema.toString(true)));
    return unmodifiableMap(registry);
  }

  private static Schema defineSchema(SchemaBuilder.TypeBuilder<Schema> schemaBuilder, BeanManager beanManager, BeanInfo beanInfo, final Map<String, Boolean> definedTypes, Map<String, Schema> registry) {
    final String schemaName = schemaName(beanInfo.getTargetRaw(), beanInfo.getTarget());
    if (definedTypes.containsKey(schemaName)) {
      return schemaBuilder.type(schemaName);
    }
    definedTypes.put(schemaName, true);
    SchemaBuilder.RecordBuilder<Schema> recordBuilder = schemaBuilder
      .record(schemaName)
      .prop(AvroStandard.JAVA_TYPE_SCHEMA_PROP, beanInfo.getSignature());
    SchemaBuilder.FieldAssembler<Schema> fieldAssembler = recordBuilder.fields();
    beanInfo.getFields()
      .forEach(fieldInfo ->
        fieldAssembler.name(fieldInfo.getName())
          .type(defineSchema(schemaBuilder, beanInfo, fieldInfo, beanManager, definedTypes, registry))
          .noDefault()
      );
    return fieldAssembler.endRecord();
  }

  static String schemaName(Class<?> rawType, Type type) {
    return String.join("__", rawType.getName(), hashSHA256(typeToString(type)).substring(0, 8));
  }

  static Schema defineSchema(final SchemaBuilder.TypeBuilder<Schema> base, final BeanInfo beanInfo, final FieldInfo fieldInfo, final BeanManager beanManager, final Map<String, Boolean> definedTypes, Map<String, Schema> registry) {
    final List<TypeFingerprint> fingerprints = new ArrayList<>();
    final List<String> signatures = new ArrayList<>();
    typeTrace(fieldInfo.getType(), fingerprints, signatures);
    return defineSchema(base, fingerprints, beanInfo, fieldInfo, 0, beanManager, definedTypes, registry);
  }

  static Schema defineSchema(SchemaBuilder.TypeBuilder<Schema> base, List<TypeFingerprint> fingerprints, final BeanInfo beanInfo, FieldInfo fieldInfo, int offset, BeanManager beanManager, Map<String, Boolean> definedTypes, Map<String, Schema> registry) {
    SchemaBuilder.BaseTypeBuilder<Schema> schemaBuilder
      = (offset == 0 && fieldInfo.isNullable()) ? base.nullable()
      : base;
    switch (FieldType.of(fingerprints.get(offset))) {
      case ARRAY:
        return schemaBuilder.array().items(defineSchema(base, fingerprints, beanInfo, fieldInfo, offset + 1, beanManager, definedTypes, registry));
      case MAP:
        return schemaBuilder.map().values().type(base.type(defineSchema(base, fingerprints, beanInfo, fieldInfo, offset + 1, beanManager, definedTypes, registry)));
      case SHORT:
      case INT:
        return schemaBuilder.intType();
      case LONG:
        return schemaBuilder.longType();
      case FLOAT:
        return schemaBuilder.floatType();
      case DOUBLE:
        return schemaBuilder.doubleType();
      case BOOLEAN:
        return schemaBuilder.booleanType();
      case STRING:
        return schemaBuilder.stringType();
      default:
        return beanManager.valueCodecsIndex().keySet().stream()
          .filter(type -> type.isAssignableFrom(fieldInfo.getTargetTypeInfo().rawType()))
          .map(type -> beanManager.valueCodecsIndex().get(type))
          .map(type -> (AvroValueCodec<?>) type)
          .map(AvroValueCodec::schema)
          .findAny()
          .orElseGet(() -> {
            final String schemaName = schemaName(fieldInfo.getTargetTypeInfo().rawType(), fieldInfo.getTargetTypeInfo().type());
            if (definedTypes.containsKey(schemaName)) {
              return schemaBuilder.type(schemaName);
            } else {
              Schema schema = defineSchema(base, beanManager, beanManager.beansIndex().get(fieldInfo.getTypeSignature()), definedTypes, registry);
              registry.put(fieldInfo.getTypeSignature(), schema);
              return schema;
            }
          });
    }

  }

}
