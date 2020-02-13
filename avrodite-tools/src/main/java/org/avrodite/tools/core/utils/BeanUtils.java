package org.avrodite.tools.core.utils;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isProtected;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

  private static final String BOOLEAN_GETTER_PREFIX = "is";
  private static final String GETTER_PREFIX = "get";
  private static final String SETTER_PREFIX = "set";

  public static Optional<Method> accessor(
    BiPredicate<Method, Field> selector, Field field) {
    return Arrays.stream(field.getDeclaringClass().getDeclaredMethods())
      .filter(BeanUtils::isVisible)
      .filter(method -> method.getParameterCount() <= 1)
      .filter(method -> selector.test(method, field))
      .findAny();
  }

  public static boolean isVisible(Member member) {
    return !isPrivate(member.getModifiers())
      && !isStatic(member.getModifiers())
      && !isAbstract(member.getModifiers())
      && !isProtected(member.getModifiers())
      && !isFinal(member.getModifiers());
  }

  public static boolean isGetter(Method method, Field field) {

    return isGetterName(method, field)
      && method.getParameterCount() == 0
      && field.getType().isAssignableFrom(method.getReturnType());
  }

  public static boolean isSetter(Method method, Field field) {
    return isSetterName(method, field)
      && method.getParameterCount() == 1;
  }

  private static boolean isGetterName(Method method, Field field) {
    return method.getName().equals(field.getName())
      || method.getName().equalsIgnoreCase(GETTER_PREFIX + field.getName())
      || ((field.getType().equals(boolean.class) || field.getType().equals(Boolean.class))
      && method.getName().equalsIgnoreCase(BOOLEAN_GETTER_PREFIX + field.getName()));
  }

  private static boolean isSetterName(Method method, Field field) {
    return method.getName().equals(field.getName())
      || method.getName().equalsIgnoreCase(SETTER_PREFIX + field.getName());
  }
}
