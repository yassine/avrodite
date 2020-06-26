project {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite', version: '0.2.0-SNAPSHOT', relativePath: '../pom.groovy')
  artifactId 'avrodite-avro'
  modelVersion '4.0.0'
  dependencies {
    dependency 'org.avrodite:avrodite-api'
    dependency('org.apache.avro:avro:1.9.1') { exclusions('org.slf4j:slf4j-api') }
    dependency('org.openjdk.jmh:jmh-core')
    dependency('org.openjdk.jmh:jmh-generator-annprocess')
  }
  build {
    plugins {
      plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin')
    }
  }
}
