package org.avrodite.avro.maven;

import static com.google.common.io.Files.write;
import static java.lang.String.format;

import com.machinezoo.noexception.Exceptions;
import java.io.BufferedWriter;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.avrodite.avro.v1_8.AvroStandardV18;
import org.avrodite.tools.AvroditeTools;
import org.avrodite.tools.compiler.Compilation;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
@Mojo(name = "compile-codecs", requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Slf4j
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CompileCodecs extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(alias = "context", required = true)
  private List<String> contextPackages;

  @Parameter(alias = "additionalClasspathItems")
  private List<String> additionalClasspathItems = new ArrayList<>();

  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File output;

  @Override
  @SneakyThrows
  public void execute() {
    Set<String> classpathItems = getClasspathItems();
    URL[] urls = classpathItems.stream().map(this::toURL).toArray(URL[]::new);
    try (URLClassLoader urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
      Thread.currentThread().setContextClassLoader(urlClassLoader);
      Set<Compilation> compilations = AvroditeTools.compiler(AvroStandardV18.AVRO_1_8)
        .discover(contextPackages.toArray(new String[0]))
        .addClassLoader(urlClassLoader)
        .addClassPathItems(classpathItems.toArray(new String[0]))
        .compile();
      compilations.forEach(this::writeToFile);
      File spiDir = new File(output, "META-INF/services");
      log.info("creating spi services dir: {}", spiDir.mkdirs());
      File spiFile = new File(spiDir, AvroStandardV18.AVRO_1_8.api().typeCodecApi().getName());
      Set<String> services = new HashSet<>();
      if (spiFile.exists()) {
        services.addAll(Files.readAllLines(spiFile.toPath()));
      }
      compilations.forEach(compilation -> services.add(compilation.name()));
      try (BufferedWriter writer = Files.newBufferedWriter(spiFile.toPath())) {
        services.forEach(service -> {
          Exceptions.sneak().run(() -> {
            writer.write(service);
            writer.newLine();
          });
        });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @SneakyThrows
  @SuppressWarnings("UnstableApiUsage")
  private void writeToFile(Compilation compilation) {
    String fqn = compilation.name();
    File helper = new File(output, fqn.replace('.', File.separatorChar));
    File parent = helper.getParentFile();
    if (!parent.exists() && !parent.mkdirs()) {
      log.error("error while creating path {}", parent.getAbsolutePath());
      return;
    }
    write(compilation.byteCode(), new File(parent, format("%s.class", helper.getName())));
  }

  @SneakyThrows
  private Set<String> getClasspathItems() {
    Set<String> paths;
    if (project == null) {
      paths = new HashSet<>();
    } else {
      paths = new HashSet<>(project.getCompileClasspathElements());
    }
    additionalClasspathItems.stream()
      .filter(Objects::nonNull)
      .flatMap(s -> Arrays.stream(s.split(File.separator)))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .forEach(paths::add);
    return paths;
  }

  @SneakyThrows
  private URL toURL(String path) {
    return new File(path).toURI().toURL();
  }

}
