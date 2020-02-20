package org.avrodite.tools.core.utils;

import static java.util.Arrays.stream;
import static ru.vyarus.java.generics.resolver.GenericsResolver.resolve;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.avrodite.tools.core.bean.TypeInfo;
import org.avrodite.tools.template.TypeFingerprint;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

@UtilityClass
public class TypeUtils {

  public static final TypeInfo OBJECT_TYPE = new TypeInfo(Object.class, Object.class, Object.class.getName());

  public static String typeToString(Type t) {
    if (t instanceof ParameterizedType) {
      return ((ParameterizedType) t).getRawType().getTypeName()
        + "<"
        + StringUtils.join(",", stream(((ParameterizedType) t).getActualTypeArguments())
        .map(TypeUtils::typeToString).map(name -> (CharSequence) name)
        .toArray(CharSequence[]::new))
        + ">";
    } else {
      if (t instanceof GenericArrayType) {
        return typeToString(((GenericArrayType) t).getGenericComponentType()) + "[]";
      }
      if (((Class<?>) t).isArray()) {
        return typeToString(((Class<?>) t).getComponentType()) + "[]";
      }
      if (((Class<?>) t).getEnclosingClass() != null) {//Inner class
        return typeToString(((Class<?>) t).getEnclosingClass()) + "." + ((Class<?>) t).getSimpleName();
      }
      return ((Class<?>) t).getName();
    }
  }

  public static Class<?> typeToRawClass(Type t) {
    if (t instanceof ParameterizedType) {
      return typeToRawClass(((ParameterizedType) t).getRawType());
    } else if (t instanceof GenericArrayType) {
      return typeToRawClass(((GenericArrayType) t).getGenericComponentType());
    } else {
      return (Class<?>) t;
    }
  }

  @SuppressWarnings("ConstantConditions")
  public static TypeInfo typeTarget(Type t) {
    if (t instanceof GenericArrayType) {
      Type arrayComponentType = ((GenericArrayType) t).getGenericComponentType();
      return typeTarget(arrayComponentType);
    } else if (t instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) t;
      Class<?> rawType = (Class<?>) parameterizedType.getRawType();
      if (Iterable.class.isAssignableFrom(rawType)) {
        GenericsContext genericsContext = resolve(rawType).inlyingType(parameterizedType).type(Iterable.class);
        if (genericsContext.generics().isEmpty()) {
          return OBJECT_TYPE;
        }
        return typeTarget(genericsContext.genericTypes().get(0));
      } else if (Map.class.equals(rawType)) {
        GenericsContext genericsContext = resolve(rawType).inlyingType(parameterizedType).type(Map.class);
        if (genericsContext.generics().isEmpty()) {
          return OBJECT_TYPE;
        }
        return typeTarget(genericsContext.genericTypes().get(1));
      }
      return new TypeInfo(t, rawType, TypeUtils.typeToString(t));
    } else if (t instanceof Class && ((Class<?>) t).isArray()) {
      return typeTarget(((Class<?>) t).getComponentType());
    }
    return new TypeInfo(t, (Class<?>) t, TypeUtils.typeToString(t));
  }

  public static void typeTrace(Type t, List<TypeFingerprint> fingerprints, List<String> signatures) {
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
