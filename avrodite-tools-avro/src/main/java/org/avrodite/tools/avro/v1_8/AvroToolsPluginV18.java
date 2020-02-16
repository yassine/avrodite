package org.avrodite.tools.avro.v1_8;

import com.google.auto.service.AutoService;
import org.avrodite.avro.AvroStandard;
import org.avrodite.avro.v1_8.AvroStandardV18;
import org.avrodite.tools.api.AvroditeToolsPlugin;
import org.avrodite.tools.avro.AvroBeanTemplateContext;
import org.avrodite.tools.avro.AvroToolsPlugin;
import org.avrodite.tools.avro.AvroToolsPluginContext;
import org.avrodite.tools.core.bean.BeanInfo;

@AutoService(AvroditeToolsPlugin.class)
public class AvroToolsPluginV18 extends AvroToolsPlugin {

  private static final String STANDARD_FIELD_NAME = "AVRO_1_8";

  @Override
  public AvroStandard standard() {
    return AvroStandardV18.AVRO_1_8;
  }

  @Override
  protected AvroBeanTemplateContext avroBeanTemplateContext(AvroToolsPluginContext pluginContext, BeanInfo beanInfo) {
    return new AvroBeanTemplateContext(pluginContext.getSchemas().get(beanInfo.getSignature()),
        String.join(".", AvroStandardV18.class.getName(), STANDARD_FIELD_NAME));

  }
}
