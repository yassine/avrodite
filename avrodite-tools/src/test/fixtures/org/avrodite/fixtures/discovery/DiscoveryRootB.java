package org.avrodite.fixtures.discovery;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DiscoveryRootB extends AbstractDiscoveryTestRoot<ModelF, ModelE, ModelD, ModelC, ModelB, ModelA> {
  private int[] matrix;
  private List<Map<String, CompositeModel<ModelA, ModelB>>> composite;
  boolean logical;
  String string;
  int nativeInt;
}
