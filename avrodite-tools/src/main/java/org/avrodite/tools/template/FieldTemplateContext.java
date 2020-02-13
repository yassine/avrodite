package org.avrodite.tools.template;

import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;
import static org.avrodite.tools.core.utils.TypeUtils.typeToString;
import static org.avrodite.tools.template.Utils.codecFqName;
import static org.avrodite.tools.utils.TypeUtils.typeTrace;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.avrodite.api.CodecStandard;
import org.avrodite.api.ValueCodec;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.core.bean.FieldInfo;

@Getter
public class FieldTemplateContext {

  private final FieldInfo info;
  private final List<TypeFingerprint> fingerprints;
  private final List<String> signatures;
  private final CodecStandard<?, ?, ?, ?> codecStandard;
  private final ValueCodec<?, ?, ?, ?> valueCodec;

  public FieldTemplateContext(FieldInfo info, CodecStandard<?, ?, ?, ?> codecStandard, BeanManager beanManager) {
    final List<TypeFingerprint> typeFingerprints = new ArrayList<>();
    final List<String> signaturesList = new ArrayList<>();
    typeTrace(info.getType(), typeFingerprints, signaturesList);
    this.fingerprints  = unmodifiableList(typeFingerprints);
    this.signatures    = unmodifiableList(signaturesList);
    this.codecStandard = codecStandard;
    this.info          = info;
    this.valueCodec    = ofNullable(beanManager)
                            .flatMap(b -> b.valueCodecsIndex().keySet()
                              .stream()
                              .filter(type -> type.isAssignableFrom(info.getTargetTypeInfo().rawType()))
                              .findAny()
                              .map(b.valueCodecsIndex()::get)
                            ).orElse(null);
  }

  public boolean isValueType(){
    return valueCodec != null;
  }

  public String valueCodecName(){
    return ofNullable(valueCodec).map(Object::getClass).map(Class::getName).orElse(null);
  }

  public String targetEncoderClassFqName() {
    return codecFqName(info.getTargetTypeInfo().rawType(), typeToString(info.getTargetTypeInfo().type()), codecStandard);
  }

  public String declaringClassEncoderFqName() {
    return codecFqName(info.getField().getDeclaringClass(), typeToString(info.getDeclaringType()), codecStandard);
  }

  public String nullValue() {
    switch (info.getField().getType().getName()) {
      case "boolean":
        return "false";
      case "int":
      case "double":
      case "float":
      case "short":
      case "long":
      case "char":
        return "0";
      default:
        return "null";
    }
  }

}
