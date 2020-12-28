package com.github.jaaa.util;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
@Fork( value=8, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15"} )

@State(Scope.Benchmark)
public class Hex16Benchmark
{
  public static void main( String... args ) throws RunnerException
  {
    var opt = new OptionsBuilder()
      .include( Hex16Benchmark.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  private static long swap1( long bits, int i, int j )
  {
    i <<= 2;
    j <<= 2;
    long mi = 15L << i,
         mj = 15L << j;
    j -= i;

    return bits & ~(mi|mj)
       | ((bits & mi)  << j)
       | ((bits & mj) >>> j);
  }

  private static long swap2( long bits, int i, int j )
  {
    i <<= 2;
    j <<= 2;
    // https://en.wikipedia.org/wiki/XOR_swap_algorithm
    long    m =   15L << j; j -= i;
    bits ^= m & bits  << j;
    bits ^=(m & bits)>>> j;
    bits ^= m & bits  << j;

    return bits;
  }

  private static long swap3( long bits, int i, int j )
  {
    i <<= 2;
    j <<= 2;
    long m = 15L << i;
    j -= i;

    return    bits & ~(m|m<<j)
      | ((m & bits) << j)
      | ( m & bits >>> j);
  }

  private static long swap4( long bits, int i, int j )
  {
    i <<= 2;
    j <<= 2;
    long mi = 15L << i,
         mj = 15L << j;
    j -= i;

    return     bits & ~(mi|mj)
      |  (mj & bits << j)
      |  (mi & bits>>> j);
  }

  private static long swap5( long bits, int i, int j )
  {
    i <<= 2;
    j <<= 2;
    long bi = bits & 15L << i; bits ^= bi;
    long bj = bits & 15L << j; bits ^= bj; j -= i;
    bits |= bj >>> j;
    bits |= bi <<  j;

    return bits;
  }

  private long bits;
  private int i,j;
  Random rng = new Random();

  @Setup(Level.Invocation)
  public void setup()
  {
    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new AssertionError();
    bits = rng.nextLong();
       j = rng.nextInt(15)+1;
       i = rng.nextInt(j);

    assert swap1(bits,i,j) == swap2(bits,i,j);
    assert swap2(bits,i,j) == swap3(bits,i,j);
    assert swap3(bits,i,j) == swap4(bits,i,j);
  }

  @Benchmark public void bench_swap1( Blackhole blackhole ) { blackhole.consume( swap1(bits,i,j) ); }
  @Benchmark public void bench_swap2( Blackhole blackhole ) { blackhole.consume( swap2(bits,i,j) ); }
  @Benchmark public void bench_swap3( Blackhole blackhole ) { blackhole.consume( swap3(bits,i,j) ); }
  @Benchmark public void bench_swap4( Blackhole blackhole ) { blackhole.consume( swap4(bits,i,j) ); }
  @Benchmark public void bench_swap5( Blackhole blackhole )
  {
    blackhole.consume( swap5(bits,i,j) );
  }
}
