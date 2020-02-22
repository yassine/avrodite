project(artifactId: 'avrodite-avro-maven-plugin', version: '0.1.0-SNAPSHOT') {
  modelVersion '4.0.0'
  packaging 'maven-plugin'
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy'){}
  properties {
    'sonar.skip' 'true'
  }
  dependencies {
    dependency('org.avrodite:avrodite-tools-avro:0.1.0-SNAPSHOT')
    dependency('com.google.guava:guava:28.2-jre')
    dependency('org.apache.maven:maven-core:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven:maven-plugin-api:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0:provided')
    dependency('ch.qos.logback:logback-classic')
    /* utilities */
    dependency('org.projectlombok:lombok')
    dependency('com.machinezoo.noexception:noexception')
    /* testing */
    dependency('org.apache.maven.plugin-testing:maven-plugin-testing-harness:3.3.0:test')
    dependency('org.apache.maven:maven-compat:3.6.3:test')
    dependency('io.takari.polyglot:polyglot-groovy:0.4.4:test')
    dependency('org.mockito:mockito-core:3.2.4:test')
    dependency('junit:junit:4.13:test')
  }

  profiles {
    profile(id: 'test.functional'){
      build {
        plugins {
          plugin(groupId: 'org.apache.maven.plugins', artifactId: 'maven-invoker-plugin', version: '3.2.1'){
            configuration{
              debug true
              cloneProjectsTo '${project.build.directory}/fixtures'
              localRepositoryPath '${project.build.directory}/local-repo'
              pomIncludes '*/pom.xml'
              projectsDirectory 'src/test/fixtures'
              settingsFile 'src/test/resources/settings.xml'
            }
            executions {
              execution(id: 'integration-tests', goals: ['install', 'run'])
            }
          }
        }
      }
    }
  }

  build {
    plugins {
      plugin(groupId: 'org.apache.maven.plugins', artifactId : 'maven-plugin-plugin', version: '3.6.0')
    }
  }

}
