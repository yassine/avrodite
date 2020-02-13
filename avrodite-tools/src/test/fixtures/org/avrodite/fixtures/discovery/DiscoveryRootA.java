package org.avrodite.fixtures.discovery;

import lombok.Getter;

@Getter
public class DiscoveryRootA extends AbstractDiscoveryTestRoot<ModelA, ModelB, ModelC, ModelD, ModelE, ModelF> {
  private int[] matrix;
}
