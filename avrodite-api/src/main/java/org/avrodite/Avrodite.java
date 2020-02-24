package org.avrodite;

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
  public <B, U extends Codec<B, ?, ?, S>> U getCodec(Class<B> beanClass) {
    return (U) codecIndex.get(beanClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <B, U extends Codec<B, ?, ?, S>> U getCodec(Type beanType) {
    return (U) codecIndex.get(beanType);
  }

  public static <S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> AvroditeBuilder<S, C> builder(S standard) {
    return new AvroditeBuilder<>(requireNonNull(standard));
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Slf4j
  public static class AvroditeBuilder<S extends CodecStandard<?, ?, C, ?>, C extends Codec<?, ?, ?, S>> {

    private final S standard;

    private final Set<C> userCodecs = new HashSet<>();

    public AvroditeBuilder<S, C> include(List<C> codecs) {
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
      userCodecs.forEach(codec -> {
        if(codec instanceof Configurable){
          ((Configurable)codec).configure(avrodite);
        }
        Type t = getCodecTarget(codec.getClass());
        codecIndex.put(t, codec);
      });
      return avrodite.codecIndex(unmodifiableMap(codecIndex));
    }

    private Type getCodecTarget(Class<?> clazz) {
      return ofNullable(resolve(clazz).type(Codec.class).genericTypes())
        .map(generics -> generics.get(0))
        .orElse(Object.class);
    }

  }

}
