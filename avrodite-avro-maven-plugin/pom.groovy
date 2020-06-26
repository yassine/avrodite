project {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite', version: '0.2.0-SNAPSHOT', relativePath: '../pom.groovy')
  artifactId 'avrodite-avro-maven-plugin'
  modelVersion '4.0.0'
  packaging 'maven-plugin'
  properties {
    'sonar.skip' 'true'
  }
  dependencies {
    dependency('org.avrodite:avrodite-tools-avro')
    dependency('com.google.guava:guava:28.2-jre')
    dependency('org.apache.maven:maven-core:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven:maven-plugin-api:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0:provided')
    dependency('junit:junit:4.13:test')
  }

  profiles {
    profile(id: 'test.functional'){
      activation {
        property(name: 'test.profile', value: 'all')
        property(name: 'test.profile', value: 'ci')
      }
      build {
        plugins {
          plugin(groupId: 'org.apache.maven.plugins', artifactId: 'maven-invoker-plugin', version: '3.2.1'){
            configuration {
              debug true
              cloneProjectsTo '${project.build.directory}/fixtures'
              localRepositoryPath '${project.build.directory}/local-repo'
              pomIncludes '*/pom.xml'
              projectsDirectory 'src/test/maven-plugin-fixtures'
              settingsFile 'src/test/resources/settings.xml'
            }
            executions {
              execution(id: 'integration-tests', goals: ['install', 'run'], phase: 'post-integration-test')
            }
          }
        }
      }
    }
  }

  build {
    plugins {
      plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin')
      plugin(groupId: 'org.apache.maven.plugins', artifactId : 'maven-plugin-plugin', version: '3.6.0')
    }
  }

}
