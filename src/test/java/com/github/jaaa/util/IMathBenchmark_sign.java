package com.github.jaaa.util;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 4, time = 4/*sec*/)
@Measurement(iterations = 4, time = 4/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx2g"} )

@State(Scope.Benchmark)
public class IMathBenchmark_sign
{
  public static void main( String... args ) throws RunnerException
  {
    var opt = new OptionsBuilder()
      .include( IMathBenchmark_sign.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  Random rng;
  int n;

  @Setup(Level.Trial)
  public void init() {
    rng = new Random();
  }

//  @Setup(Level.Invocation)
//  public void advance() {
//    n = rng.nextInt();
//  }

  @Benchmark public int signV1() {
    int n = rng.nextInt();
    return n==0 ? 0 : 1-(n>>>31<<1);
  }

  @Benchmark public int signV2() {
    int n = rng.nextInt();
    return n==0 ? 0 : 1 - (2 & n>>>30);
  }

  @Benchmark public int signV3() {
    int n = rng.nextInt();
    return (n >> 31) | (-n >>> 31);
  }

  @Benchmark public int signReference() {
    int n = rng.nextInt();
    return n < 0 ? -1
         : n > 0 ? +1 : 0;
  }
}
