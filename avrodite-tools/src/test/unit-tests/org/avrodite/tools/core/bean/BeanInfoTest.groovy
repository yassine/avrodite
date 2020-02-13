package org.avrodite.tools.core.bean

import org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot
import org.avrodite.fixtures.discovery.DiscoveryRootB
import spock.lang.Specification

class BeanInfoTest extends Specification {

  def discoveryBeanManager = BeanManager.builder()
    .includePackages(AbstractDiscoveryTestRoot.class.package.name)
    .build()
  def discoveryBeanManager2 = BeanManager.builder()
    .includePackages(AbstractDiscoveryTestRoot.class.package.name)
    .build()

  def "BeanInfo equality is based on the type signature"() {
    given:
    def bean = discoveryBeanManager.beansIndex().get(DiscoveryRootB.class.name)
    def bean2 = discoveryBeanManager2.beansIndex().get(DiscoveryRootB.class.name)

    expect:
    bean == bean2
    bean.signature == bean2.signature
    bean.hashCode() == bean2.hashCode()
  }

}
