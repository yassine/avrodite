package org.avrodite.tools.core.bean;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.avrodite.tools.core.utils.BeanUtils;

@RequiredArgsConstructor
@Getter
@Accessors
public class FieldAccessor {
  private final boolean visible;
  private final Method getter;
  private final Method setter;

  FieldAccessor(Field field) {
    getter = BeanUtils.accessor(BeanUtils::isGetter, field).orElse(null);
    setter = BeanUtils.accessor(BeanUtils::isSetter, field).orElse(null);
    visible = BeanUtils.isVisible(field);
  }

  public boolean isPubliclyReadable(){
    return isPublic(getter.getModifiers());
  }

  public boolean isPubliclyWritable(){
    return isPublic(setter.getModifiers());
  }

  public boolean isReadable() {
    return visible || (getter != null && !isPrivate(getter.getModifiers()));
  }

  public boolean isWritable() {
    return visible || (setter != null && !isPrivate(setter.getModifiers()));
  }

  public boolean isHidden() {
    return !isReadable() || !isWritable();
  }

}
