package org.avrodite.tools.avro;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.avrodite.tools.core.bean.BeanManager;

@RequiredArgsConstructor
@Getter
public class AvroToolsPluginContext {
  private final BeanManager beanManager;
  private final Map<String, Schema> schemas;
}
