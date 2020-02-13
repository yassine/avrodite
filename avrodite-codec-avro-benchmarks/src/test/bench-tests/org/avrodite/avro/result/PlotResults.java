package org.avrodite.avro.result;

import static com.machinezoo.noexception.Exceptions.sneak;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Files;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.BarTrace;

@Builder
@Slf4j
public class PlotResults {

  private List<String> inputFiles;
  private String driverPath;

  public void plot() {
    File tempoDir = Files.newTemporaryFolder();
    FirefoxDriver firefoxDriver = getDriver(tempoDir, driverPath);
    inputFiles
      .forEach(s -> generatePNGImages(s, firefoxDriver, tempoDir));
    firefoxDriver.quit();
  }

  @SneakyThrows
  public static InputStream getPngPlot(FirefoxDriver firefoxDriver, File tempoDir, String page) {
    firefoxDriver.get("file://" + page);
    log.info("{}", new File(tempoDir, "newplot.png").delete());
    WebElement webElement = firefoxDriver.findElementByCssSelector(".modebar > div:nth-child(1) > a");
    webElement.click();
    WebDriverWait wait = new WebDriverWait(firefoxDriver, 3);
    wait.until((driver) -> sneak().get(() -> new File(tempoDir, "newplot.png").exists()));
    return new FileInputStream(new File(tempoDir, "newplot.png"));
  }

  @SneakyThrows
  public static void main(String[] args) {
    String driverPath = "/code/webdriver/geckodriver";
    PlotResults.builder()
      .inputFiles(Arrays.asList("/tmp/bench-results-nullable-fields.json", "/tmp/bench-results.json"))
      .driverPath(driverPath)
      .build()
      .plot();
  }

  private static FirefoxDriver getDriver(File tempoDir, String driverPath) {
    System.setProperty("webdriver.gecko.driver", driverPath);
    FirefoxProfile fxProfile = new FirefoxProfile();
    fxProfile.setPreference("browser.download.folderList", 2);
    fxProfile.setPreference("browser.download.manager.showWhenStarting", false);
    fxProfile.setPreference("browser.download.dir", tempoDir.getAbsolutePath());
    fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip,application/octet-stream,image/png,image/jpeg,application/vnd.ms-outlook,text/html,application/pdf");
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.addArguments("--headless");
    firefoxOptions.setProfile(fxProfile);
    return new FirefoxDriver(firefoxOptions);
  }

  @SneakyThrows
  public static void generatePNGImages(String input, FirefoxDriver firefoxDriver, File tempoDir) {
    File file = new File(input);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ResultModel[] models = mapper.readValue(new FileInputStream(file), ResultModel[].class);

    List<MetricModel> metricModels = singletonList(
      new MetricModel()
        .name("Throughput")
        .unit("ops/ms")
        .extractor((model) -> model.getPrimaryMetric().getScore())
    );

    metricModels.stream()
      .map(metric -> {
        StringColumn subject = StringColumn.create("subject");
        DoubleColumn metricColumn = DoubleColumn.create(metric.name);
        Arrays.stream(models)
          .forEach(model -> {
            String[] nameParts = model.getBenchmark().split("\\.");
            subject.append(nameParts[nameParts.length - 1]);
            metricColumn.append(metric.extractor.apply(model));
          });
        Table table = Table.create(subject, metricColumn);
        if (metric.name.equalsIgnoreCase("throughput")) {
          table = table.sortOn(metric.name);
        } else {
          table = table.sortDescendingOn(metric.name);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(metric.name);
        sb.append(" [");
        sb.append(metric.unit);
        sb.append("] ");
        sb.append((metric.name.equalsIgnoreCase("throughput") ? "- Higher is better -" : "- Lower is better -"));
        String title = sb.toString();
        return new Pair<>(metric, create(title, table, "subject", metric.name));
      }).forEach(plot -> sneak().run(() -> {
      String filePath = format("/tmp/%s.html", file.getName());
      Page page = Page.pageBuilder(plot.value, "target").build();
      String output = page.asJavascript();
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)), StandardCharsets.UTF_8)) {
        writer.write(output);
      }
      InputStream is = getPngPlot(firefoxDriver, tempoDir, filePath);
      FileOutputStream pngFile = new FileOutputStream(new File(format("/media/yassine/work/oss/avrodite/avrodite-pages/images/%s-%s.png", file.getName(), plot.key.name).toLowerCase()));
      IOUtils.copy(is, pngFile);
      new File(filePath).delete();
    }));
  }

  protected static Figure create(String title, Table table, String groupColName, String numberColName) {
    Layout layout = standardLayout(title).build();
    BarTrace trace =
      BarTrace.builder(table.categoricalColumn(groupColName), table.numberColumn(numberColName))
        .orientation(BarTrace.Orientation.HORIZONTAL)
        .build();
    return new Figure(layout, trace);
  }

  private static Layout.LayoutBuilder standardLayout(String title) {
    return Layout.builder().title(title).height(500).width(1000).margin(Margin.builder().left(160).build());
  }

  @AllArgsConstructor
  static class Pair<K, V> {
    K key;
    V value;
  }

  @Getter
  @Setter
  @Accessors(chain = true, fluent = true)
  static class MetricModel {
    private String name;
    private Function<ResultModel, Double> extractor;
    private String unit;
  }


}
