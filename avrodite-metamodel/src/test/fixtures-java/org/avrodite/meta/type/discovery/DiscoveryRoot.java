package org.avrodite.meta.type.discovery;

import java.util.List;
import java.util.Map;

public class DiscoveryRoot extends AbstractDiscoveryTestRoot<ModelF, ModelE, ModelD, ModelC, ModelB, ModelA> {
  private int[] matrix;
  private List<Map<String, CompositeModel<ModelA, ModelB>>> composite;
  boolean logical;
  String string;
  int nativeInt;

  public int[] getMatrix() {
    return matrix;
  }

  public void setMatrix(int[] matrix) {
    this.matrix = matrix;
  }

  public List<Map<String, CompositeModel<ModelA, ModelB>>> getComposite() {
    return composite;
  }

  public void setComposite(List<Map<String, CompositeModel<ModelA, ModelB>>> composite) {
    this.composite = composite;
  }

  public boolean isLogical() {
    return logical;
  }

  public void setLogical(boolean logical) {
    this.logical = logical;
  }

  @Override
  public String getString() {
    return string;
  }

  @Override
  public void setString(String string) {
    this.string = string;
  }

  public int getNativeInt() {
    return nativeInt;
  }

  public void setNativeInt(int nativeInt) {
    this.nativeInt = nativeInt;
  }
}
