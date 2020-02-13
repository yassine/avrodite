package org.avrodite.tools.utils;

import static java.util.Spliterators.spliteratorUnknownSize;
import static org.avrodite.tools.error.AvroditeToolsException.API;

import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import lombok.experimental.UtilityClass;
import org.avrodite.api.CodecStandard;
import org.avrodite.tools.api.AvroditeToolsPlugin;
import org.avrodite.tools.core.utils.ReflectionUtils;

@UtilityClass
public class PluginUtils {

  public static AvroditeToolsPlugin findPluginByStandard(CodecStandard standard) {
    return StreamSupport.stream(spliteratorUnknownSize(ServiceLoader.load(AvroditeToolsPlugin.class).iterator(), Spliterator.ORDERED), false)
      .map(ReflectionUtils::<AvroditeToolsPlugin>castTo)
      .filter(plugin -> plugin.standard().name().equals(standard.name())
                        && plugin.standard().version().equals(standard.version()))
      .findAny()
      .orElseThrow(() -> API.pluginNotFound(standard.name(), standard.version()));
  }

}
