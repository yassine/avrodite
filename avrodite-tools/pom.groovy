project(artifactId: 'avrodite-tools', version: '0.1.0-SNAPSHOT') {
  modelVersion '4.0.0'
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')

  dependencies {
    dependency('org.avrodite:avrodite-api')
    dependency('io.pebbletemplates:pebble:3.1.0') { exclusions('org.slf4j:slf4j-api') }
    dependency('org.jgrapht:jgrapht-core:1.3.1')
    /* utilities */
    dependency('com.machinezoo.noexception:noexception')
    dependency('org.projectlombok:lombok')
    dependency('ru.vyarus:generics-resolver')
    dependency('io.github.classgraph:classgraph')
    /* logging */
    dependency('ch.qos.logback:logback-classic')
    dependency('org.jboss.logging:jboss-logging-annotations')
    dependency('org.jboss.logging:jboss-logging-processor')
    dependency('org.jboss.logging:jboss-logging')
    /* testing */
    dependency('org.junit.jupiter:junit-jupiter')
    dependency('org.junit.vintage:junit-vintage-engine')
    dependency('org.spockframework:spock-core')
  }

}
