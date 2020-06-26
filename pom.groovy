project {
  groupId 'org.avrodite'
  artifactId 'avrodite'
  version '0.2.0-SNAPSHOT'
  modelVersion '4.0.0'
  packaging 'pom'

  properties {
    'version.project' '${project.version}'
    'maven.compiler.source' '1.8'
    'maven.compiler.target' '1.8'
    'project.build.sourceEncoding' 'UTF-8'
    'version.build.compiler' '3.8.1'
    'version.build.jacoco' '0.8.5'
    'version.build.surefire' '3.0.0-M3'
    'version.kotlin' '1.3.61'
    'version.logging.logback' '1.2.3'
    'version.logging.kotlin-logging' '1.7.8'
    'version.spek' '2.0.9'
    'version.testing.jmh' '1.23'
    'java.home' '${env.JAVA_HOME}'
  }

  modules {
    module 'avrodite-api'
    module 'avrodite-avro'
    module 'avrodite-avro-maven-plugin'
    module 'avrodite-metadata'
    module 'avrodite-tools'
    module 'avrodite-tools-avro'
  }

  dependencyManagement {
    dependencies {
      /* intra-module dependecies*/
      dependency('org.avrodite:avrodite-api:${version.project}')
      dependency('org.avrodite:avrodite-avro:${version.project}')
      dependency('org.avrodite:avrodite-metadata:${version.project}')
      dependency('org.avrodite:avrodite-tools:${version.project}')
      dependency('org.avrodite:avrodite-tools-avro:${version.project}')
      /* core dependencies */
      dependency('io.github.microutils:kotlin-logging:${version.logging.kotlin-logging}')
      dependency('org.jetbrains.kotlin:kotlin-stdlib:${version.kotlin}')
      dependency('org.jetbrains.kotlin:kotlin-reflect:${version.kotlin}')
      dependency('ch.qos.logback:logback-classic:${version.logging.logback}')
      /* testing */
      dependency('org.spekframework.spek2:spek-dsl-jvm:${version.spek}:test')
      dependency('org.spekframework.spek2:spek-runner-junit5:${version.spek}:test')
      dependency("io.strikt:strikt-core:0.23.7:test")
      /* bench testing */
      dependency('org.openjdk.jmh:jmh-core:${version.testing.jmh}:test')
      dependency('org.openjdk.jmh:jmh-generator-annprocess:${version.testing.jmh}:test')
    }
  }

  dependencies {
    dependency('org.jetbrains.kotlin:kotlin-stdlib')
    dependency('io.github.microutils:kotlin-logging')
    dependency('ch.qos.logback:logback-classic')
    /* testing */
    dependency('org.spekframework.spek2:spek-dsl-jvm:${version.spek}:test')
    dependency('org.spekframework.spek2:spek-runner-junit5:${version.spek}:test')
    dependency("io.strikt:strikt-core:0.23.7:test")
  }

  build {
    pluginManagement {
      plugins {
        plugin(artifactId: 'maven-compiler-plugin', version: '${version.build.compiler}') {
          configuration {
            compilerArgs {
              compilerArg '-parameters'
            }
          }
          executions {
            execution(id: 'default-compile', phase: 'compile')
            /* compile shared testing fixtures */
            execution(id: 'default-testCompile', phase: 'test-compile') {
              configuration {
                compileSourceRoots('${project.basedir}/src/test/fixtures-java')
              }
            }
          }
        }
        plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin', version: '${version.kotlin}'){
          executions {
            execution(id: 'compile.kotlin.src', goals: 'compile', phase: 'compile'){
              configuration {
                sourceDirs {
                  sourceDir '${project.basedir}/src/main/kotlin'
                }
                jvmTarget '1.8'
              }
            }
            execution(id: 'compile.kotlin.test.fixtures', goals: 'test-compile', phase: 'test-compile'){
              configuration {
                sourceDirs {
                  sourceDir '${project.basedir}/src/test/fixtures-kotlin'
                }
                jvmTarget '1.8'
              }
            }
          }
        }
        plugin(artifactId: 'maven-surefire-plugin', version: '${version.build.surefire}') {
          executions {
            /* disable default surefire execution as the project doesn't use the conventional src/test/java directory */
            execution(id: 'default-test', phase: 'none')
          }
          configuration {
            includes {}
            useFile 'false'
            /*
            systemPropertyVariables {
              'MAVEN_TARGET_DIR' '${project.build.directory}'
            }
            */
          }
        }
      }
    }
  }

  profiles {
    profile(id: 'test.unit') {
      activation { property(name: 'test.profile', value: 'ci') }
      build {
        plugins {
          plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin', version: '${version.kotlin}'){
            executions {
              execution(id: 'compile.kotlin.test.unit', goals: 'test-compile'){
                configuration {
                  sourceDirs {
                    sourceDir '${project.basedir}/src/test/unit-tests'
                  }
                  output '${project.build.directory}/unit-tests-classes'
                  testOutput '${project.build.directory}/unit-tests-classes'
                  jvmTarget '1.8'
                }
              }
            }
          }
          plugin(artifactId: 'maven-surefire-plugin') {
            executions {
              execution(id: 'test.unit', goals: 'test') {
                configuration {
                  testSourceDirectory '${project.basedir}/src/test/unit-tests'
                  testClassesDirectory '${project.build.directory}/unit-tests-classes'
                  argLine '${surefireArgLine}'
                  includes {
                    include '**/*Spec'
                  }
                  additionalClasspathElements {
                    additionalClasspathElement '${project.build.directory}/test-classes'
                  }
                }
              }
            }
          }
        }
      }
    }
    profile(id: 'test.functional') {
      activation { property(name: 'test.profile', value: 'ci') }
      build {
        plugins {
          plugin(groupId: 'org.jetbrains.kotlin', artifactId: 'kotlin-maven-plugin', version: '${version.kotlin}'){
            executions {
              execution(id: 'compile.kotlin.test.functional', goals: 'test-compile'){
                configuration {
                  sourceDirs {
                    sourceDir '${project.basedir}/src/test/functional-tests'
                  }
                  output '${project.build.directory}/functional-tests-classes'
                  testOutput '${project.build.directory}/functional-tests-classes'
                  jvmTarget '1.8'
                }
              }
            }
          }
          plugin(artifactId: 'maven-surefire-plugin') {
            executions {
              execution(id: 'test.functional', goals: 'test') {
                configuration {
                  testSourceDirectory '${project.basedir}/src/test/functional-tests'
                  testClassesDirectory '${project.build.directory}/functional-tests-classes'
                  argLine '${surefireArgLine}'
                  includes {
                    include '**/*Spec'
                  }
                  additionalClasspathElements {
                    additionalClasspathElement '${project.build.directory}/test-classes'
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  repositories {
    repository {
      id 'jcenter'
      url 'https://jcenter.bintray.com'
    }
  }
}
