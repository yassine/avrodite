package org.avrodite.avro.maven;

import java.util.Arrays;
import lombok.SneakyThrows;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.mockito.Mockito;

public class CompileCodecsMojoTest extends AbstractMojoTestCase {

  @SneakyThrows
  public void test() {
    MavenProject project = Mockito.mock(MavenProject.class);
    Mockito.when(project.getCompileClasspathElements())
                  .thenReturn(Arrays.asList("hello", "world"));
    System.out.println(project.getCompileClasspathElements());
    System.out.println(System.getProperty("MAVEN_TARGET_DIR"));

    /*
    String location = "src/test/resources/pom.groovy";
    MavenProject project = getProject(getTestFile(location));
    DefaultDependencyResolutionRequest resolutionRequest = new DefaultDependencyResolutionRequest();
    resolutionRequest.setMavenProject(project);
    DefaultSettingsBuildingRequest req = new DefaultSettingsBuildingRequest();
    //LocalRepository localRepository = new LocalRepository(String.join(System.getProperty("user.home")));
    String path = String.join(File.separator, System.getProperty("user.home"), ".m2");

    System.out.println(lookup(RepositorySystemSession.class));

    LocalRepository localRepository = new LocalRepository(path);
    System.out.println(lookup(SettingsBuilder.class).build(req).getEffectiveSettings());
    System.out.println(System.getProperty("user.home"));

    //System.out.println(newMavenSession(project).getRepositorySession().getLocalRepositoryManager());
    //System.out.println(lookupConfiguredMojo(project, "compile-codecs"));
*/
  }


}
