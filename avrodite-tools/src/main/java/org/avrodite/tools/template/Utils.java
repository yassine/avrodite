package org.avrodite.tools.template;

import static java.lang.String.format;
import static java.lang.String.join;
import static org.avrodite.tools.core.utils.CryptoUtils.hashSHA256;

import lombok.experimental.UtilityClass;
import org.avrodite.api.CodecStandard;

@UtilityClass
class Utils {

  private static final String CODEC_NAME_FORMAT = "%s_%s_%s_Codec_%s";
  private static final String STRIP_PATTERN = "[^A-Za-z0-9_]";
  private static final String SEPARATOR = "_";

  static String codecName(Class<?> target, String signature, CodecStandard codecStandard) {
    return format(CODEC_NAME_FORMAT,
            target.getSimpleName(), codecStandard.name(),
            codecStandard.version().replaceAll(STRIP_PATTERN, SEPARATOR), hashSHA256(signature).substring(0, 8));
  }

  static String codecFqName(Class<?> target, String signature, CodecStandard codecStandard) {
    return join(".", target.getPackage().getName(), codecName(target, signature, codecStandard));
  }

}
