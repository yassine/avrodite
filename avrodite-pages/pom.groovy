project(artifactId: 'avrodite-pages', version: '0.1.0-SNAPSHOT') {
  modelVersion '4.0.0'
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')
  packaging 'pom'

  dependencies {
    dependency('org.avrodite:avrodite-api:${version.project}')
    dependency('org.avrodite:avrodite-avro:${version.project}')
    dependency('org.avrodite:avrodite-tools:${version.project}')
    dependency('org.avrodite:avrodite-tools-avro:${version.project}')
  }

  profiles {
    profile(id: 'test.coverage') {
      build {
        plugins {
          plugin(groupId: 'org.jacoco', artifactId: 'jacoco-maven-plugin', version: '${version.build.jacoco}') {
            executions {
              execution(id: 'aggregate-report', phase: 'post-integration-test', goals: 'report-aggregate') {
                configuration {
                  outputDirectory '${project.parent.build.directory}/aggregate-coverage'
                }
              }
            }
          }
        }
      }
    }
  }

}
