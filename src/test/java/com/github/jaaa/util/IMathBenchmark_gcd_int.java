package com.github.jaaa.util;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;
import static java.lang.Math.min;

// REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
// why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
// experiments, perform baseline and negative tests that provide experimental control, make sure
// the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
// Do not assume the numbers tell you what you want them to tell.
//
// Benchmark                    Mode  Cnt    Score   Error  Units
// IMathBenchmark_gcd_int.gcd0  avgt   64   95.016 ± 0.317  ns/op
// IMathBenchmark_gcd_int.gcd1  avgt   64  227.999 ± 0.928  ns/op
// IMathBenchmark_gcd_int.gcd2  avgt   64  147.464 ± 0.378  ns/op
// IMathBenchmark_gcd_int.gcd3  avgt   64  164.220 ± 0.183  ns/op
// IMathBenchmark_gcd_int.gcd4  avgt   64  123.012 ± 0.231  ns/op
// IMathBenchmark_gcd_int.gcd5  avgt   64  108.695 ± 0.245  ns/op
// IMathBenchmark_gcd_int.gcd6  avgt   64   82.146 ± 0.553  ns/op
// :compareGcd (Thread[Execution worker for ':',5,main]) completed. Took 4 hrs 59 mins 25.545 secs.
//
// BUILD SUCCESSFUL in 4h 59m 29s
// 4 actionable tasks: 2 executed, 2 up-to-date
// Watching 95 directories to track changes
// 05:36:00: Execution finished 'compareGcd --info'.

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 64, time = 8/*sec*/)
@Measurement(iterations = 16, time = 8/*sec*/)
@Fork( value=4, jvmArgsAppend={"-XX:MaxInlineLevel=15", "-Xmx16g"} )

@State(Scope.Benchmark)
public class IMathBenchmark_gcd_int
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void main( String... args ) throws RunnerException, IOException
  {
    boolean ea = false;
    assert  ea = true;
    if   ( !ea ) throw new IllegalStateException();

    var rng = new SplittableRandom();
    System.out.print("Running test 1...");

    for( int x=-4_000; x < 4_000; x++ )
    for( int y=-4_000; y < 4_000; y++ )
    {
      assert gcd0(x,y) == gcd1(x,y);
      assert gcd0(x,y) == gcd2(x,y);
      assert gcd0(x,y) == gcd3(x,y);
      assert gcd0(x,y) == gcd4(x,y);
      assert gcd0(x,y) == gcd5(x,y);
      assert gcd0(x,y) == gcd6(x,y);
    }

    System.out.println(" passed!");

    System.out.print("Running test 2...");

    for( int run=0; run++ < 10_000_000; )
    {
      int x = rng.nextInt(),
          y = rng.nextInt();
      assert gcd0(x,y) == gcd1(x,y);
      assert gcd0(x,y) == gcd2(x,y);
      assert gcd0(x,y) == gcd3(x,y);
      assert gcd0(x,y) == gcd4(x,y);
      assert gcd0(x,y) == gcd5(x,y);
      assert gcd0(x,y) == gcd6(x,y);
    }

    System.out.println(" passed!");

    var opt = new OptionsBuilder()
      .include( IMathBenchmark_gcd_int.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  private static int gcd0( int x, int y )
  {
    while( y != 0 )
      y = x % (x=y);
    return abs(x);
  }

  private static int gcd1( int x, int y )
  {
    x = abs(x);
    y = abs(y);

    for( int s=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << s;

      int l = x & 1 ^ 1; x >>>= l;
      int r = y & 1 ^ 1; y >>>= r;
      s += l & r;

      if( (l|r) == 0 ) {
        if( x > y ) x = x-y >>> 1;
        else        y = y-x >>> 1;
      }
    }
  }

  private static int gcd2( int x, int y )
  {
    int sx = x>>31; x = (x^sx) - sx; // <- x = abs(x)
    int sy = y>>31; y = (y^sy) - sy; // <- y = abs(y)

    for( int shift=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      int l = x & 1 ^ 1; x >>>= l;
      int r = y & 1 ^ 1; y >>>= r;
      shift += l & r;

      // xy = |y-x| / 2
      int xy = y-x; int z = xy>>31;
          xy = (xy^z) - z; // <- xy = abs(xy)
          xy >>>= 1;
      int  a = -(l|r);
      z |= a;
      // <- x,y = |x-y|/2, min(x,y)
      y = (y & z | ~z & x );
      x = (x & a | ~a & xy);
    }
  }

  private static int gcd3( int x, int y )
  {
    int sx = x>>31; x = (x^sx) - sx; // <- x = abs(x)
    int sy = y>>31; y = (y^sy) - sy; // <- y = abs(y)

    for( int shift=0;; ) {
      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      int l = x & 1 ^ 1; x >>>= l;
      int r = y & 1 ^ 1; y >>>= r;
      shift += l & r;

      int a = ~-(l|r);
      int b = x-y >> 31; y -= b & a & x; y >>>= -b & a;
          b = ~b;        x -= b & a & y; x >>>= -b & a;
    }
  }

//  private static int gcd4( int x, int y )
//  {
//    int sx = x>>31; x = (x^sx) - sx; // <- x = abs(x)
//    int sy = y>>31; y = (y^sy) - sy; // <- y = abs(y)
//
//    for( int shift=0;; ) {
//      int l = numberOfTrailingZeros(x); x >>>= l;
//      int r = numberOfTrailingZeros(y); y >>>= r;
//      shift += min(l,r);
//
//      int xy = x-y,
//              sxy = xy>>31,
//              min_xy = y ^ ((y^x) & sxy);
//
//      if( min_xy==0 || x==y )
//        return (x|y) << shift;
//
//      y = min_xy;//y &~sxy | x & sxy; // <- y = min(x,y)
//      xy = (xy^sxy) - sxy; // <- xy = abs(xy)
//      x = xy >>> 1;
//    }
//  }

  private static int gcd4( int x, int y )
  {
    x = abs(x);
    y = abs(y);
    for( int shift=0;;) {
      int l = Integer.numberOfTrailingZeros(x); x >>>= l;
      int r = Integer.numberOfTrailingZeros(y); y >>>= r;
      shift += min(l,r);

      if( x==0 || x==y || y==0 )
        return (x|y) << shift;

      int z = abs(x-y) >>> 1;
      x = min(x,y);
      y = z;
    }
  }

//  private static int gcd4( int x, int y )
//  {
//    int sx = x>>31; x = (x^sx) - sx; // <- x = abs(x)
//    int sy = y>>31; y = (y^sy) - sy; // <- y = abs(y)
//
//    for(;;) {
//      int l = numberOfTrailingZeros(x);
//      int r = numberOfTrailingZeros(y);
//      int lr = l-r,
//         slr = lr>>31;
//      x >>>=  lr & ~slr;
//      y >>>= -lr &  slr;
//
//      if( x==0 || x==y || y==0 )
//        return x|y;
//
//      // x,y = min(x,y), |x-y|/2
//      int xy =  x-y>>1, z = xy>>31;
//          xy = (xy^z) - z;
//      y^= (x^y) & z;
//      x = xy;
//    }
//  }

//  private static int gcd4( int x, int y )
//  {
//    x = abs(x);
//    y = abs(y);
//    for(;;) {
//      int l = Integer.numberOfTrailingZeros(x);
//      int r = Integer.numberOfTrailingZeros(y);
//      int lr = l-r,
//         slr = lr>>31;
//      x >>>=  lr & ~slr;
//      y >>>= -lr &  slr;
//
//      if( x==0 || x==y || y==0 )
//        return x|y;
//
//      // x,y = min(x,y), |x-y|/2
//      int z = abs(x-y) >>> 1;
//      x = min(x,y);
//      y = z;
//    }
//  }

  private static int gcd5( int x, int y )
  {
    x = abs(x);
    y = abs(y);
    int l = Integer.numberOfTrailingZeros(x); x >>>= l;
    int r = Integer.numberOfTrailingZeros(y); y >>>= r;
    int shift = min(l,r);
    for(;;) {
      if( x > y ) {
        int z = x; x = y; y = z;
      }
      if( x==0 || x==y )
        return (x|y) << shift;
      y -= x;
      y >>>= Integer.numberOfTrailingZeros(y);
    }
  }

  private static int gcd6( int x, int y )
  {
    x = abs(x);
    y = abs(y);
    int l = Integer.numberOfTrailingZeros(x); x >>>= l;
    int r = Integer.numberOfTrailingZeros(y); y >>>= r;
    int shift = min(l,r);
    if( x > y ) {
      int z = x; x = y; y = z;
    }
    while( x != 0 ) {
      // Interleave euclidean and binary GCD.
      // Ensures that y halves each iteration.
      int z = x > (y>>>1) ? y-x : y%x;
      y = x;
      x = z;
      // Invariant: x <= y && (y is odd)
      x >>>= Integer.numberOfTrailingZeros(x);
    }
    return y << shift;
  }

// FIELDS
  private final SplittableRandom rng = new SplittableRandom();
  private int x,y;

  @Setup(Level.Invocation)
  public void init() {
    x = rng.nextInt();
    y = rng.nextInt();
  }
// CONSTRUCTORS
// METHODS
  @Benchmark public void gcd0( Blackhole bh ) { bh.consume( gcd0(x,y) ); }
  @Benchmark public void gcd1( Blackhole bh ) { bh.consume( gcd1(x,y) ); }
  @Benchmark public void gcd2( Blackhole bh ) { bh.consume( gcd2(x,y) ); }
  @Benchmark public void gcd3( Blackhole bh ) { bh.consume( gcd3(x,y) ); }
  @Benchmark public void gcd4( Blackhole bh ) { bh.consume( gcd4(x,y) ); }
  @Benchmark public void gcd5( Blackhole bh ) { bh.consume( gcd5(x,y) ); }
  @Benchmark public void gcd6( Blackhole bh ) { bh.consume( gcd6(x,y) ); }
}
