project {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite', version: '0.2.0-SNAPSHOT', relativePath: '../pom.groovy')
  artifactId 'avrodite-metadata'
  modelVersion '4.0.0'
  dependencies {
    dependency('org.jetbrains.kotlin:kotlin-reflect')
  }
  build {
    plugins {
      plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin')
    }
  }
}
