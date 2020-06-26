project {

  parent(groupId: 'org.avrodite', artifactId: 'avrodite', version: '0.2.0-SNAPSHOT', relativePath: '../pom.groovy')
  artifactId 'avrodite-tools-avro'
  modelVersion '4.0.0'
  dependencies {
    dependency 'org.avrodite:avrodite-api'
    dependency 'org.avrodite:avrodite-avro'
    dependency 'org.avrodite:avrodite-tools'
    dependency 'org.avrodite:avrodite-metadata'
    dependency('com.google.guava:guava:28.2-jre')
    dependency('org.apache.maven:maven-core:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven:maven-plugin-api:3.6.3'){ exclusions('org.slf4j:slf4j-api', 'com.google.guava:guava') }
    dependency('org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.0:provided')
    dependency 'org.openjdk.jmh:jmh-core'
    dependency 'org.openjdk.jmh:jmh-generator-annprocess'
  }

  build {
    plugins {
      plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin')
      plugin(artifactId: 'maven-compiler-plugin')
    }
  }

}
