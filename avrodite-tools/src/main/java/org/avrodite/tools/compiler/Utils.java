package org.avrodite.tools.compiler;

import static java.lang.Thread.currentThread;
import static java.util.Collections.unmodifiableMap;
import static org.avrodite.tools.core.utils.ReflectionUtils.getDeclaredMethods;

import com.machinezoo.noexception.Exceptions;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import lombok.experimental.UtilityClass;

@UtilityClass
class Utils {

  public static final String DEFINE_CLASS_METHOD_NAME = "defineClass";

  static Class<?> defineClass(ClassLoader classLoader, Compilation compilation) {
    return getDeclaredMethods(classLoader.getClass())
      .stream()
      .filter(method -> method.getName().equals(DEFINE_CLASS_METHOD_NAME) && method.getParameters().length == 4)
      .findAny()
      .map(method -> {
        method.setAccessible(true);
        return Exceptions.sneak().get(() -> (Class<?>) method.invoke(currentThread().getContextClassLoader(),
          compilation.name(), compilation.byteCode(), 0, compilation.byteCode().length));
      })
      .orElse(null);
  }

  static final class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final Map<String, CustomJavaFileObject> files = new HashMap<>();

    ClassFileManager(StandardJavaFileManager m) {
      super(m);
    }

    @Override
    public CustomJavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
                                                     FileObject sibling) {
      files.put(className, new CustomJavaFileObject(className, kind));
      return files.get(className);
    }

    public Map<String, CustomJavaFileObject> files() {
      return unmodifiableMap(files);
    }
  }

  static final class CustomJavaFileObject extends SimpleJavaFileObject {
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    private CustomJavaFileObject(String name, Kind kind) {
      super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
    }

    byte[] getBytes() {
      return os.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
      return os;
    }
  }

  static final class CharSequenceJavaFileObject extends SimpleJavaFileObject {
    private final CharSequence content;

    CharSequenceJavaFileObject(String className, CharSequence content) {
      super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
      this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return content;
    }
  }
}
