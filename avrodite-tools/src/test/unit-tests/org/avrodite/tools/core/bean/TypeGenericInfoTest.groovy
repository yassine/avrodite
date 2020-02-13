package org.avrodite.tools.core.bean

import org.avrodite.fixtures.discovery.*
import spock.lang.Specification

import java.util.function.Function
import java.util.stream.Collectors

class TypeGenericInfoTest extends Specification {

  static def discoveryBeanManager = BeanManager.builder().includePackages(AbstractDiscoveryTestRoot.class.package.name).build()
  static def bean = discoveryBeanManager.beansIndex().get(DiscoveryRootB.class.name)
  static def beanFields = bean.fields.stream().collect(Collectors.toMap({ FieldInfo field -> field.name }, Function.identity()))

  def "field target type maps to the type representing or wrapped by a datastructure-like compositions of that given target type "() {
    given:
    def compositeDataStructureType1 = beanFields['grandGrandChildren']
    def compositeDataStructureType2 = beanFields['map2']
    def simpleType2 = beanFields['component']

    expect:
    compositeDataStructureType1.targetTypeInfo.rawType() == ModelD.class
    compositeDataStructureType2.targetTypeInfo.rawType() == ModelB.class
    simpleType2.targetTypeInfo.rawType() == ModelF.class
  }

  def "field target type signature maps to the type signature representing wrapped by a datastructure like composition of that given target type "() {
    given:
    // A parameterized type field with a wrapping datastructure
    def field = beanFields['composite']

    expect:
    field.targetTypeInfo.signature() == 'org.avrodite.fixtures.discovery.CompositeModel<org.avrodite.fixtures.discovery.ModelA,org.avrodite.fixtures.discovery.ModelB>'
  }

}
