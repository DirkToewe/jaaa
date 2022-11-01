package com.github.jaaa.util;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.lang.Math.min;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 64, time = 16/*sec*/)
@Measurement(iterations = 16, time = 16/*sec*/)
@Fork(
  value = 4,
  jvmArgsAppend = {"-XX:MaxInlineLevel=15", "-Xmx8g"}
)

@State(Scope.Benchmark)
public class IMathBenchmark_gcd_long
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String... args ) throws RunnerException, IOException
  {
    boolean ea = false;
    assert  ea = true;
    if   ( !ea ) throw new IllegalStateException();

    var rng = new Random(1337);
    System.out.print("\nRunning tests...");
    for( int run=0; run++ < 1_000_000; )
    {
      long x = rng.nextLong(),
           y = rng.nextLong();
      assert gcd1(x,y) == gcd2(x,y);
      assert gcd1(x,y) == gcd3(x,y);
    }
    System.out.println(" passed!\n");

    var opt = new OptionsBuilder()
      .include( IMathBenchmark_gcd_long.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  private static long gcd1( long x, long y )
  {
    x = abs(x);
    y = abs(y);

    for( int s=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << s;

      long l = x & 1 ^ 1; x >>>= l;
      long r = y & 1 ^ 1; y >>>= r;
      s += l & r;

      if( (l|r) == 0 ) {
        if( x > y ) x = x-y >>> 1;
        else        y = y-x >>> 1;
      }
    }
  }

  private static long gcd2( long x, long y )
  {
    long sx = x>>63; x = (x^sx) - sx; // <- x = abs(x)
    long sy = y>>63; y = (y^sy) - sy; // <- y = abs(y)

    for( int shift=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      long l = x & 1 ^ 1; x >>>= l;
      long r = y & 1 ^ 1; y >>>= r;
      shift += l & r;

      // xy = |y-x| / 2
      long xy = y-x;long z = xy>>63;
           xy = (xy^z) - z; // <- xy = abs(xy)
           xy >>>= 1;
      long a = -(l|r);
      z |= a;
      // <- x,y = |x-y|/2, min(x,y)
      y = (y & z | ~z & x );
      x = (x & a | ~a & xy);
    }
  }

  private static long gcd3( long x, long y )
  {
    x = abs(x);
    y = abs(y);
    int l = Long.numberOfTrailingZeros(x); x >>>= l;
    int r = Long.numberOfTrailingZeros(y); y >>>= r;
    int shift = min(l,r);
    if( x > y ) {
      long z = x; x = y; y = z;
    }
    while( x != 0 ) {
      // Interleave euclidean and binary GCD.
      // Ensures that y halves each iteration.
      long z = x > (y>>>1) ? y-x : y%x;
      y = x;
      x = z;
      // Invariant: x <= y && (y is odd)
      x >>>= Long.numberOfTrailingZeros(x);
    }
    return y << shift;
  }

  // FIELDS
  private final Random rng = new Random();

  // CONSTRUCTORS
// METHODS
  @Benchmark
  public void gcd1( Blackhole blackhole ) {
    long x = rng.nextLong(),
         y = rng.nextLong();
    blackhole.consume( gcd1(x,y) );
  }

  @Benchmark
  public void gcd2( Blackhole blackhole ) {
    long x = rng.nextLong(),
         y = rng.nextLong();
    blackhole.consume( gcd2(x,y) );
  }

  @Benchmark
  public void gcd3( Blackhole blackhole ) {
    long x = rng.nextLong(),
         y = rng.nextLong();
    blackhole.consume( gcd3(x,y) );
  }
}
