package org.avrodite.tools.core.bean

import org.avrodite.fixtures.discovery.*
import spock.lang.Specification

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.function.Function
import java.util.stream.Collectors

class FieldInfoTest extends Specification {

  static def discoveryBeanManager = BeanManager.builder()
    .includePackages(AbstractDiscoveryTestRoot.class.package.name)
    .build()
  static def bean = discoveryBeanManager.beansIndex().get(DiscoveryRootB.class.name)
  static def beanFields = bean.fields.stream()
    .collect(Collectors.toMap({ FieldInfo field -> field.name }, Function.identity()))


  def "(non) inherited fields shall be labeled as so"() {
    given:
    def baseField = beanFields['matrix']
    def inheritedField = beanFields['component']
    expect:
    !baseField.inherited
    inheritedField.inherited
  }

  def "field declaring type should reference its resolved type-parameters"() {
    given:
    def simpleTypedField = beanFields['matrix']
    def parametrizedTypedField = beanFields['children']
    def parametrizedTypedFieldDeclaringType = parametrizedTypedField.declaringType as ParameterizedType

    expect:
    parametrizedTypedFieldDeclaringType.actualTypeArguments == [
      ModelF.class as Type,
      ModelE.class as Type,
      ModelD.class as Type,
      ModelC.class as Type,
      ModelB.class as Type,
      ModelA.class as Type,
    ] as Type[]
    !(simpleTypedField.declaringType instanceof ParameterizedType)
  }

  def "bean fields of Interest should be discovered and resolve to their correct (parameterized-)type signatures"() {
    given:
    def fieldsSignatures = beanFields.values().stream()
      .collect(Collectors.toMap({ ((FieldInfo) it).name }, { ((FieldInfo) it).typeSignature }))

    expect:
    fieldsSignatures == [
      'arr'               : 'org.avrodite.fixtures.discovery.ModelA[]',
      'children'          : 'java.util.List<org.avrodite.fixtures.discovery.ModelE>',
      'component'         : 'org.avrodite.fixtures.discovery.ModelF',
      'composite'         : 'java.util.List<java.util.Map<java.lang.String,org.avrodite.fixtures.discovery.CompositeModel<org.avrodite.fixtures.discovery.ModelA,org.avrodite.fixtures.discovery.ModelB>>>',
      'doubleNumber'      : 'java.lang.Double',
      'doubleNumber2'     : 'double',
      'floatNumber'       : 'java.lang.Float',
      'floatNumber2'      : 'float',
      'grandGrandChildren': 'java.util.Set<java.util.List<org.avrodite.fixtures.discovery.ModelD>[]>',
      'intNumber'         : 'java.lang.Integer',
      'intNumber2'        : 'int',
      'logical'           : 'boolean',
      'longNumber'        : 'java.lang.Long',
      'longNumber2'       : 'long',
      'map'               : 'java.util.Map<java.lang.String,org.avrodite.fixtures.discovery.ModelC>',
      'map2'              : 'java.util.Map<java.lang.String,java.util.List<org.avrodite.fixtures.discovery.ModelB[]>>',
      'matrix'            : 'int[]',
      'nativeInt'         : 'int',
      'shortNumber'       : 'java.lang.Short',
      'shortNumber2'      : 'short',
      'string'            : 'java.lang.String',
    ]
  }
}
