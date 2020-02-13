package org.avrodite.tools.error;

import org.jboss.logging.Logger;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

public class AvroditeToolsException extends Exception {

  public static final Api API = Messages.getBundle(Api.class);

  @MessageBundle(projectCode = "AVRO_TOOLS_CODEC")
  public interface Api {
    @LogMessage(level = Logger.Level.ERROR)
    @Message(value = "Argument '%s' can't be null.")
    IllegalArgumentException illegalNullArgument(String name);

    @LogMessage(level = Logger.Level.ERROR)
    @Message(value = "Avrodite tools Plugin for codec format '%s' version '%s' can't be located on classpath")
    IllegalArgumentException pluginNotFound(String format, String version);
  }

}
