package org.avrodite.tools.avro

import org.apache.avro.Schema
import org.avrodite.avro.v1_8.AvroStandardV18
import org.avrodite.fixtures.types.ConcreteType
import org.avrodite.tools.core.bean.BeanManager
import org.avrodite.tools.core.bean.NullableFieldPredicate
import org.avrodite.tools.core.bean.TypeInfo
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Type

import static java.util.stream.Collectors.toMap

class ConcreteSpec extends Specification {

  def "schema definitions for the parametrized fixture are correct"(){
    given:
    def beanManager = BeanManager.builder(AvroStandardV18.AVRO_1_8)
      .includePackages(ConcreteType.class.package.name)
      .nullableFieldPredicate(new NullableFieldPredicate() {
        @Override
        boolean test(Class<?> contextClass, Type contextType, Field field, TypeInfo typeInfo) {
          return false
        }
      })
      .build()
    def schemaIndex = SchemaUtils.createSchemaRegistry(beanManager)
    def concreteSchema = schemaIndex.get(ConcreteType.class.name)
    Map<String, Schema.Field> fieldIndex = concreteSchema.fields.stream()
      .collect(toMap({ Schema.Field  field -> field.name() }, { it }))

    expect:
    /*
      field: parent
      description: a type parameter field that resolves to a self reference
     */
    fieldIndex['parent'] != null
    fieldIndex['parent'].schema().namespace == ConcreteType.class.package.name
    fieldIndex['parent'].schema().type == Schema.Type.RECORD
    SchemaUtils.schemaName(ConcreteType.class, ConcreteType.class)
      .endsWith(fieldIndex['parent'].schema().name)

    /*
      field: instant
      description: the value type is not supported directly, a ValueCodec SPI handles it. Therefore it should be included
    */
    fieldIndex['instant'] != null
    fieldIndex['instant'].schema().type == Schema.Type.LONG

    /*
      field: meta
      description: a generic type that resolves to Map<String, String>
    */
    fieldIndex['meta'] != null
    fieldIndex['meta'].schema().type == Schema.Type.MAP
    fieldIndex['meta'].schema().valueType.type == Schema.Type.STRING

    /*
      field: test
      description: a boolean field
    */
    fieldIndex['test'] != null
    fieldIndex['test'].schema().type == Schema.Type.BOOLEAN

    /*
      field: floats
      description: an array of floats
    */
    fieldIndex['floats'] != null
    fieldIndex['floats'].schema().type == Schema.Type.ARRAY
    fieldIndex['floats'].schema().elementType.type == Schema.Type.FLOAT

    /*
      field: int
      description: an int field
    */
    fieldIndex['count'] != null
    fieldIndex['count'].schema().type == Schema.Type.INT

  }
}
