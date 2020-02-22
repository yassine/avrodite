package org.avrodite.tools.core.bean;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.avrodite.tools.core.utils.TypeUtils.typeToRawClass;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.experimental.UtilityClass;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

@UtilityClass
public class Utils {

  public static final List<Class<?>> NUMERIC_TYPES =
    unmodifiableList(asList(double.class, Double.class, float.class, Float.class, long.class, Long.class, short.class, Short.class, int.class, Integer.class, boolean.class, Boolean.class));
  public static final List<Class<?>> DATA_STRUCTURES_TYPES =
    unmodifiableList(asList(List.class, Set.class, Queue.class, Iterable.class, Collection.class));
  public static final List<Class<?>> OTHER_PRIMITIVE_TYPES = singletonList(String.class);

  static boolean isOfInterest(FieldInfo field, Set<String> whitelist, Set<Class<?>> exclusions) {
    Class<?> actualFieldType = typeToRawClass(field.getType());
    return isFieldOfInterest(field.getField())
      && isOfInterest(actualFieldType, field.getType(), field.getTargetTypeInfo().rawType(), field.getTargetTypeInfo().type(), whitelist, exclusions)
      && isOfInterest(actualFieldType, field.getType(), field.getField().getDeclaringClass(), field.getField().getDeclaringClass(), whitelist, exclusions);
  }

  static boolean isFieldInfoTargetTypeOfInterest(FieldInfo field) {
    return (!isAbstract(field.getTargetTypeInfo().rawType().getModifiers()) || NUMERIC_TYPES.stream().anyMatch(field.getTargetTypeInfo().rawType()::equals))
      && !isInterface(field.getTargetTypeInfo().rawType().getModifiers());
  }

  static boolean isFieldOfInterest(Field field) {
    return !Modifier.isStatic(field.getModifiers())
      && !Modifier.isFinal(field.getModifiers())
      && !Modifier.isNative(field.getModifiers());
  }

  static boolean isOfInterest(Class<?> actualClass, Type actualType, Class<?> targetClass, Type targetType, Set<String> whitelist, Set<Class<?>> exclusions) {
    if (Map.class.isAssignableFrom(actualClass)) {
      GenericsContext mapGenericsContext = resolve(actualClass).inlyingType(actualType).type(Map.class);
      if (mapGenericsContext.generics().size() == 2 && mapGenericsContext.generic(0).equals(String.class)) {
        return isOfInterest(mapGenericsContext.generic(1), mapGenericsContext.genericType(1), targetClass, targetType, whitelist, exclusions);
      } else {
        return false;
      }
    }
    return isOfInterest(targetClass, whitelist, exclusions)
      && isOfActualTypeInterest(actualClass, whitelist, exclusions);
  }

  static boolean isOfInterest(Class<?> target, Set<String> whitelist, Set<Class<?>> exclusions) {
    return exclusions.stream().noneMatch(target::equals)
      && !isInterface(target.getModifiers())
      && (OTHER_PRIMITIVE_TYPES.stream().anyMatch(target::equals)
      || NUMERIC_TYPES.stream().anyMatch(target::equals)
      || whitelist.stream().anyMatch(pkg -> target.getPackage().getName().startsWith(pkg))
    );
  }

  static boolean isOfActualTypeInterest(Class<?> actualClass, Set<String> whitelist, Set<Class<?>> exclusions) {
    if (DATA_STRUCTURES_TYPES.stream().anyMatch(actualClass::equals)) {
      return true;
    } else if (actualClass.isArray()) {
      return true;
    } else {
      return isOfInterest(actualClass, whitelist, exclusions);
    }
  }

}
