project(artifactId: 'avrodite-api', version: '0.1.0-SNAPSHOT') {
  modelVersion '4.0.0'
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')

  dependencies {
    dependency('org.projectlombok:lombok')
    dependency('ru.vyarus:generics-resolver')
    /* logging */
    dependency('ch.qos.logback:logback-classic')
    dependency('org.jboss.logging:jboss-logging-annotations')
    dependency('org.jboss.logging:jboss-logging-processor')
    dependency('org.jboss.logging:jboss-logging')
  }

}
