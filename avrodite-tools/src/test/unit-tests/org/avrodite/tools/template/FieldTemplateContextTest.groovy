package org.avrodite.tools.template

import org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot
import org.avrodite.fixtures.discovery.DiscoveryRootB
import org.avrodite.tools.core.bean.BeanManager
import org.avrodite.tools.core.bean.FieldInfo
import spock.lang.Specification

import java.util.function.Function
import java.util.stream.Collectors

class FieldTemplateContextTest extends Specification {

  static def discoveryBeanManager = BeanManager.builder().includePackages(AbstractDiscoveryTestRoot.class.package.name).build()
  static def bean = discoveryBeanManager.beansIndex().get(DiscoveryRootB.class.name)
  static def beanFields = bean.fields.stream().collect(Collectors.toMap({ FieldInfo field -> field.name }, Function.identity()))

  def "A type wrapped by multiple levels of datastructures should have correct type fingerprints "() {
    given:
    //'A set of a list of an array of a type' field
    def compositeDatastructureType1 = beanFields['grandGrandChildren']
    def fieldTemplateContext = new FieldTemplateContext(compositeDatastructureType1, null, null)
    expect:
    fieldTemplateContext.fingerprints == [
      TypeFingerprint.SET,
      TypeFingerprint.ARRAY,
      TypeFingerprint.LIST,
      TypeFingerprint.TYPE
    ] as List
  }

  def "A type wrapped by multiple levels of datastructures should have correct type signatures "() {
    given:
    //'A set of a list of an array of a type' field
    def compositeDatastructureType1 = beanFields['grandGrandChildren']
    def fieldTemplateContext = new FieldTemplateContext(compositeDatastructureType1, null, null)
    expect:
    fieldTemplateContext.signatures == [
      'java.util.List<org.avrodite.fixtures.discovery.ModelD>[]',
      'java.util.List<org.avrodite.fixtures.discovery.ModelD>',
      'org.avrodite.fixtures.discovery.ModelD'
    ]
  }

}
