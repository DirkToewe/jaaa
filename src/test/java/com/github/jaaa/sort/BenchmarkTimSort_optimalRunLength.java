package com.github.jaaa.sort;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.numberOfLeadingZeros;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 4, time = 4/*sec*/)
@Measurement(iterations = 4, time = 4/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx2g"} )

@State(Scope.Benchmark)
public class BenchmarkTimSort_optimalRunLength
{
  public static void main( String... args ) throws RunnerException
  {
    Options opt = new OptionsBuilder()
      .include( BenchmarkTimSort_optimalRunLength.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  private static final int MIN_MERGE = 32;

  static int minRunLenJDK( int n )
  {
    assert n >= 0;
    int r = 0; // Becomes 1 if any 1 bits are shifted off
    while (n >= MIN_MERGE) {
      r |= (n & 1);
      n >>= 1;
    }
    return n + r;
  }

  static int minRunLenJaaaV1( int n )
  {
    if( n < MIN_MERGE )
      return n;
    int s = 27 - numberOfLeadingZeros(n),
        l = n>>>s;
    if( l<<s != n )
      ++l;
    return l;
  }

  static int minRunLenJaaaV2( int len )
  {
    int minRunLen = 16;
    if( minRunLen <= 0 || len <  0 ) throw new IllegalArgumentException();
    int                              s = 0;
    if(   len>>>  16  >= minRunLen ) s =16;
    if(   len>>>(s|8) >= minRunLen ) s|= 8;
    if(   len>>>(s|4) >= minRunLen ) s|= 4;
    if(   len>>>(s|2) >= minRunLen ) s|= 2;
    if(   len>>>(s|1) >= minRunLen ) s|= 1;
    int l=len>>> s;
    return l<<s != len ? l+1 : l;
  }

  static int minRunLenJaaaV3( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    int minRunLen = 16;
    if( len <= minRunLen ) return len;
    int s = 31 - numberOfLeadingZeros(len/minRunLen),
        l = len>>>s;
    return l<<s != len ? l+1 : l;
  }

  private SplittableRandom rng = new SplittableRandom();
  private int n;

  @Setup(Level.Invocation)
  public void randomize() {
    n = rng.nextInt() & Integer.MAX_VALUE;
  }

  @Benchmark public void bench_minRunLenJDK   ( Blackhole bh ) { bh.consume( minRunLenJDK(n) ); }
  @Benchmark public void bench_minRunLenJaaaV1( Blackhole bh ) { bh.consume( minRunLenJaaaV1(n) ); }
  @Benchmark public void bench_minRunLenJaaaV2( Blackhole bh ) { bh.consume( minRunLenJaaaV2(n) ); }
  @Benchmark public void bench_minRunLenJaaaV3( Blackhole bh ) { bh.consume( minRunLenJaaaV3(n) ); }
  @Benchmark public void bench_optRunLen      ( Blackhole bh ) { bh.consume( TimSort.optimalRunLength(16,n) ); }
}
