package com.github.jaaa.util;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;


public class ProgressExperiments
{
  public static void main( String... args ) throws InterruptedException
  {
    int N = 100_000_000;

    for( int run=0; run++ < 10; )
    {
      List<Integer> list = IntStream.range(0,N).boxed().collect(toList());

      LongAdder counter = new LongAdder();
      long base_t0 = System.nanoTime();
      for( int i: list )
        counter.increment();
      long base_dt = System.nanoTime() - base_t0;

      if( counter.sum() != N ) throw new AssertionError();

      System.out.printf("%.2f ns/op\n", (double) base_dt / N);

      counter.reset();
      long t0 = System.nanoTime();
      for( int i: Progress.print(list) )
        counter.increment();
      long dt = System.nanoTime() - t0;

      if( counter.sum() != N ) throw new AssertionError();

      System.out.printf("List Overhead: %.2f ns/op\n", (double) (dt - base_dt) / N);
    }

    for( int run=0; run++ < 10; )
    {
      LongAdder counter = new LongAdder();

      long base_t0 = System.nanoTime();
      IntStream.range(0,N).boxed().parallel().forEach(
        i -> counter.increment()
      );
      long base_dt = System.nanoTime() - base_t0;

      if( counter.sum() != N ) throw new AssertionError();

      System.out.printf("%.2f ns/op\n", (double) base_dt / N);
      counter.reset();

      long t0 = System.nanoTime();
      Progress.print( IntStream.range(0,N).boxed() ).parallel().forEach(
        i -> counter.increment()
      );
      long dt = System.nanoTime() - t0;

      if( counter.sum() != N ) throw new AssertionError();

      System.out.printf("Stream Overhead: %.2f ns/op\n", (double) (dt - base_dt) / N);
    }
  }
}
