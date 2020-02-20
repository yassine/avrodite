project(artifactId: 'avrodite-codec-avro-benchmarks', version: '0.1.0-SNAPSHOT') {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT', relativePath: '../pom.groovy')
  modelVersion '4.0.0'

  dependencies {
    dependency('org.avrodite:avrodite-api')
    dependency('org.avrodite:avrodite-codec-avro')
    dependency('org.avrodite:avrodite-tools-avro')
    /* utilities */
    dependency('org.projectlombok:lombok')
    /* logging */
    dependency('ch.qos.logback:logback-classic')
    dependency('org.jboss.logging:jboss-logging-annotations')
    dependency('org.jboss.logging:jboss-logging-processor')
    dependency('org.jboss.logging:jboss-logging')
    /* testing */
    dependency('tech.tablesaw:tablesaw-core:0.37.3:test')
    dependency('tech.tablesaw:tablesaw-jsplot:0.37.3:test')
    dependency('com.fasterxml.jackson.dataformat:jackson-dataformat-avro:2.8.5:test')
    dependency('com.google.protobuf:protobuf-java:3.11.3:test')
    dependency('org.assertj:assertj-core')
    dependency('org.junit.jupiter:junit-jupiter')
    dependency('org.junit.vintage:junit-vintage-engine')
    dependency('org.openjdk.jmh:jmh-core')
    dependency('org.openjdk.jmh:jmh-generator-annprocess')
    dependency('org.spockframework:spock-core')
    /* selenium: hack to render charts */
    dependency('org.seleniumhq.selenium:selenium-firefox-driver:3.141.59:test')
    dependency('org.seleniumhq.selenium:selenium-java:3.141.59:test')
    dependency('commons-io:commons-io:2.6:test')
  }

}
