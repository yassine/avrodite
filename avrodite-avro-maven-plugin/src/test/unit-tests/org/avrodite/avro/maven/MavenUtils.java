package org.avrodite.avro.maven;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.groovy.builder.ModelBuilder;

public class MavenUtils {

  @SneakyThrows
  public static MavenProject getProject(File pom){
    GroovyShell shell = new GroovyShell();
    String text = IOUtil.toString(new FileInputStream(pom));
    Script script = shell.parse(new GroovyCodeSource(text, pom.toString(), pom.toString()));
    ModelBuilder builder = new ModelBuilder();
    script.setBinding(builder);
    $ExecuteManager executeManager = new $ExecuteManager();
    Field field = builder.getClass().getDeclaredField("executeManager");
    field.setAccessible(true);
    field.set(builder, executeManager);
    return  new MavenProject((Model) builder.build(script));
  }

  @Getter
  static class $ExecuteManager implements ExecuteManager {

    private Model model;

    @Override
    public void register(Model model, List<ExecuteTask> tasks) {
      this.model = model;
    }

    @Override
    public List<ExecuteTask> getTasks(Model model) {
      return new ArrayList<>();
    }

    @Override
    public void install(Model model, Map<String, ?> options) {

    }
  }

}
