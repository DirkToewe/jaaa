import java.nio.file.Files
import java.nio.file.Paths
import java.lang.Math.min
import java.util.stream.IntStream
import kotlin.text.Regex

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

val v_junit = "5.7.1"
val v_jqwik = "1.5.1"
val v_assertj = "3.12.2"
val v_jmh = "1.29"

tasks.compileTestJava {
  options.compilerArgs.addAll( listOf("-parameters") )
}

var memTotal   = Integer.MAX_VALUE
val memPerFork = 5

if( System.getProperty("os.name").toLowerCase() == "linux" )
{
  val pattern = "(?ui)\\s*MemAvailable\\s*:\\s*(?<kiloBytes>\\d+)\\s*kB\\s*".toRegex().toPattern()
  val memKiloBytes = Files.lines( Paths.get("/proc/meminfo") ).flatMapToInt{
    line ->
      val m = pattern.matcher(line)
      if( m.matches() )
        IntStream.of( m.group("kiloBytes").toInt() )
      else
        IntStream.empty()
  }.findFirst()

  if( memKiloBytes.isPresent() )
    memTotal = memKiloBytes.asInt / (1024*1024)
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
  maxHeapSize = "${memPerFork}g"
  maxParallelForks = min(memTotal/memPerFork, Runtime.getRuntime().availableProcessors())
  jvmArgs = listOf(
    "-ea",
    "--illegal-access=permit",
    "-XX:MaxInlineLevel=15"
//    "-Xdisablejavadump",
//    "-Xdump:none"
  )
  include("**/*Properties.class")
  include("**/*Test.class")
  include("**/*Tests.class")
}

tasks.register<JavaExec>("benchParallelMergeSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.sort.ParallelMergeSortComparison"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx12g")
}

tasks.register<JavaExec>("compareMerge") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.merge.MergeComparison"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15")
}

tasks.register<JavaExec>("compareSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.sort.BenchmarkSort"
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx12g")
}

tasks.register<JavaExec>("compareMergeParallel") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.merge.ParallelMergeComparison"
  jvmArgs = listOf(
    "-ea",
    "--illegal-access=warn",
    "-XX:MaxInlineLevel=15",
//    "-Xscmx50M",
//    "-XX:+UseParallelGC",
//    "-XX:+UseParallelOldGC",
    "-Xmx48g"
  )
}

tasks.register<JavaExec>("compareBiPartition") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  main = "com.github.jaaa.partition.BiPartitionComparison"
  jvmArgs = listOf("-ea", "-XX:MaxInlineLevel=15", "-Xmx48g")
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
//  testImplementation("org.junit.jupiter:junit-jupiter-api:$v_junit")
//  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$v_junit")
  testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$v_jmh")
}
