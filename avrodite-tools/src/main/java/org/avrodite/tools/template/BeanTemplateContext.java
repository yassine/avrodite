package org.avrodite.tools.template;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static org.avrodite.tools.core.utils.CryptoUtils.hashSHA256;
import static org.avrodite.tools.core.utils.CryptoUtils.randomBase64;

import java.util.List;
import lombok.Getter;
import org.avrodite.api.CodecStandard;
import org.avrodite.tools.core.bean.BeanInfo;
import org.avrodite.tools.core.bean.BeanManager;

@Getter
public class BeanTemplateContext {

  private final BeanManager beanManager;
  private final BeanInfo bean;
  private final List<FieldTemplateContext> fields;
  private final CodecStandard codecStandard;

  public BeanTemplateContext(BeanManager beanManager, BeanInfo bean, CodecStandard codecStandard) {
    this.beanManager = beanManager;
    this.bean   = bean;
    this.fields = bean.getFields().stream()
      .map(field -> new FieldTemplateContext(field, codecStandard, beanManager))
      .collect(toList());
    this.codecStandard = codecStandard;
  }

  public String random() {
    return hashSHA256(valueOf(randomBase64(12))).substring(0, 8);
  }

  public String codecName() {
    return Utils.codecName(bean.getTargetRaw(), bean.getSignature(), codecStandard);
  }

  public String codecFqName() {
    return Utils.codecFqName(bean.getTargetRaw(), bean.getSignature(), codecStandard);
  }

  public boolean hasHiddenFields() {
    return bean.getFields().stream()
      .filter(fieldInfo -> !fieldInfo.isInherited())
      .anyMatch(fieldInfo -> fieldInfo.getAccessor().isHidden());
  }

}
