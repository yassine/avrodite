package org.avrodite.tools.avro;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;

@RequiredArgsConstructor
@Getter
public class AvroBeanTemplateContext {

  private final Schema schema;
  private final String standard;

  public String schemaString() {
    String s = Arrays.toString(schema.toString().getBytes());
    return s.substring(1, s.length() - 1);
  }

}
