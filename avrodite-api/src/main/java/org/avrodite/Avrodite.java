package org.avrodite;

import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isInterface;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.Spliterators.spliteratorUnknownSize;
import static lombok.AccessLevel.PRIVATE;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
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
  private Map<Type, ?> codecIndex;

  @Getter
  private final S codecStandard;

  @SuppressWarnings("unchecked")
  @Override
  public <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Class<B> beanClass) {
    return (U) codecIndex.get(beanClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <B, U extends Codec<B, ?, ?, S>> U getBeanCodec(Type beanType) {
    return (U) codecIndex.get(beanType);
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

    @Deprecated
    public AvroditeBuilder<S, C> discoverCodecsAt(Package... pkgs) {
      includedPackages.addAll(asList(pkgs));
      return this;
    }

    public AvroditeBuilder<S, C> include(List<Class<?>> codecs) {
      userCodecs.addAll(codecs);
      return this;
    }

    @SuppressWarnings("unchecked")
    public Avrodite<S, C> build() {
      HashMap<Type, C> codecIndex = new HashMap<>();
      Avrodite<S, C> avrodite = new Avrodite<>(standard);
      StreamSupport.stream(spliteratorUnknownSize(ServiceLoader.load(standard.api().typeCodecApi()).iterator(), Spliterator.ORDERED), false)
        .filter(Objects::nonNull)
        .filter(codec -> standard.name().equals(codec.standard().name()) && standard.version().equals(codec.standard().version()))
        .map(codec -> codecIndex.computeIfAbsent(getCodecTarget(codec.getClass()), a -> codec))
        .filter(codec -> Configurable.class.isAssignableFrom(codec.getClass()))
        .forEach(codec -> ((Configurable<C, ?, ?, S>) codec).configure(avrodite));

      userCodecs.stream()
        .filter(clazz -> !isAbstract(clazz.getModifiers()))
        .filter(clazz -> !isInterface(clazz.getModifiers()))
        .filter(standard.api().typeCodecApi()::isAssignableFrom)
        .forEach(clazz -> register(clazz, codecIndex, avrodite));

      return avrodite.codecIndex(unmodifiableMap(codecIndex));
    }

    @SuppressWarnings("unchecked")
    private void register(Class<?> clazz, HashMap<Type, C> codecIndex, Avrodite<S, C> avrodite) {
      ofNullable(AvroditeBuilder.<S, C>getBeanCodecInstance(clazz))
        .filter(codec -> standard.name().equals(codec.standard().name()) && standard.version().equals(codec.standard().version()))
        .map(codec -> codecIndex.computeIfAbsent(getCodecTarget(codec.getClass()), a -> codec))
        .filter(codec -> Configurable.class.isAssignableFrom(clazz))
        .ifPresent(codec -> ((Configurable<C, ?, ?, S>) codec).configure(avrodite));
    }

    private Type getCodecTarget(Class<?> clazz) {
      return ofNullable(resolve(clazz).type(Codec.class).genericTypes())
        .filter(generics -> !generics.isEmpty())
        .map(generics -> generics.get(0))
        .orElse(Object.class);
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
