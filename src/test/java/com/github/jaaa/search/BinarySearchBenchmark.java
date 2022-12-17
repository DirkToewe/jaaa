package com.github.jaaa.search;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)

@Warmup     (iterations = 8, time = 16/*sec*/)
@Measurement(iterations = 8, time = 16/*sec*/)
@Fork( value=4, jvmArgsAppend={"-ea", "-XX:MaxInlineLevel=15", "-Xmx4g"} )

@State(Scope.Benchmark)
public class BinarySearchBenchmark
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
    Random rng = new Random();

    System.out.print("Running test1...");
    Integer[] arr = rng.ints(1_000_000).sorted().boxed().toArray(Integer[]::new);
    for( int run=0; run++ < 1_000_000; )
    {
      Integer key = arr[ rng.nextInt(arr.length) ];
      assert  key.equals( arr[          binSearch1(arr,key) ] );
      assert  key.equals( arr[ Arrays.binarySearch(arr,key) ] );
    }
    System.out.println(" passed!");

    Options opt = new OptionsBuilder()
      .include( BinarySearchBenchmark.class.getCanonicalName() )
      .build();

    Collection<RunResult> results = new Runner(opt).run();
  }

  private static <T extends Comparable<? super T>> int binSearch1( T[] arr, T key )
  {
         int lo=0;
    for( int hi=arr.length; lo < hi; ) {
      int                       mid = lo+hi >>> 1,
          c = key.compareTo(arr[mid]);
      if( c < 0 )          hi = mid;   else
      if( c > 0 )          lo = mid+1; else
                         return mid;
    }
    return ~lo;
  }

//  private static <T extends Comparable<? super T>> int binSearch1( T[] arr, T key )
//  {
//         int lo=0;
//    for( int hi=arr.length; lo < hi; ) {
//      int                     mid = lo+hi >>> 1,
//        c = key.compareTo(arr[mid]);
//        c = Integer.signum(c);
//      switch(c) {
//        case -1:   hi = mid;   continue;
//        case  0: return mid;
//        case +1:   lo = mid+1; continue;
//      }
//    }
//    return ~lo;
//  }

//  private static <T extends Comparable<? super T>> int binSearch1( T[] arr, T key )
//  {
//    int[] range = {0,arr.length};
//    while( range[0] < range[1] ) { int mid = range[0]+range[1] >>> 1;
//      int c = key.compareTo(arr[mid]);
//      if( c == 0 )       return mid;
//      c >>>= 31;
//      range[c] = mid + (c^1);
//    }
//    return ~range[0];
//  }

//  private static <T extends Comparable<? super T>> int binSearch1( T[] arr, T key )
//  {
//    int lo=0,
//        hi=arr.length;
//    while( lo < hi ) {      int mid = lo+hi >>> 1;
//      int c = key.compareTo(arr[mid]);
//      if( c == 0 )       return mid;
//          c >>= 31;
//      lo =  c & lo |~c & mid+1;
//      hi = ~c & hi | c & mid;
//    }
//    return ~lo;
//  }

// FIELDS
  private final Random rng = new Random();
  private final Integer[] arr = new Integer[(1<<16) - 1];

// CONSTRUCTORS
// METHODS
  @Setup(Level.Iteration)
  public void setup() {
    Arrays.setAll(arr, i -> rng.nextInt());
    Arrays.sort(arr);
  }

  @Benchmark
  public void binSearch1( Blackhole blackhole ) {
    Integer key = arr[ rng.nextInt(arr.length) ];
    blackhole.consume( binSearch1(arr,key) );
  }

  @Benchmark
  public void binSearchJDK( Blackhole blackhole ) {
    Integer key = arr[ rng.nextInt(arr.length) ];
    blackhole.consume( Arrays.binarySearch(arr,key) );
  }
}
