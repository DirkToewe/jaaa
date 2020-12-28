plugins {
  java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
  sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
}

val v_junit = "5.7.0"
val v_jqwik = "1.3.10"
val v_assertj = "3.12.2"
val v_jmh = "1.26"

tasks.compileTestJava {
  options.compilerArgs.addAll( listOf("-parameters") )
}

tasks.test {
  useJUnitPlatform {
    includeEngines(
      "assertj-core",
//      "junit-jupiter",
      "jqwik"
    )
  }
  enableAssertions = true
  maxHeapSize = "2g"
  maxParallelForks = Runtime.getRuntime().availableProcessors()
  jvmArgs = listOf(
    "-ea",
    "--illegal-access=permit",
    "-XX:MaxInlineLevel=15"
  )
  include("**/*Properties.class")
  include("**/*Test.class")
  include("**/*Tests.class")
}

tasks.register<JavaExec>("benchParallelMergeSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.sort.ParallelMergeSortComparison"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx48g")
}

tasks.register<JavaExec>("compareMerge") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.merge.MergeComparison"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15")
}

tasks.register<JavaExec>("compareMergeParallel") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.merge.ParallelMergeComparison"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx48g")
}

dependencies {
  testImplementation("net.jqwik:jqwik:$v_jqwik")
  testImplementation("net.jqwik:jqwik-engine:$v_jqwik")
  testImplementation("org.assertj:assertj-core:$v_assertj")
  testImplementation("org.apache.commons:commons-math3:3.6.1")
  testImplementation("org.openjdk.jmh:jmh-core:$v_jmh")
  testImplementation("org.jsoup:jsoup:1.13.1")
  testImplementation("org.jparsec:jparsec:3.1")
  testImplementation("org.json:json:20201115")
  testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$v_jmh")
}
