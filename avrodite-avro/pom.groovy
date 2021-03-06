project(artifactId: 'avrodite-avro', version: '0.1.0-SNAPSHOT') {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')
  modelVersion '4.0.0'

  dependencies {
    dependency('org.avrodite:avrodite-api')
    dependency('org.apache.avro:avro:1.9.1') { exclusions('org.slf4j:slf4j-api') }
    /* utilities */
    dependency('org.projectlombok:lombok')
    dependency('ru.vyarus:generics-resolver')
    /* logging */
    dependency('ch.qos.logback:logback-classic')
    dependency('org.jboss.logging:jboss-logging-annotations')
    dependency('org.jboss.logging:jboss-logging-processor')
    dependency('org.jboss.logging:jboss-logging')
    /* testing */
    dependency('org.junit.jupiter:junit-jupiter')
    dependency('org.junit.vintage:junit-vintage-engine')
    dependency('org.spockframework:spock-core')
    dependency('org.assertj:assertj-core')
  }

}
