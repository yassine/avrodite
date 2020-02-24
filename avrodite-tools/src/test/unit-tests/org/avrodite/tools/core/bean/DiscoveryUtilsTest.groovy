package org.avrodite.tools.core.bean

import org.avrodite.fixtures.event.EquityMarketPriceEvent
import org.avrodite.tools.core.utils.ScanUtils
import spock.lang.Specification

import java.lang.reflect.Modifier
import java.util.stream.Collectors

import static org.avrodite.tools.core.bean.DiscoveryUtils.discoverTypes

class DiscoveryUtilsTest extends Specification {

  def "test getTypeSequence"() {
    given:
    def set = ScanUtils.classPathScannerWith(Collections.singleton(EquityMarketPriceEvent.class.package), new HashSet<>())
      .enableClassInfo().scan()
      .getAllClasses().stream()
      .filter({ !Modifier.isAbstract(it.getModifiers()) })
      .map({ Class.forName(it.name) })
      .collect(Collectors.toSet())

    def result = discoverTypes(set, Collections.singleton(EquityMarketPriceEvent.class.package.name), new HashSet<>())
      .stream()
      .map({ it.signature() })
      .collect(Collectors.toSet())

    expect:

    result == [
        "org.avrodite.fixtures.event.Equity",
        "org.avrodite.fixtures.event.EventMeta",
        "org.avrodite.fixtures.event.AbstractEvent<org.avrodite.fixtures.event.EventMeta,org.avrodite.fixtures.event.Equity>",
        "org.avrodite.fixtures.event.EquityMarketPriceEvent"
      ] as Set
  }

}
