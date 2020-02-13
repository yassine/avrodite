package org.avrodite.tools.avro.v1_9;

import com.google.auto.service.AutoService;
import org.avrodite.avro.AvroStandard;
import org.avrodite.avro.value.v1_9.AvroStandardV19;
import org.avrodite.tools.api.AvroditeToolsPlugin;
import org.avrodite.tools.avro.AvroBeanTemplateContext;
import org.avrodite.tools.avro.AvroToolsPlugin;
import org.avrodite.tools.avro.AvroToolsPluginContext;
import org.avrodite.tools.core.bean.BeanInfo;

@AutoService(AvroditeToolsPlugin.class)
public class AvroToolsPluginV19 extends AvroToolsPlugin {

  private static final String STANDARD_FIELD_NAME = "AVRO_1_9";

  @Override
  public AvroStandard standard() {
    return AvroStandardV19.AVRO_1_9;
  }

  @Override
  protected AvroBeanTemplateContext avroBeanTemplateContext(AvroToolsPluginContext pluginContext, BeanInfo beanInfo) {
    return new AvroBeanTemplateContext(pluginContext.getSchemas().get(beanInfo.getSignature()),
      String.join(".",AvroStandardV19.class.getName(), STANDARD_FIELD_NAME));
  }
}
