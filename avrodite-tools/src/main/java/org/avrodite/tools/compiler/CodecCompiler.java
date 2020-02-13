package org.avrodite.tools.compiler;

import static java.lang.String.join;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.tools.ToolProvider.getSystemJavaCompiler;

import io.github.classgraph.ClassGraph;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor
public class CodecCompiler {

  private final Set<Compilation> results;

  public static Builder begin() {
    return new Builder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final Set<String> classPathItems = new HashSet<>();
    private final Map<String, String> sourceCodes = new HashMap<>();

    public Builder addClassPathItem(String item) {
      classPathItems.add(item);
      return this;
    }

    public Builder addClassSourceCode(String className, String sourceCode) {
      sourceCodes.put(className, sourceCode);
      return this;
    }

    public CodecCompiler compile() {
      JavaCompiler compiler = getSystemJavaCompiler();
      StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, null);
      Utils.ClassFileManager manager = new Utils.ClassFileManager(standardJavaFileManager);
      List<JavaFileObject> files = sourceCodes.entrySet().stream()
        .map(pair -> new Utils.CharSequenceJavaFileObject(pair.getKey(), pair.getValue()))
        .collect(toList());
      List<String> options = new LinkedList<>();
      if (!classPathItems.isEmpty()) {
        new ClassGraph().getClasspathURIs().stream()
              .map(URI::getPath)
              .forEach(classPathItems::add);
        options.add("-classpath");
        options.add(join(":", classPathItems));
      }
      compiler.getTask(null, manager, null, options, null, files).call();
      Set<Compilation> results = manager.files().entrySet().stream()
        .map(entry -> Compilation.of(entry.getKey(), entry.getValue().getBytes()))
        .collect(toSet());
      return new CodecCompiler(unmodifiableSet(results));
    }

  }
}
