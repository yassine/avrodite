package org.avrodite.tools.core.utils;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.avrodite.tools.core.bean.TypeInfo;
import ru.vyarus.java.generics.resolver.GenericsResolver;
import ru.vyarus.java.generics.resolver.context.container.ParameterizedTypeImpl;

@UtilityClass
@Slf4j
public class ReflectionUtils {

  @SneakyThrows
  public static Class<?> forName(String name) {
    return Thread.currentThread().getContextClassLoader().loadClass(name);
  }

  public static boolean hasNoArgsConstructor(Class<?> clazz) {
    try {
      Constructor<?> constructor = clazz.getConstructor();
      return Modifier.isPublic(constructor.getModifiers());
    } catch (Exception e) {
      log.debug(e.getMessage());
      return false;
    }
  }

  @SneakyThrows
  public static <V> V instantiate(Class<? extends V> clazz){
    return clazz.getConstructor().newInstance();
  }

  @SuppressWarnings("unchecked")
  public static <T> T castTo(Object type) {
    return (T) type;
  }

  public static List<TypeInfo> getSuperTypes(Class<?> baseType) {
    final List<TypeInfo> list = new ArrayList<>();
    Class<?> current = baseType;
    Class<?> helper;
    while (current != null) {
      helper = current.getSuperclass();
      if (helper != null && !helper.equals(Object.class)) {
        Type type = new ParameterizedTypeImpl(helper, GenericsResolver.resolve(current).type(helper).genericTypes().toArray(new Type[0]));
        list.add(new TypeInfo(type, helper, TypeUtils.typeToString(type)));
      }
      current = helper;
    }
    return list;
  }

  public static List<Field> getFields(Class<?> target) {
    List<Field> allFields = new ArrayList<>();
    List<Field> localFields = asList(target.getDeclaredFields());
    if (!target.getSuperclass().equals(Object.class)) {
      allFields.addAll(getFields(target.getSuperclass()));
    }
    allFields = allFields.stream()
      .filter(field -> localFields.stream()
        .noneMatch(localField -> localField.getName().equals(field.getName())))
      .collect(toList());
    allFields.addAll(localFields);
    return unmodifiableList(allFields);
  }

  public static List<Method> getDeclaredMethods(Class<?> clazz) {
    ArrayList<Method> fieldImmutableList = new ArrayList<>();
    ofNullable(clazz.getSuperclass())
      .filter(clazz2 -> !clazz2.equals(Object.class))
      .ifPresent(superClazz -> fieldImmutableList.addAll(getDeclaredMethods(superClazz)));
    fieldImmutableList.addAll(stream(clazz.getDeclaredMethods()).collect(toList()));
    return unmodifiableList(fieldImmutableList);
  }

}
