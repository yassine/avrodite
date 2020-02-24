package org.avrodite.fixtures.cyclic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BeanA {
  BeanB beanB;
}
