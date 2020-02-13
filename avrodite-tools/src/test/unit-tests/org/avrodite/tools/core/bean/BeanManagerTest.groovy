package org.avrodite.tools.core.bean

import org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot
import spock.lang.Specification

class BeanManagerTest extends Specification {

  def beanManager = BeanManager.builder()
    .includePackages(AbstractDiscoveryTestRoot.class.package.name)
    .build()

  def "it should discover all the models from the user provided packages"() {
    expect:
    beanManager.beansIndex().keySet() == [
      "org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot<org.avrodite.fixtures.discovery.ModelA,org.avrodite.fixtures.discovery.ModelB,org.avrodite.fixtures.discovery.ModelC,org.avrodite.fixtures.discovery.ModelD,org.avrodite.fixtures.discovery.ModelE,org.avrodite.fixtures.discovery.ModelF>",
      "org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot<org.avrodite.fixtures.discovery.ModelF,org.avrodite.fixtures.discovery.ModelE,org.avrodite.fixtures.discovery.ModelD,org.avrodite.fixtures.discovery.ModelC,org.avrodite.fixtures.discovery.ModelB,org.avrodite.fixtures.discovery.ModelA>",
      "org.avrodite.fixtures.discovery.CompositeModel",
      "org.avrodite.fixtures.discovery.CompositeModel<org.avrodite.fixtures.discovery.ModelA,org.avrodite.fixtures.discovery.ModelB>",
      "org.avrodite.fixtures.discovery.DiscoveryRootA",
      "org.avrodite.fixtures.discovery.DiscoveryRootB",
      "org.avrodite.fixtures.discovery.ModelA",
      "org.avrodite.fixtures.discovery.ModelB",
      "org.avrodite.fixtures.discovery.ModelC",
      "org.avrodite.fixtures.discovery.ModelD",
      "org.avrodite.fixtures.discovery.ModelE",
      "org.avrodite.fixtures.discovery.ModelF"
    ] as Set
  }
}
