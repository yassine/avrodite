package org.avrodite.tools.core.bean;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.avrodite.tools.core.bean.DiscoveryUtils.discoverTypes;
import static org.avrodite.tools.core.bean.Utils.isOfInterest;
import static org.avrodite.tools.core.utils.ReflectionUtils.getFields;
import static org.avrodite.tools.core.utils.ScanUtils.classPathScannerWith;
import static org.avrodite.tools.core.utils.TypeUtils.typeTarget;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import com.machinezoo.noexception.Exceptions;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.avrodite.api.CodecStandard;
import org.avrodite.api.ValueCodec;
import org.avrodite.tools.core.utils.ReflectionUtils;

@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor(access = PRIVATE)
@Slf4j
public class BeanManager {

  private final Set<Class<?>> targets;
  private final Set<BeanInfo> beans;
  private final Map<String, BeanInfo> beansIndex;
  private final Map<Class<?>, ? extends ValueCodec<?, ?, ?, ?>> valueCodecsIndex;

  public static <S extends CodecStandard<?, ?, ?, ?>> Builder<S> builder(S standard) {
    return new Builder<>(standard);
  }

  public static <T extends CodecStandard<?, ?, ?, ?>> Builder<T> builder() {
    return new Builder<>(null);
  }

  @RequiredArgsConstructor(access = PRIVATE)
  public static class Builder<S extends CodecStandard<?, ?, ?, ?>> {

    private final S standard;
    private final Set<Class<?>> excludeClasses = new HashSet<>();
    private final Set<ClassLoader> classLoaders = new HashSet<>();
    private final Set<String> includePackages = new HashSet<>();
    private NullableFieldPredicate nullablePredicate = (Class<?> contextClass, Type contextType, Field field, TypeInfo genericInfo) -> true;

    public Builder<S> includePackages(String... packages) {
      includePackages.addAll(asList(packages));
      return this;
    }

    public Builder<S> addClassLoaders(Set<ClassLoader> classLoaders) {
      this.classLoaders.addAll(classLoaders);
      return this;
    }

    public Builder<S> excludeClasses(Class<?>... classes) {
      this.excludeClasses.addAll(asList(classes));
      return this;
    }

    public Builder<S> nullableFieldPredicate(NullableFieldPredicate predicate) {
      ofNullable(predicate).ifPresent(p -> this.nullablePredicate = p);
      return this;
    }

    public BeanManager build() {
      ScanResult scanResult = classPathScannerWith(classLoaders).enableClassInfo().scan();

      Map<Class<?>, ValueCodec<?, ?, ?, ?>> valueCodecIndex = scanResult
        .getClassesImplementing(ValueCodec.class.getName())
        .stream()
        .map(ClassInfo::getName)
        .map(ReflectionUtils::forName)
        .filter(clazz -> excludeClasses.stream().noneMatch(clazz::equals))
        .filter(clazz -> getValueCodecValueType(clazz) != null)
        .filter(ReflectionUtils::hasNoArgsConstructor)
        .map(ReflectionUtils::<Class<? extends ValueCodec<?, ?, ?, ?>>>castTo)
        .filter(clazz -> ofNullable(standard).map(s -> standard.api().valueCodecApi().isAssignableFrom(clazz)).orElse(true))
        .map(ReflectionUtils::<ValueCodec<?, ?, ?, ?>>instantiate)
        .collect(toMap(codec -> getValueCodecValueType(codec.getClass()), identity()));

      Set<Class<?>> targets = scanResult
        .getAllClasses()
        .filter(classInfo -> includePackages.stream().anyMatch(pkg -> classInfo.getPackageName().startsWith(pkg)))
        .stream()
        .map(ClassInfo::getName)
        .map(ReflectionUtils::forName)
        .filter(clazz -> !isInterface(clazz.getModifiers()))
        .filter(clazz -> !isAbstract(clazz.getModifiers()))
        .filter(clazz -> excludeClasses.stream().noneMatch(clazz::equals))
        .collect(toSet());

      Set<BeanInfo> beans = getBeanInfo(discoverTypes(targets, includePackages, excludeClasses), valueCodecIndex);
      return Exceptions.log(BeanManager.log).get(() -> {
        Map<String, BeanInfo> beanInfos = beans.stream().collect(toMap(BeanInfo::getSignature, identity()));
        return new BeanManager(targets, beans, beanInfos, unmodifiableMap(valueCodecIndex));
      }).orElse(null);
    }

    Set<BeanInfo> getBeanInfo(Set<TypeInfo> types, Map<Class<?>, ValueCodec<?, ?, ?, ?>> valueCodecIndex) {
      return types.stream()
        .map(type -> BeanInfo.builder()
          .fields(
            getFields(type.rawType()).stream()
              .map(field -> FieldInfo.builder()
                .contextClass(type.rawType())
                .field(field)
                .fieldType(type.resolver().resolveFieldType(field))
                .nullable(nullablePredicate.test(type.rawType(), type.type(), field, typeTarget(type.resolver().resolveFieldType(field))))
                .visitWith(builder ->
                  builder.valueCodec(
                    ofNullable(valueCodecIndex.get(typeTarget(builder.fieldType()).rawType()))
                      .orElse(null)
                  )
                ).build())
              .filter(field -> valueCodecIndex.keySet().stream()
                                .anyMatch(v -> v.isAssignableFrom(field.getTargetTypeInfo().rawType()))
                              || isOfInterest(field, includePackages, excludeClasses))
              .filter(Utils::isFieldInfoTargetTypeOfInterest)
              .collect(toList())
          )
          .target(type.type())
          .targetRaw(type.rawType())
          .signature(type.signature())
          .build()
        ).collect(toSet());
    }

  }

  private static Class<?> getValueCodecValueType(Class<?> clazz) {
    return ofNullable(resolve(clazz).type(ValueCodec.class).generics())
      .filter(generics -> generics.size() == 1)
      .map(generics -> generics.get(0))
      .orElse(null);
  }

}

