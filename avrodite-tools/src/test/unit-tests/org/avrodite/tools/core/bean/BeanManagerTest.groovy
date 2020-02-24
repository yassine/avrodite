package org.avrodite.tools.core.bean

import org.avrodite.fixtures.cyclic.BeanA
import org.avrodite.fixtures.cyclic.BeanB
import org.avrodite.fixtures.discovery.AbstractDiscoveryTestRoot
import spock.lang.Specification

import static java.util.stream.Collectors.toSet

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

  def "cyclic dependencies are correctly discovered"() {
    given:
    def cyclicBeanManager = BeanManager.builder()
      .includePackages(BeanA.class.package.name)
      .build()

    expect:
    cyclicBeanManager.beansIndex().values().stream()
      .map({it.targetRaw})
      .collect(toSet()) == [BeanA.class, BeanB.class] as Set
  }

}
