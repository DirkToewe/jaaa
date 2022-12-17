import java.lang.Runtime.getRuntime
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.IntStream
import kotlin.math.max
import kotlin.math.min

plugins {
  java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenLocal()
  mavenCentral()
  maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

val v_jqwik = "1.7.1"
val v_assertj = "3.23.1"//"3.21.0"
val v_jmh = "1.35"

tasks.compileJava {
  options.compilerArgs.addAll(
    listOf(
      "-parameters",
//      "--add-modules=jdk.incubator.vector"
    )
  )
}

tasks.compileTestJava {
  options.compilerArgs.addAll(
    listOf(
      "-parameters",
//      "--add-exports=java.base/sun.net.util=ALL-UNNAMED",
//      "--add-exports=java.management/sun.management=ALL-UNNAMED",
//      "--add-exports=java.rmi/sun.rmi.registry=ALL-UNNAMED",
//      "--add-exports=java.security.jgss/sun.security.krb5=ALL-UNNAMED",
//      "--add-modules=jdk.incubator.vector"
    )
  )
}

var memTotal   = Integer.MAX_VALUE
val memPerFork = 5

if( System.getProperty("os.name").decapitalize() == "linux" )
{
  val pattern = "(?ui)\\s*MemAvailable\\s*:\\s*(?<kiloBytes>\\d+)\\s*kB\\s*".toRegex().toPattern()
  val memKiloBytes = Files.lines( Paths.get("/proc/meminfo") ).flatMapToInt{
    line ->
      val m = pattern.matcher(line)
      if( m.matches() ) IntStream.of( m.group("kiloBytes").toInt() )
      else              IntStream.empty()
  }.findFirst()

  if( memKiloBytes.isPresent() )
    memTotal = memKiloBytes.asInt / (1024*1024)
}

tasks.test {
  useJUnitPlatform {
    includeEngines(
      "assertj-core",
      "jqwik"
    )
  }
  enableAssertions = true
  maxHeapSize = "${memPerFork}g"
  maxParallelForks = max( 1, min(memTotal/memPerFork, getRuntime().availableProcessors()) )
  jvmArgs = listOf(
    "-ea",
//    "--illegal-access=permit",
//    "--add-modules=jdk.incubator.vector",
//    "-Xdisablejavadump",
//    "-Xdump:none",
  )
  include(
    "**/*Properties.class",
    "**/*Test.class",
    "**/*Tests.class"
  )
}

tasks.register<JavaExec>("benchmarkMerge") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.merge.BenchmarkMerge")
  jvmArgs = listOf("-ea")
}

tasks.register<JavaExec>("benchmarkSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.sort.BenchmarkSort")
  jvmArgs = listOf("-ea")
}

tasks.register<JavaExec>("benchmarkGcdInt") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.util.IMathBenchmark_gcd_int")
  jvmArgs = listOf("-ea")
}

tasks.register<JavaExec>("benchmarkGcdLong") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.util.IMathBenchmark_gcd_long")
  jvmArgs = listOf("-ea")
}

tasks.register<JavaExec>("benchmarkBiPartition") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.partition.BenchmarkBiPartition")
  jvmArgs = listOf("-ea")
}

dependencies {
  implementation("org.apache.commons:commons-math3:3.6.1")
  testImplementation("com.github.haifengl:smile-core:3.0.0")
  testImplementation("org.bytedeco:openblas:0.3.19-1.5.7:linux-x86_64")
  testImplementation("org.bytedeco:arpack-ng:3.8.0-1.5.7:linux-x86_64")
  testImplementation("net.jqwik:jqwik:$v_jqwik")
  testImplementation("net.jqwik:jqwik-engine:$v_jqwik")
  testImplementation("org.assertj:assertj-core:$v_assertj")
  testImplementation("org.apache.commons:commons-math3:3.6.1")
  testImplementation("org.openjdk.jmh:jmh-core:$v_jmh")
  testImplementation("org.jparsec:jparsec:3.1")
  testImplementation("org.json:json:20220924")
  testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$v_jmh")
}
