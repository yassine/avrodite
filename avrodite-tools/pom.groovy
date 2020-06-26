project {
  parent(groupId: 'org.avrodite', artifactId: 'avrodite', version: '0.2.0-SNAPSHOT', relativePath: '../pom.groovy')
  artifactId 'avrodite-tools'
  modelVersion '4.0.0'
  dependencies {
    dependency('io.github.classgraph:classgraph:4.8.34')
    dependency('org.avrodite:avrodite-api')
    dependency('org.avrodite:avrodite-metadata')
    dependency('org.freemarker:freemarker:2.3.30')
    dependency('org.jetbrains.kotlin:kotlin-stdlib')
    dependency('org.jetbrains.kotlin:kotlin-compiler:${version.kotlin}')
    dependency('org.jetbrains.kotlin:kotlin-reflect:${version.kotlin}')
    dependency('org.jetbrains.kotlin:kotlin-scripting-compiler:${version.kotlin}')
  }
  build {
    plugins {
      plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin')
    }
  }
}
