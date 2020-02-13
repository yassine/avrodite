package org.avrodite.tools.api;

import org.avrodite.api.CodecStandard;
import org.avrodite.api.ValueCodec;
import org.avrodite.tools.core.bean.BeanInfo;
import org.avrodite.tools.core.bean.BeanManager;
import org.avrodite.tools.template.CodecSourceTemplate;

public interface AvroditeToolsPlugin<V extends ValueCodec<?, ?, ?, S>, S extends CodecStandard<?, ?, ?, V>, C> {

  C createPluginContext(BeanManager beanManager);

  CodecSourceTemplate.Builder customizeCodecTemplate(C pluginContext, CodecSourceTemplate.Builder codecTemplateBuilder, BeanInfo beanInfo);

  S standard();
}
