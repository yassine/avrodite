package org.avrodite.tools.core.utils;

import static java.lang.String.format;

import com.machinezoo.noexception.Exceptions;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/*
The probability of collision, for 2^R random values of N bits is about 2^(2R-N).
*/
@UtilityClass
public class CryptoUtils {

  private static final int MAX = 8;
  private static final char[] CHARACTERS = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
    'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
    't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    '+', '/'
  };

  private static final ThreadLocal<MessageDigest> DIGEST = ThreadLocal.withInitial(() ->
    Exceptions.sneak().get(() -> MessageDigest.getInstance("SHA-256"))
  );

  public static char[] randomBase64(int multiplier) {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    char[] data = new char[multiplier * MAX];
    long randomLong;
    int c;
    int e;
    for (c = 0; c < multiplier; c++) {
      randomLong = random.nextLong(Long.MAX_VALUE);
      for (e = 0; e < MAX; e++) {
        data[MAX * c + e] = CHARACTERS[0x3F & (int) randomLong];
        randomLong = randomLong >> 6;
      }
    }
    return data;
  }

  @SneakyThrows
  @SuppressWarnings("CheckStyle")
  public static String hashSHA256(String originalString) {
    return bytesToHex(DIGEST.get().digest(originalString.getBytes(StandardCharsets.UTF_8)));
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      hexString.append(format("%02X", 0xff & b));
    }
    return hexString.toString();
  }
}
