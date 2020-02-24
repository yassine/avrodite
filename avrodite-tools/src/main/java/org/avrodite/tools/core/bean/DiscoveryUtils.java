package org.avrodite.tools.core.bean;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Collections.unmodifiableSet;
import static org.avrodite.tools.core.utils.ReflectionUtils.getFields;
import static org.avrodite.tools.core.utils.ReflectionUtils.getSuperTypes;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.util.HashSet;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.avrodite.tools.core.utils.TypeUtils;

@UtilityClass
public class DiscoveryUtils {

  /**
   * Discover the (parameterized-)types referenced by a set of discovered classes
   *
   * @param discoveredClasses the set of classes to initialize the Object graph
   * @param whiteList         A set of whitelist packages, only classes of these packages are added to the Object graph
   * @param blackList         A set of classes to exclude from the Object graph
   * @return A list of TypeInfo that includes all types referenced by the discovered classes either by inheritance or composition
   */
  public static Set<TypeInfo> discoverTypes(Set<Class<?>> discoveredClasses, Set<String> whiteList, Set<Class<?>> blackList) {
    Set<TypeInfo> allTypes = new HashSet<>();
    // add the supertypes to the party if they fulfill the scope requirements
    discoveredClasses.stream()
      .map(clazz -> new TypeInfo(clazz, clazz, clazz.getName()))
      .filter(allTypes::add)
      .forEach(clazzTypeInfo ->
        getSuperTypes(clazzTypeInfo.rawType()).stream()
          .filter(typeInfo -> blackList.stream().noneMatch(typeInfo.rawType()::equals))
          .filter(typeInfo -> whiteList.stream().anyMatch(typeInfo.rawType().getPackage().getName()::startsWith))
          .forEach(allTypes::add)
      );
    // add the fields types to the party if they fulfill the scope requirements
    new HashSet<>(allTypes).forEach(clazzTypeInfo ->
          getFields(clazzTypeInfo.rawType()).stream()
            .filter(field -> whiteList.stream().anyMatch(pkg -> field.getDeclaringClass().getPackage().getName().startsWith(pkg)))
            .map(field -> resolve(clazzTypeInfo.rawType()).inlyingType(clazzTypeInfo.type()).resolveFieldType(field))
            .map(TypeUtils::typeTarget)
            .filter(genericInfo -> discoveredClasses.contains(genericInfo.rawType()))
            .filter(genericInfo -> !isAbstract(genericInfo.rawType().getModifiers()))
            .filter(genericInfo -> !isInterface(genericInfo.rawType().getModifiers()))
            .filter(typeInfo -> blackList.stream().noneMatch(typeInfo.rawType()::equals))
            .filter(genericInfo -> !genericInfo.equals(clazzTypeInfo))
            .forEach(allTypes::add)
        );
    return unmodifiableSet(allTypes);
  }

}
