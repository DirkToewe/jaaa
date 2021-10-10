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
import static java.lang.Integer.numberOfTrailingZeros;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 8, time = 8/*sec*/)
@Measurement(iterations = 8, time = 8/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx16g"} )

@State(Scope.Benchmark)
public class IMathBenchmark_gcd_int
{
// STATIC FIELDS

// STATIC CONSTRUCTOR
  static {
    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new IllegalStateException();
  }

// STATIC METHODS
  public static void main( String... args ) throws RunnerException, IOException
  {
    var rng = new Random(1337);
    System.out.print("Running test 1...");

    for( int x=0; x < 1_000; x++ )
    for( int y=0; y < 1_000; y++ )
    {
      assert gcd1(x,y) == gcd2(x,y);
      assert gcd1(x,y) == gcd3(x,y);
      assert gcd1(x,y) == gcd4(x,y);
    }

    System.out.println(" passed!");

    System.out.print("Running test 2...");

    for( int run=0; run++ < 1_000_000; )
    {
      int x = rng.nextInt(),
          y = rng.nextInt();
      assert gcd1(x,y) == gcd2(x,y);
      assert gcd1(x,y) == gcd3(x,y);
      assert gcd1(x,y) == gcd4(x,y);
    }

    System.out.println(" passed!");

    var opt = new OptionsBuilder()
      .include( IMathBenchmark_gcd_int.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
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

//  private static int gcd4( int x, int y )
//  {
//    x = abs(x);
//    y = abs(y);
//    for( int shift=0;;) {
//      int l = Integer.numberOfTrailingZeros(x); x >>>= l;
//      int r = Integer.numberOfTrailingZeros(y); y >>>= r;
//      shift += min(l,r);
//
//      if( x==0 || x==y || y==0 )
//        return (x|y) << shift;
//
//      int z = abs(x-y) >>> 1;
//      x = min(x,y);
//      y = z;
//    }
//  }

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



  private static int gcd4( int x, int y )
  {
    x = abs(x);
    y = abs(y);
    for(;;) {
      int l = Integer.numberOfTrailingZeros(x);
      int r = Integer.numberOfTrailingZeros(y);
      int lr = l-r,
         slr = lr>>31;
      x >>>=  lr & ~slr;
      y >>>= -lr &  slr;

      if( x==0 || x==y || y==0 )
        return x|y;

      // x,y = min(x,y), |x-y|/2
      int z = abs(x-y) >>> 1;
      x = min(x,y);
      y = z;
    }
  }

// FIELDS
  private final Random rng = new Random();

// CONSTRUCTORS
// METHODS
//  @Benchmark
//  public void gcd1( Blackhole blackhole ) {
//    int x = rng.nextInt(),
//        y = rng.nextInt();
//    blackhole.consume( gcd1(x,y) );
//  }

//  @Benchmark
//  public void gcd2( Blackhole blackhole ) {
//    int x = rng.nextInt(),
//        y = rng.nextInt();
//    blackhole.consume( gcd2(x,y) );
//  }

//  @Benchmark
//  public void gcd3( Blackhole blackhole ) {
//    int x = rng.nextInt(),
//        y = rng.nextInt();
//    blackhole.consume( gcd3(x,y) );
//  }

  @Benchmark
  public void gcd4( Blackhole blackhole ) {
    int x = rng.nextInt(),
        y = rng.nextInt();
    blackhole.consume( gcd4(x,y) );
  }
}
