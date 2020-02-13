package org.avrodite.tools.template;

import static java.util.Collections.unmodifiableMap;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Accessors(fluent = true) @Slf4j @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CodecSourceTemplate {

  private final Map<String, Object> contextMap;
  private final String templateLocation;
  @Getter
  private final String path;

  /**
   * Renders the source code of the associated context.
   *
   * @return Source code of associated template
   */
  @SneakyThrows
  public String render() {
    PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    PebbleTemplate codecTemplate = engine.getTemplate(templateLocation);
    Writer codecSourceWriter = new StringWriter();
    codecTemplate.evaluate(codecSourceWriter, contextMap);
    log.debug(codecSourceWriter.toString());
    return codecSourceWriter.toString();
  }

  public static Builder builder(){
    return new Builder();
  }

  @Setter @Accessors(fluent = true, chain = true) @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {

    public final Map<String, Object> contextMap = new HashMap<>();

    @Setter @Getter
    public String templateLocation;
    @Setter @Getter
    public String path;

    public Builder addContext(String key, Object context){
      contextMap.put(key, context);
      return this;
    }

    public CodecSourceTemplate build(){
      return new CodecSourceTemplate(unmodifiableMap(contextMap), templateLocation, path);
    }

  }

}
