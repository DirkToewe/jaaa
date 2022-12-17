package com.github.jaaa.util;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)

@Warmup     (iterations = 8, time = 8/*sec*/)
@Measurement(iterations = 8, time = 8/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx16g"} )

@State(Scope.Benchmark)
public class IMathBenchmark_ntz_int
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
    System.out.print("Running tests...");
    for( int x = Integer.MIN_VALUE;; x++ )
    {
      assert Integer.numberOfTrailingZeros(x) == ntz(x);
      if( x == Integer.MAX_VALUE )
        break;
    }
    System.out.println(" passed!");

    Options opt = new OptionsBuilder()
      .include( IMathBenchmark_ntz_int.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

//  private static int ntz( int x )
//  {
//    if( x==0 ) return 32;
//    int n=16; if( x != (x>>>n<<n) ) { n^=16; }
//        n|=8; if( x != (x>>>n<<n) ) { n^= 8; }
//        n|=4; if( x != (x>>>n<<n) ) { n^= 4; }
//        n|=2; if( x != (x>>>n<<n) ) { n^= 2; }
//        n|=1; if( x != (x>>>n<<n) ) { n^= 1; }
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;        int n = 0;
//    if( (x>>>16<<16) == x ) { x>>>=16; n|=16; }
//    if( (x>>> 8<< 8) == x ) { x>>>= 8; n|= 8; }
//    if( (x>>> 4<< 4) == x ) { x>>>= 4; n|= 4; }
//    if( (x>>> 2<< 2) == x ) { x>>>= 2; n|= 2; }
//    if( (x>>> 1<< 1) == x ) {          n|= 1; }
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    int n = 0;
//    int m = ~(-1<<16);
//            if((x & m) == 0 ) { x>>>=16; n|=16; }
//    m>>>=8; if((x & m) == 0 ) { x>>>= 8; n|= 8; }
//    m>>>=4; if((x & m) == 0 ) { x>>>= 4; n|= 4; }
//    m>>>=2; if((x & m) == 0 ) { x>>>= 2; n|= 2; }
//    m>>>=1; if((x & m) == 0 ) { x>>>= 1; n|= 1; }
//            if((x & m) == 0 ) {          n+= 1; }
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;                      int n = 0;
//    if((x & 0b1111_1111_1111_1111) == 0 ) { x>>>=16; n|=16; }
//    if((x & 0b0000_0000_1111_1111) == 0 ) { x>>>= 8; n|= 8; }
//    if((x & 0b0000_0000_0000_1111) == 0 ) { x>>>= 4; n|= 4; }
//    if((x & 0b0000_0000_0000_0011) == 0 ) { x>>>= 2; n|= 2; }
//    if((x & 0b0000_0000_0000_0001) == 0 ) {          n|= 1; }
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x==0 ) return 32;
//    int n=16; n ^= (x>>>n<<n)-x >>> 27 & 16;
//        n|=8; n ^= (x>>>n<<n)-x >>> 28 &  8;
//        n|=4; n ^= (x>>>n<<n)-x >>> 29 &  4;
//        n|=2; n ^= (x>>>n<<n)-x >>> 30 &  2;
//        n|=1; n ^= (x>>>n<<n)-x >>> 31;
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x==0 ) return 32;
//    int n=16; n ^= (x>>>n<<n)-x >>> 31 << 4;
//        n|=8; n ^= (x>>>n<<n)-x >>> 31 << 3;
//        n|=4; n ^= (x>>>n<<n)-x >>> 31 << 2;
//        n|=2; n ^= (x>>>n<<n)-x >>> 31 << 1;
//        n|=1; n ^= (x>>>n<<n)-x >>> 31;
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;        int n=0;
//    if( x == (x>>>16<<16) ) { x>>>=16; n=16; }
//    if( x == (x>>> 8<< 8) ) { x>>>= 8; n|=8; }
//    if( x == (x>>> 4<< 4) ) { x>>>= 4; n|=4; }
//    if( x == (x>>> 2<< 2) ) { x>>>= 2; n|=2; }
//    if( x == (x>>> 1<< 1) ) {          n|=1; }
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;                      int n = 0;
//    int s = ~((x & 0b11111111_11111111_00000000_00000000)-x >> 31) & 16; x>>>= s; n|= s;
//        s = ~((x & 0b11111111_11111111_11111111_00000000)-x >> 31) &  8; x>>>= s; n|= s;
//        s = ~((x & 0b11111111_11111111_11111111_11110000)-x >> 31) &  4; x>>>= s; n|= s;
//        s = ~((x & 0b11111111_11111111_11111111_11111100)-x >> 31) &  2; x>>>= s; n|= s;
//        s = ~((x & 0b11111111_11111111_11111111_11111110)-x >> 31) &  1;          n|= s;
//    return n;
//  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;                    int n = 0;
//    int s = ~((x>>>16<<16)-x >> 31) & 16; x>>>= s; n|= s;
//        s = ~((x>>> 8<< 8)-x >> 31) &  8; x>>>= s; n|= s;
//        s = ~((x>>> 4<< 4)-x >> 31) &  4; x>>>= s; n|= s;
//        s = ~((x>>> 2<< 2)-x >> 31) &  2; x>>>= s; n|= s;
//        s = ~((x>>> 1<< 1)-x >> 31) &  1;          n|= s;
//    return n;
//  }

  private static int ntz( int x )
  {
    if( x == 0 ) return 32;
    int n=16; n ^= (x>>>n<<n)-x >>> 31 << 4;
        n|=8; n ^= (x>>>n<<n)-x >>> 31 << 3;
        n|=4; n ^= (x>>>n<<n)-x >>> 31 << 2;
        n|=2; n ^= (x>>>n<<n)-x >>> 31 << 1;
        n|=1; n ^= (x>>>n<<n)-x >>> 31;
    return n;
  }

//  private static int ntz( int x )
//  {
//    if( x == 0 ) return 32;
//    int n = 31;
//    int m = -1<<16; if( x != (x&m) ) { m>>=16; n^=16; }
//        m = m << 8; if( x != (x&m) ) { m>>= 8; n^= 8; }
//        m = m << 4; if( x != (x&m) ) { m>>= 4; n^= 4; }
//        m = m << 2; if( x != (x&m) ) { m>>= 2; n^= 2; }
//        m = m << 1; if( x != (x&m) ) {         n^= 1; }
//    return n;
//  }

// FIELDS

// CONSTRUCTORS
// METHODS
  @Benchmark
  public void ntzJDK( Blackhole blackhole )
  {
//    int x = rng.nextInt();
//    blackhole.consume( Integer.numberOfTrailingZeros(x) );

    for( int x = Integer.MIN_VALUE;; x++ )
    {
      int y = Hashing.hash(x);
      blackhole.consume( Integer.numberOfTrailingZeros(y) );
      if( x == Integer.MAX_VALUE )
        break;
    }
  }

  @Benchmark
  public void ntz( Blackhole blackhole )
  {
//    int x = rng.nextInt();
//    blackhole.consume( ntz(x) );

    for( int x = Integer.MIN_VALUE;; x++ )
    {
      int y = Hashing.hash(x); // <- hash x to mix up branch prediction
      blackhole.consume( ntz(y) );
      if( x == Integer.MAX_VALUE )
        break;
    }
  }
}
