package org.avrodite.tools.avro;

import static org.avrodite.tools.avro.SchemaUtils.createSchemaRegistry;

import java.util.Map;
import org.apache.avro.Schema;
import org.avrodite.avro.AvroStandard;
import org.avrodite.avro.AvroValueCodec;
import org.avrodite.tools.api.AvroditeToolsPlugin;
import org.avrodite.tools.core.bean.BeanInfo;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.template.BeanTemplateContext;
import org.avrodite.tools.template.CodecSourceTemplate;

public abstract class AvroToolsPlugin implements AvroditeToolsPlugin<AvroValueCodec<?>, AvroStandard, AvroToolsPluginContext> {

  private static final String CODEC_TEMPLATE_LOCATION = "org/avrodite/tools/avro/template/avrodite.peb";

  @Override
  public AvroToolsPluginContext createPluginContext(BeanManager beanManager) {
    Map<String, Schema> schema = createSchemaRegistry(beanManager);
    return new AvroToolsPluginContext(beanManager, schema);
  }

  @Override
  public CodecSourceTemplate.Builder customizeCodecTemplate(AvroToolsPluginContext pluginContext, CodecSourceTemplate.Builder codecTemplateBuilder, BeanInfo beanInfo) {
    codecTemplateBuilder.addContext("context", new BeanTemplateContext(pluginContext.getBeanManager(), beanInfo, standard()));
    codecTemplateBuilder.addContext("avroContext", avroBeanTemplateContext(pluginContext, beanInfo));
    codecTemplateBuilder.templateLocation(templateLocation());
    return codecTemplateBuilder;
  }

  protected String templateLocation(){
    return CODEC_TEMPLATE_LOCATION;
  }

  protected abstract AvroBeanTemplateContext avroBeanTemplateContext(AvroToolsPluginContext pluginContext, BeanInfo beanInfo);

}
