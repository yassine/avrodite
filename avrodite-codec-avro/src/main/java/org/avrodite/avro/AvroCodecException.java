package org.avrodite.avro;

import org.avrodite.api.error.CodecException;
import org.jboss.logging.Logger;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.MessageBundle;

public class AvroCodecException extends CodecException {

  public static final Api API = Messages.getBundle(Api.class);

  public AvroCodecException(String message) {
    super(message);
  }

  public AvroCodecException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  @MessageBundle(projectCode = "AVRO_CODEC")
  public interface Api {
    @LogMessage(level = Logger.Level.ERROR)
    @org.jboss.logging.annotations.Message(value = "Unexpected union index value '%s' at field '%s' of type '%s'.")
    AvroCodecException unexpectedUnionIndexValue(int value, String field, String type);

    @LogMessage(level = Logger.Level.ERROR)
    @org.jboss.logging.annotations.Message(value = "Illegal end of array, expected 0 but was '%s'.")
    AvroCodecException illegalArrayEnd(int value);

    @LogMessage(level = Logger.Level.ERROR)
    @org.jboss.logging.annotations.Message(value = "Illegal end of map, expected 0 but was '%s'.")
    AvroCodecException illegalMapEnd(long value);

    @org.jboss.logging.annotations.Message(value = "End of input.")
    AvroCodecException endOfInput();

    @org.jboss.logging.annotations.Message(value = "Unexpected input, expected '%s' but was '%s'.")
    AvroCodecException unexpectedInput(int expected, int actual);

    @org.jboss.logging.annotations.Message(value = "Unexpected number serialization.")
    AvroCodecException unexpectedNumber();

    @org.jboss.logging.annotations.Message(value = "Unexpected error during encoding/decoding.")
    AvroCodecException error(@Cause Exception e);
  }
}
