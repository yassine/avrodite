package org.avrodite.tools.core.bean;

import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.avrodite.api.ValueCodec;
import org.avrodite.tools.core.utils.TypeUtils;
import ru.vyarus.java.generics.resolver.context.container.ParameterizedTypeImpl;

@Getter
@Accessors @ToString(of = "field")
public class FieldInfo {

  private final boolean inherited;
  private final boolean nullable;
  private final FieldAccessor accessor;
  private final String typeSignature;
  private final Type type;
  private final Field field;
  private final String name;
  private final Type declaringType;
  private final TypeInfo targetTypeInfo;

  public FieldInfo(final Class<?> contextClass, final Field field, final Type fieldType, final boolean nullable) {
    this.accessor = new FieldAccessor(field);
    this.field = field;
    this.inherited = !field.getDeclaringClass().equals(contextClass);
    this.type = fieldType;
    this.typeSignature = TypeUtils.typeToString(fieldType);
    this.name = field.getName();
    this.nullable = nullable;
    this.targetTypeInfo = TypeUtils.typeTarget(fieldType);
    List<Class<?>> declaringGenerics = resolve(contextClass).type(field.getDeclaringClass()).generics();
    if (!declaringGenerics.isEmpty()) {
      this.declaringType = new ParameterizedTypeImpl(field.getDeclaringClass(), declaringGenerics.toArray(new Type[0]));
    } else {
      this.declaringType = field.getDeclaringClass();
    }
  }

  public static <V extends ValueCodec<?, ?, ?, ?>> Builder<V> builder() {
    return new Builder<>();
  }

  @Accessors(chain = true, fluent = true)
  @Setter
  @Getter
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder<V extends ValueCodec<?, ?, ?, ?>> {
    private Class<?> contextClass;
    private Field field;
    private Type fieldType;
    private boolean nullable;
    private V valueCodec;

    public Builder<V> visitWith(Consumer<Builder<V>> consumer) {
      consumer.accept(this);
      return this;
    }

    public FieldInfo build(){
      return new FieldInfo(contextClass, field, fieldType, nullable);
    }

  }

}
