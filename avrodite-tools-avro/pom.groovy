project(artifactId: 'avrodite-tools-avro', version: '0.1.0-SNAPSHOT') {
  modelVersion '4.0.0'
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')

  dependencies {
    dependency('org.avrodite:avrodite-avro')
    dependency('org.avrodite:avrodite-tools')
    dependency('com.google.auto.service:auto-service:1.0-rc6:provided')
    dependency('org.apache.avro:avro:1.9.1') { exclusions('org.slf4j:slf4j-api') }
    /* utilities */
    dependency('org.projectlombok:lombok')
    /* logging */
    dependency('ch.qos.logback:logback-classic')
    /* testing */
    dependency('org.junit.jupiter:junit-jupiter')
    dependency('org.junit.vintage:junit-vintage-engine')
    dependency('org.spockframework:spock-core')
  }

}
