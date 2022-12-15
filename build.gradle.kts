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
  sourceCompatibility = JavaVersion.VERSION_17
}

val v_jqwik = "1.7.0"
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
val memPerFork = 6

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
//      "junit-jupiter",
      "jqwik"
    )
  }
  enableAssertions = true
  maxHeapSize = "${memPerFork}g"
  maxParallelForks = max( 1, min(2*memTotal/memPerFork, getRuntime().availableProcessors()) )
  jvmArgs = listOf(
    "-ea",
    "--illegal-access=permit",
    "-XX:MaxInlineLevel=15",
    "-XX:AutoBoxCacheMax=1000000",
//    "--add-modules=jdk.incubator.vector"
//    "-Xdisablejavadump",
//    "-Xdump:none"
  )
  include(
    "**/*Properties.class",
    "**/*Test.class",
    "**/*Tests.class"
  )
}

tasks.register<JavaExec>("benchParallelMergeSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.sort.ParallelMergeSortComparison")
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx12g")
}

tasks.register<JavaExec>("compareMerge") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.merge.MergeComparison")
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15")
}

tasks.register<JavaExec>("compareSort") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.sort.BenchmarkSort")
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx12g")
}

tasks.register<JavaExec>("compareMergeParallel") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.merge.ParallelMergeComparison")
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

tasks.register<JavaExec>("compareGcd_int") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.util.IMathBenchmark_gcd_int")
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx6g")
}

tasks.register<JavaExec>("compareGcd_long") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.util.IMathBenchmark_gcd_long")
  jvmArgs = listOf("-ea", "--illegal-access=warn", "-XX:MaxInlineLevel=15", "-Xmx6g")
}

tasks.register<JavaExec>("compareBiPartition") {
  dependsOn(tasks.compileTestJava)
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("com.github.jaaa.partition.BiPartitionComparison")
  jvmArgs = listOf("-ea", "-XX:MaxInlineLevel=15", "-Xmx48g")
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
