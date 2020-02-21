package org.avrodite.avro.result;

import static com.machinezoo.noexception.Exceptions.sneak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Page;

@Slf4j
public class PlotUtils {

  @SneakyThrows
  public static void plot(TestRepresentation testRepresentation, File outputDir, String geckoDriverPath){
    File downloadDir = Files.createTempDirectory("").toFile();
    FirefoxDriver driver = getDriver(downloadDir, geckoDriverPath);
    File throughputFile = plotHtmlFigure(testRepresentation, testRepresentation.getThroughputMetric().getFigure());
    generate(downloadDir,
      throughputFile,
      new File(outputDir, testRepresentation.getThroughputMetric().imageName()),
      driver
    );
    File mallocFile = plotHtmlFigure(testRepresentation, testRepresentation.getHeapAllocationRate().getFigure());
    generate(downloadDir,
      mallocFile,
      new File(outputDir, testRepresentation.getHeapAllocationRate().imageName()),
      driver
    );
    FileUtils.deleteDirectory(downloadDir);
  }

  @SneakyThrows
  private static void generate(File downloadDir, File htmlFile, File targetFile, FirefoxDriver firefoxDriver){
    File downloadedFile = new File(downloadDir, "newplot.png");
    firefoxDriver.get("file://"+htmlFile.getAbsolutePath());
    WebElement webElement = firefoxDriver.findElementByCssSelector(".modebar > div:nth-child(1) > a");
    webElement.click();
    WebDriverWait wait = new WebDriverWait(firefoxDriver, 1);
    wait.until(driver -> sneak().get(downloadedFile::exists));
    IOUtils.copy(new FileInputStream(downloadedFile), new FileOutputStream(targetFile));
    log.info("remove download file '{}': '{}'", downloadedFile.getAbsolutePath(), downloadedFile.delete());
    log.info("remove html file '{}' : '{}'", downloadedFile.getAbsolutePath(), htmlFile.delete());
  }

  @SneakyThrows
  private static File plotHtmlFigure(TestRepresentation testRepresentation, Figure figure){
    File file = Files.createTempFile(testRepresentation.getId()+"."+testRepresentation.getThroughputMetric().getId(), ".html").toFile();
    Page page = Page.pageBuilder(figure, "target").build();
    String output = page.asJavascript();
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      writer.write(output);
    }
    log.info("Content written to {}", file.getAbsolutePath());
    return file;
  }

  private static FirefoxDriver getDriver(File downloadDir, String driverPath) {
    System.setProperty("webdriver.gecko.driver", driverPath);
    FirefoxProfile fxProfile = new FirefoxProfile();
    fxProfile.setPreference("browser.download.folderList", 2);
    fxProfile.setPreference("browser.download.manager.showWhenStarting", false);
    fxProfile.setPreference("browser.download.dir", downloadDir.getAbsolutePath());
    fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip,application/octet-stream,image/png,image/jpeg,application/vnd.ms-outlook,text/html,application/pdf");
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.addArguments("--headless");
    firefoxOptions.setProfile(fxProfile);
    return new FirefoxDriver(firefoxOptions);
  }
}
