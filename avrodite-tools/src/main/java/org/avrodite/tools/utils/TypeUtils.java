package org.avrodite.tools.utils;

import static org.avrodite.tools.core.utils.TypeUtils.typeToString;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.avrodite.tools.template.TypeFingerprint;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

@UtilityClass
public class TypeUtils {

  public static void typeTrace(Type t, List<TypeFingerprint> fingerprints, List<String> signatures){
    if (t instanceof GenericArrayType) {
      fingerprints.add(TypeFingerprint.of(t));
      Type arrayComponentType = ((GenericArrayType) t).getGenericComponentType();
      signatures.add(typeToString(arrayComponentType));
      typeTrace(arrayComponentType, fingerprints, signatures);
      return;
    } else if (t instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) t;
      Class<?> rawType = (Class<?>) parameterizedType.getRawType();
      fingerprints.add(TypeFingerprint.of(t));
      if (Iterable.class.isAssignableFrom(rawType)) {
        GenericsContext genericsContext = resolve(rawType).inlyingType(parameterizedType).type(Iterable.class);
        signatures.add(typeToString(genericsContext.genericTypes().get(0)));
        typeTrace(genericsContext.genericTypes().get(0), fingerprints, signatures);
      } else if (Map.class.equals(rawType)) {
        GenericsContext genericsContext = resolve(rawType).inlyingType(parameterizedType).type(Map.class);
        signatures.add(typeToString(genericsContext.genericTypes().get(1)));
        typeTrace(genericsContext.genericTypes().get(1), fingerprints, signatures);
      }
      return;
    } else if (t instanceof Class && ((Class<?>) t).isArray()) {
      fingerprints.add(TypeFingerprint.of(t));
      signatures.add(typeToString(((Class<?>) t).getComponentType()));
      typeTrace(((Class<?>) t).getComponentType(), fingerprints, signatures);
      return;
    }
    fingerprints.add(TypeFingerprint.of(t));
  }

}
