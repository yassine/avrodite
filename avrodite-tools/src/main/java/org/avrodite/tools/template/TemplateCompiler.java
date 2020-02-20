package org.avrodite.tools.template;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.avrodite.tools.template.Utils.codecFqName;

import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.avrodite.tools.api.AvroditeToolsPlugin;
import org.avrodite.tools.compiler.CodecCompiler;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.error.AvroditeToolsException;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of", access = AccessLevel.PRIVATE)
public class TemplateCompiler {

  @Getter
  private final CodecCompiler compiler;

  public static Builder builder() {
    return new Builder();
  }

  @Accessors(fluent = true, chain = true) @SuppressWarnings({"rawtypes", "unchecked"})
  public static class Builder {

    private final Set<String> classPathItems = new HashSet<>();
    @Setter
    private AvroditeToolsPlugin avroditeToolsPlugin;
    @Setter
    private BeanManager beanManager;

    public Builder addClassPathItems(String... items) {
      classPathItems.addAll(asList(items));
      return this;
    }

    public TemplateCompiler build() {
      CodecCompiler.Builder compilationBuild = CodecCompiler.begin();
      classPathItems.forEach(compilationBuild::addClassPathItem);

      Object context = avroditeToolsPlugin.createPluginContext(beanManager);
      beanManager.beansIndex().values().stream()
        .map(bean ->
          avroditeToolsPlugin.customizeCodecTemplate(context, CodecSourceTemplate.builder(), bean)
            .path(codecFqName(bean.getTargetRaw(), bean.getSignature(), avroditeToolsPlugin.standard())
                    .replaceAll("\\.","/"))
        )
        .peek(builder -> builder.templateLocation(ofNullable(builder.templateLocation())
          .orElseThrow(() -> AvroditeToolsException.API.illegalNullArgument("templateLocation"))))
        .peek(builder -> builder.path(ofNullable(builder.path())
          .orElseThrow(() -> AvroditeToolsException.API.illegalNullArgument("codec class path"))))
        .map(CodecSourceTemplate.Builder::build)

        .forEach(template -> compilationBuild.addClassSourceCode(template.path(), template.render()));

      return new TemplateCompiler(compilationBuild.compile());
    }

  }
}
