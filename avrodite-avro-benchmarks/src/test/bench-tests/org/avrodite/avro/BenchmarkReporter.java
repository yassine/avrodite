package org.avrodite.avro;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import lombok.SneakyThrows;
import org.avrodite.avro.result.BenchResultModel;
import org.avrodite.avro.result.PlotUtils;
import org.avrodite.avro.result.TemplateContext;
import org.avrodite.avro.result.TestRepresentation;
import org.avrodite.fixtures.event.EquityMarketPriceEvent;

/**
 * helps generate bench results docs from benchmarks output
 * --yassine
 */
public class BenchmarkReporter {

  static final String OUTPUT_FILE = "avrodite-pages/data/output.json";

  @SneakyThrows
  public static void main(String[] args) {
    String oldClasspathProperty = System.getProperty("java.class.path");
    try {
      String classPath = String.join(":",
        new ClassGraph().getClasspathURIs().stream()
          .map(URI::getPath)
          .filter(path -> !path.contains("maven-slf4j-provider") && !path.contains("slf4j-jdk14"))
          .toArray(String[]::new)
      );
      System.setProperty("java.class.path", classPath);
      new EquityEventBenchmark().run(OUTPUT_FILE, "-cp", classPath);
      report(OUTPUT_FILE, "avrodite-pages");
    } finally {
      System.setProperty("java.class.path", oldClasspathProperty);
    }
  }

  @SneakyThrows
  public static void report(String input, String outputDirPath) {
    BenchResultModel[] modelsArr = loadDoc(input);
    File outputDir = new File(outputDirPath);
    File imagesOutputDir = new File(outputDir, "images");
    TestRepresentation t1 = new TestRepresentation("T1", "All fields are nullable", modelsArr, model -> model.getParams().getNullable().equals("true"));
    PlotUtils.plot(t1, imagesOutputDir, "/code/webdriver/geckodriver");
    TestRepresentation t2 = new TestRepresentation("T2", "All fields are NON nullable", modelsArr, model -> model.getParams().getNullable().equals("false"));
    PlotUtils.plot(t2, imagesOutputDir, "/code/webdriver/geckodriver");
    HashMap<String, Object> contextMap = new HashMap<>();
    contextMap.put("context", new TemplateContext(Arrays.asList(t1, t2), modelsArr[0]));
    contextMap.put("t1Schema", EquityMarketPriceEvent.SERIALIZED_MODEL_SCHEMA);
    contextMap.put("t2Schema", EquityMarketPriceEvent.NON_NULLABLE_FIELDS_SERIALIZED_MODEL_SCHEMA);
    contextMap.put("fixtureJson", fixtureModel());
    PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    PebbleTemplate codecTemplate = engine.getTemplate("org/avrodite/avro/result/template.peb");
    Writer codecSourceWriter = new FileWriter(new File(outputDir, "Benchmarks.md"));
    codecTemplate.evaluate(codecSourceWriter, contextMap);
  }

  @SneakyThrows
  private static BenchResultModel[] loadDoc(String path) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(new FileInputStream(path), BenchResultModel[].class);
  }

  @SneakyThrows
  private static String fixtureModel() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(EquityMarketPriceEvent.create());
  }

  @SneakyThrows
  private static URL toURL(String path) {
    return new File(path).toURI().toURL();
  }

}
