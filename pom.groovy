project(groupId: 'org.avrodite', artifactId: 'avrodite-parent', version: '0.1.0-SNAPSHOT') {

  modelVersion '4.0.0'
  packaging 'pom'
  modules {
    module 'avrodite-api'
    module 'avrodite-avro'
    module 'avrodite-avro-benchmarks'
    module 'avrodite-tools'
    module 'avrodite-tools-avro'
    module 'avrodite-avro-maven-plugin'
  }

  properties {
    'version.project' '${project.version}'
    'maven.compiler.source' '1.8'
    'maven.compiler.target' '1.8'
    'project.build.sourceEncoding' 'UTF-8'
    'version.build.jacoco' '0.8.5'
    'version.build.surefire' '3.0.0-M3'
    'version.build.compiler' '3.8.1'
    'version.build.gmaven' '1.8.1'
    'version.logging.jboss' '3.4.1.Final'
    'version.logging.jboss.ann' '2.2.0.Final'
    'version.logging.logback' '1.2.3'
    'version.testing.jmh' '1.23'
    'version.testing.junit' '5.5.2'
    'version.utilities.auto.service' '1.0-rc6'
    'version.utilities.classgraph' '4.8.53'
    'version.utilities.lombok' '1.18.12'
    'version.utilities.no-exception' '1.4.4'
    'version.utilities.vyarus-generics' '3.0.1'
    'java.home' '${env.JAVA_HOME}'
  }

  dependencyManagement {
    dependencies {
      /* intra modules dependencies */
      dependency('org.avrodite:avrodite-api:0.1.0-SNAPSHOT')
      dependency('org.avrodite:avrodite-avro:0.1.0-SNAPSHOT')
      dependency('org.avrodite:avrodite-tools:0.1.0-SNAPSHOT')
      dependency('org.avrodite:avrodite-tools-avro:0.1.0-SNAPSHOT')

      dependency('com.google.auto.service:auto-service:1.0-rc6:provided')
      /* java core language utilities */
      dependency('com.machinezoo.noexception:noexception:${version.utilities.no-exception}') { exclusions('org.slf4j:slf4j-api') }
      dependency('io.github.classgraph:classgraph:${version.utilities.classgraph}')
      dependency('org.projectlombok:lombok:${version.utilities.lombok}:provided')
      dependency('ru.vyarus:generics-resolver:${version.utilities.vyarus-generics}')
      /* logging */
      dependency('ch.qos.logback:logback-classic:${version.logging.logback}')
      dependency('org.jboss.logging:jboss-logging-annotations:${version.logging.jboss.ann}:provided')
      dependency('org.jboss.logging:jboss-logging-processor:${version.logging.jboss.ann}:provided')
      dependency('org.jboss.logging:jboss-logging:${version.logging.jboss}')
      /* testing */
      dependency('org.junit.jupiter:junit-jupiter:${version.testing.junit}:test')
      dependency 'org.junit.vintage:junit-vintage-engine:${version.testing.junit}:test'
      dependency('org.hamcrest:hamcrest:2.2:test')
      dependency('org.assertj:assertj-core:3.15.0:test')
      dependency('org.openjdk.jmh:jmh-core:${version.testing.jmh}:test')
      dependency('org.openjdk.jmh:jmh-generator-annprocess:${version.testing.jmh}:test')
      dependency('org.spockframework:spock-core:1.3-groovy-2.5:test')
    }
  }

  build {
    plugins {
      plugin(artifactId: 'maven-compiler-plugin', version: '${version.build.compiler}') {
        executions {
          /* compile shared testing fixtures */
          execution(id: 'default-testCompile', phase: 'test-compile') {
            configuration {
              compileSourceRoots('${project.basedir}/src/test/fixtures')
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
          systemPropertyVariables {
            'MAVEN_TARGET_DIR' '${project.build.directory}'
          }
        }
      }
      plugin(groupId: 'org.codehaus.gmavenplus', artifactId: 'gmavenplus-plugin', version: '${version.build.gmaven}') {
        dependencies {
          dependency('org.codehaus.groovy:groovy-all:2.5.4') { type 'pom' }
        }
      }
    }
  }

  profiles {
    profile(id: 'test.coverage') {
      build {
        plugins {
          plugin(groupId: 'org.jacoco', artifactId: 'jacoco-maven-plugin', version: '${version.build.jacoco}') {
            executions {
              execution(id: 'prepare-agent', phase: 'test-compile', goals: 'prepare-agent') {
                configuration {
                  destFile '${project.build.directory}/coverage-reports/jacoco-ut.exec'
                  propertyName 'surefireArgLine'
                }
              }
              execution(id: 'post-test-reports', phase: 'post-integration-test', goals: 'report') {
                configuration {
                  dataFile '${project.build.directory}/coverage-reports/jacoco-ut.exec'
                  outputDirectory '${project.reporting.outputDirectory}/code-coverage'
                  //exclude jboss-logging generated classes
                  excludes('**/*_$bundle.*')
                }
              }
            }
          }
        }
      }
    }
    profile(id: 'test.functional') {
      //enables functional testing compilation & execution
      activation {
        property(name: 'test.profile', value: 'all')
        property(name: 'test.profile', value: 'ci')
      }
      build {
        plugins {
          /* compile functional test java code */
          plugin(artifactId: 'maven-compiler-plugin') {
            executions {
              execution(id: 'functional-tests', phase: 'test-compile', goals: 'testCompile') {
                configuration {
                  compileSourceRoots '${project.basedir}/src/test/functional-tests'
                  outputDirectory '${project.build.directory}/functional-tests-classes'
                }
              }
            }
          }
          /* compile functional test groovy code */
          plugin(groupId: 'org.codehaus.gmavenplus', artifactId: 'gmavenplus-plugin') {
            executions {
              execution(id: 'generate-groovy-functional-tests', goals: 'compileTests') {
                configuration {
                  testSources {
                    testSource {
                      directory '${project.basedir}/src/test/functional-tests'
                      includes('**/*Spec.groovy')
                    }
                  }
                  testOutputDirectory '${project.build.directory}/functional-tests-classes'
                }
              }
            }
          }
          /* run compiled functional tests */
          plugin(artifactId: 'maven-surefire-plugin') {
            executions {
              execution(id: 'functional-tests', goals: 'test') {
                configuration {
                  includes('**/*Spec')
                  testClassesDirectory '${project.build.directory}/functional-tests-classes'
                  testSourceDirectory '${project.basedir}/src/test/functional-tests'
                  argLine '${surefireArgLine}'
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
    profile(id: 'test.unit') {
      activation {
        property(name: 'test.profile', value: 'all')
        property(name: 'test.profile', value: 'ci')
      }
      build {
        plugins {
          plugin(groupId: 'org.codehaus.gmavenplus', artifactId: 'gmavenplus-plugin') {
            executions {
              execution(id: 'generate-groovy-unit-tests', goals: 'compileTests') {
                configuration {
                  testSources {
                    testSource {
                      directory '${project.basedir}/src/test/unit-tests'
                      includes('**/*Test.groovy')
                    }
                  }
                  testOutputDirectory '${project.build.directory}/unit-tests-classes'
                }
              }
            }
          }
          plugin(artifactId: 'maven-compiler-plugin') {
            executions {
              execution(id: 'unit-tests-compile', phase: 'test-compile', goals: 'testCompile') {
                configuration {
                  outputDirectory '${project.build.directory}/unit-tests-classes'
                  compileSourceRoots '${project.basedir}/src/test/unit-tests'
                }
              }
            }
          }
          plugin(artifactId: 'maven-surefire-plugin') {
            executions {
              execution(id: 'unit-tests', goals: 'test') {
                configuration {
                  testSourceDirectory '${project.basedir}/src/test/unit-tests'
                  testClassesDirectory '${project.build.directory}/unit-tests-classes'
                  argLine '${surefireArgLine}'
                  includes {
                    include '**/*Test'
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
    profile(id: 'test.bench') {
      build {
        plugins {
          plugin(artifactId: 'maven-compiler-plugin') {
            executions {
              execution(id: 'bench-tests-compile', phase: 'test-compile', goals: 'testCompile') {
                configuration {
                  outputDirectory '${project.build.directory}/bench-tests-classes'
                  compileSourceRoots '${project.basedir}/src/test/bench-tests'
                }
              }
            }
          }
          plugin(artifactId: 'maven-surefire-plugin') {
            executions {
              execution(id: 'bench-tests', goals: 'test') {
                configuration {
                  argLine ' '//no coverage to not bias the benchmarking results
                  testSourceDirectory '${project.basedir}/src/test/bench-tests'
                  testClassesDirectory '${project.build.directory}/bench-tests-classes'
                  includes {
                    include '**/*Benchmark'
                  }
                }
              }
            }
          }
        }
      }
    }
    profile(id: 'java.tools') {
      activation {
        file(exists: '${java.home}/lib/tools.jar')
      }
      dependencies {
        dependency('com.sun:tools:${maven.compiler.source}:system') { systemPath '${java.home}/lib/tools.jar' }
      }
    }
  }

}
