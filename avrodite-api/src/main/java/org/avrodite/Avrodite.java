package org.avrodite;

import static java.lang.Thread.currentThread;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.avrodite.api.Codec;
import org.avrodite.api.CodecManager;
import org.avrodite.api.CodecStandard;
import org.avrodite.api.Configurable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class Avrodite<S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> implements CodecManager<S, C> {

  @Getter
  @Setter(PRIVATE)
  private Map<Class<?>, ?> codecIndex;

  @Getter
  private final S codecStandard;

  @SuppressWarnings("unchecked")
  @Override
  public <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Class<B> beanClass) {
    return (U) codecIndex.get(beanClass);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <B, U extends Codec<B, ?, ?, S>> U getBeanCodecAs(Class<B> beanClass, Class<? super U> codecImplementation) {
    return (U) codecIndex.get(beanClass);
  }

  public static <S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> AvroditeBuilder<S, C> builder(S standard) {
    return new AvroditeBuilder<>(requireNonNull(standard));
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Slf4j
  public static class AvroditeBuilder<S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> {

    private final S standard;

    private final Set<Package> includedPackages = new HashSet<>();
    private final Set<Class<?>> userCodecs = new HashSet<>();

    public AvroditeBuilder<S, C> discoverCodecsAt(Package... pkgs) {
      includedPackages.addAll(asList(pkgs));
      return this;
    }

    public AvroditeBuilder<S, C> include(List<Class<?>> codecs) {
      userCodecs.addAll(codecs);
      return this;
    }

    public Avrodite<S, C> build() {
      HashMap<Class<?>, C> codecIndex = new HashMap<>();
      Avrodite<S, C> avrodite = new Avrodite<>(standard);
      if (!includedPackages.isEmpty()) {
        new ClassGraph().whitelistPathsNonRecursive(
          includedPackages.stream()
            .map(Package::getName)
            .map(name -> name.replaceAll("\\.", "/"))
            .distinct()
            .toArray(String[]::new)
        ).enableAnnotationInfo()
          .enableClassInfo()
          .addClassLoader(currentThread().getContextClassLoader())
          .scan()
          .getClassesImplementing(Codec.class.getName()).stream()
          .map(ClassInfo::getName)
          .map(this::classForName)
          .filter(clazz -> !isAbstract(clazz.getModifiers()))
          .filter(clazz -> !isInterface(clazz.getModifiers()))
          .filter(standard.api().typeCodecApi()::isAssignableFrom)
          .forEach(clazz -> register(clazz, codecIndex, avrodite));
      }

      userCodecs.stream()
        .filter(clazz -> !isAbstract(clazz.getModifiers()))
        .filter(clazz -> !isInterface(clazz.getModifiers()))
        .filter(standard.api().typeCodecApi()::isAssignableFrom)
        .forEach(clazz -> register(clazz, codecIndex, avrodite));

      return avrodite.codecIndex(unmodifiableMap(codecIndex));
    }

    @SuppressWarnings("unchecked")
    private void register(Class<?> clazz, HashMap<Class<?>, C> codecIndex, Avrodite<S, C> avrodite) {
      ofNullable(AvroditeBuilder.<S, C>getBeanCodecInstance(clazz))
        .filter(codec -> standard.name().equals(codec.standard().name()) && standard.version().equals(codec.standard().version()))
        .map(codec -> codecIndex.put(getCodecTarget(clazz), codec))
        .filter(codec -> Configurable.class.isAssignableFrom(clazz))
        .ifPresent(codec -> ((Configurable<C, ?, ?, S>) codec).configure(avrodite));
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    private Class<?> getCodecTarget(Class<?> clazz) {
      return ofNullable(resolve(clazz).type(Codec.class).generics())
        .filter(generics -> !generics.isEmpty())
        .map(generics -> generics.get(0))
        .orElse((Class) Object.class);
    }

    @SneakyThrows
    Class<?> classForName(String name) {
      return Class.forName(name);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static <S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> C getBeanCodecInstance(Class<?> beanCodecClass) {
      try {
        return (C) beanCodecClass.getConstructor().newInstance();
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        return null;
      }
    }

  }

}
