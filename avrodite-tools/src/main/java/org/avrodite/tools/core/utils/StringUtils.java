package org.avrodite.tools.core.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
  private final String NULL = "null";

  public static String join(String separator, Object[] array) {
    StringBuilder sb = new StringBuilder();
    for (int c = 0; c < array.length; c++) {
      sb.append(array[c] == null ? NULL : array[c].toString());
      if (c < array.length - 1) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }
}
