package org.avrodite.tools;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.avrodite.api.CodecStandard;
import org.avrodite.api.ValueCodec;
import org.avrodite.tools.compiler.Compilation;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.core.bean.NullableFieldPredicate;
import org.avrodite.tools.error.AvroditeToolsException;
import org.avrodite.tools.template.TemplateCompiler;
import org.avrodite.tools.utils.PluginUtils;

@UtilityClass
public class AvroditeTools {

  public static <S extends CodecStandard<?, ?, ?, V>, V extends ValueCodec<?, ?, ?, S>> CompilerBuilder<S, V> compiler(S codecStandard) {
    return new CompilerBuilder<>(requireNonNull(codecStandard));
  }

  @Accessors(fluent = true, chain = true)
  @RequiredArgsConstructor
  public static class CompilerBuilder<S extends CodecStandard<?, ?, ?, V>, V extends ValueCodec<?, ?, ?, S>> {

    private final Set<Class<?>> excludeClasses = new HashSet<>();
    private final Set<ClassLoader> classLoaders = new HashSet<>();
    private final Set<String> discoveryPackages = new HashSet<>();
    private final Set<String> classPathItems = new HashSet<>();
    private final S standard;
    @Setter
    private NullableFieldPredicate nullableFieldPredicate;

    public Set<Compilation> compile() {
      return Optional.ofNullable(standard)
        .map(s -> TemplateCompiler.<V>builder()
          .beanManager(
            BeanManager.builder(s)
              .addClassLoaders(classLoaders)
              .excludeClasses(excludeClasses.toArray(new Class[0]))
              .includePackages(discoveryPackages.toArray(new String[0]))
              .nullableFieldPredicate(nullableFieldPredicate)
              .build())
          .avroditeToolsPlugin(PluginUtils.findPluginByStandard(s))
          .addClassPathItems(classPathItems.toArray(new String[0]))
          .build()
          .compiler()
          .results()
        ).orElseThrow(() -> AvroditeToolsException.API.illegalNullArgument("codec standard"));
    }

    public CompilerBuilder<S, V> discover(String... pkgs) {
      discoveryPackages.addAll(asList(pkgs));
      return this;
    }

    public CompilerBuilder<S, V> addClassLoader(ClassLoader classLoader) {
      classLoaders.add(classLoader);
      return this;
    }

    public CompilerBuilder<S, V> exclude(Class<?>... classes) {
      excludeClasses.addAll(asList(classes));
      return this;
    }

    public CompilerBuilder<S, V> addClassPathItems(String... items) {
      classPathItems.addAll(asList(items));
      return this;
    }

  }
}
