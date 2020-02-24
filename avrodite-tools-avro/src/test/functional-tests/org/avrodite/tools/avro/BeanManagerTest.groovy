package org.avrodite.tools.avro

import org.avrodite.fixtures.cyclic.BeanA
import org.avrodite.fixtures.cyclic.BeanB
import org.avrodite.tools.core.bean.BeanManager
import org.avrodite.tools.core.bean.NullableFieldPredicate
import org.avrodite.tools.core.bean.TypeInfo
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Type

class BeanManagerTest extends Specification {

  def "schema creation for cyclic type"() {
    given:
    def cyclicBeanManager = BeanManager.builder()
      .includePackages(BeanA.class.package.name)
      .nullableFieldPredicate(new NullableFieldPredicate() {
        @Override
        boolean test(Class<?> contextClass, Type contextType, Field field, TypeInfo typeInfo) {
          return false
        }
      })
      .build()
    def index = SchemaUtils.createSchemaRegistry(cyclicBeanManager)

    expect:
    index[BeanA.class.name] != null
    index[BeanA.class.name].fields[0].name() == 'beanB'
    SchemaUtils.schemaName(BeanB.class, BeanB.class).endsWith(index[BeanA.class.name].fields[0].schema().name )
    index[BeanB.class.name] != null
    index[BeanB.class.name].fields[0].name() == 'beanA'
    SchemaUtils.schemaName(BeanA.class, BeanA.class).endsWith(index[BeanB.class.name].fields[0].schema().name )
  }
}
