package org.avrodite.tools.compiler;

import static org.avrodite.tools.compiler.Utils.defineClass;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor(staticName = "of")
@Accessors(fluent = true)
@Slf4j
public class Compilation {

  private final String name;
  private final byte[] byteCode;
  private Class<?> compiledClass;

  public synchronized Class<?> defineTo(ClassLoader classLoader) {
    if (compiledClass == null) {
      try {
        compiledClass = classLoader.loadClass(name);
      } catch (Exception e) {
        log.debug(e.getMessage());
        compiledClass = defineClass(classLoader, this);
      }
    }
    return compiledClass;
  }

  public Class<?> define() {
    return defineTo(Thread.currentThread().getContextClassLoader());
  }
}
