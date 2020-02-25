package org.avrodite.tools.avro;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.avrodite.tools.core.utils.CryptoUtils.hashSHA256;
import static org.avrodite.tools.core.utils.TypeUtils.typeToString;
import static org.avrodite.tools.core.utils.TypeUtils.typeTrace;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
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
    SchemaBuilder.TypeBuilder<Schema> schemaBuilder = SchemaBuilder.builder();
    SchemaRegistryContext context = new SchemaRegistryContext(schemaBuilder, beanManager);
    beanManager.beans().forEach(beanInfo -> {
      final String schemaName = schemaName(beanInfo.getTargetRaw(), beanInfo.getTarget());
      if (!context.definedTypes.containsKey(schemaName)) {
        context.registry.put(beanInfo.getSignature(), defineSchema(beanInfo, context));
      }
    });
    context.registry.values().forEach(schema -> log.debug(schema.toString(true)));
    return unmodifiableMap(context.registry);
  }

  private static Schema defineSchema(BeanInfo beanInfo, SchemaRegistryContext context) {
    final String schemaName = schemaName(beanInfo.getTargetRaw(), beanInfo.getTarget());
    if (context.definedTypes.containsKey(schemaName)) {
      return context.base.type(schemaName);
    }
    context.definedTypes.put(schemaName, true);
    SchemaBuilder.RecordBuilder<Schema> recordBuilder = context.base
      .record(schemaName)
      .prop(AvroStandard.JAVA_TYPE_SCHEMA_PROP, beanInfo.getSignature());
    SchemaBuilder.FieldAssembler<Schema> fieldAssembler = recordBuilder.fields();
    beanInfo.getFields()
      .forEach(fieldInfo ->
        fieldAssembler.name(fieldInfo.getName())
          .type(defineSchema(fieldInfo, context))
          .noDefault()
      );
    return fieldAssembler.endRecord();
  }

  static Schema defineSchema(final FieldInfo fieldInfo, final SchemaRegistryContext context) {
    final List<TypeFingerprint> fingerprints = new ArrayList<>();
    final List<String> signatures = new ArrayList<>();
    typeTrace(fieldInfo.getType(), fingerprints, signatures);
    return defineSchema(context, fieldInfo, unmodifiableList(fingerprints), 0);
  }

  static Schema defineSchema(final SchemaRegistryContext schemaRegistryContext, final FieldInfo fieldInfo, final List<TypeFingerprint> fingerprints, final int offset) {
    SchemaBuilder.BaseTypeBuilder<Schema> schemaBuilder
      = (offset == 0 && fieldInfo.isNullable()) ? schemaRegistryContext.base.nullable()
                                                : schemaRegistryContext.base;
    switch (FieldType.of(fingerprints.get(offset))) {
      case ARRAY:
        return schemaBuilder.array().items(defineSchema(schemaRegistryContext, fieldInfo, fingerprints, offset + 1));
      case MAP:
        return schemaBuilder.map().values().type(schemaRegistryContext.base.type(defineSchema(schemaRegistryContext, fieldInfo, fingerprints, offset + 1)));
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
        return schemaRegistryContext.beanManager.valueCodecsIndex().keySet().stream()
          .filter(type -> type.isAssignableFrom(fieldInfo.getTargetTypeInfo().rawType()))
          .map(type -> schemaRegistryContext.beanManager.valueCodecsIndex().get(type))
          .map(type -> (AvroValueCodec<?>) type)
          .map(AvroValueCodec::schema)
          .findAny()
          .orElseGet(() -> {
            final String schemaName = schemaName(fieldInfo.getTargetTypeInfo().rawType(), fieldInfo.getTargetTypeInfo().type());
            if (schemaRegistryContext.definedTypes.containsKey(schemaName)) {
              return schemaBuilder.type(schemaName);
            } else {
              Schema schema = defineSchema(schemaRegistryContext.beanManager.beansIndex().get(fieldInfo.getTypeSignature()), schemaRegistryContext);
              schemaRegistryContext.registry.put(fieldInfo.getTypeSignature(), schema);
              return schema;
            }
          });
    }

  }

  static String schemaName(Class<?> rawType, Type type) {
    return String.join("__", rawType.getName(), hashSHA256(typeToString(type)).substring(0, 8));
  }

  @RequiredArgsConstructor
  private static class SchemaRegistryContext {
    private final SchemaBuilder.TypeBuilder<Schema> base;
    private final BeanManager beanManager;
    private final Map<String, Boolean> definedTypes = new HashMap<>();
    private final Map<String, Schema> registry = new HashMap<>();
  }
}
