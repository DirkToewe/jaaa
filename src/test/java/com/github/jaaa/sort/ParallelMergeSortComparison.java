package com.github.jaaa.sort;

import com.github.jaaa.sort.datagen.RandomSortDataGenerator;

import java.util.Arrays;
import java.util.Random;
import static java.util.Arrays.stream;

import static java.lang.System.nanoTime;

public class ParallelMergeSortComparison
{
  static {
    boolean ea = false;
    assert  ea = true;
       if( !ea ) throw new AssertionError();
  }

  public static void main( String... args ) throws InterruptedException
  {
                            int nSamples = 1_000_000;
       int[] x     = new    int[nSamples];
    double[] y_jdk = new double[nSamples],
             y_jaaa= new double[nSamples];

    double speedupCount = 0,
           speedupSum   = 0;

    var rng = new Random(1337);
    var gen = new RandomSortDataGenerator(rng);

    for( int i=0; i < nSamples; i++ )
    {
      int len = rng.nextInt(200_000_000) + 10_000_000;//64*1024*1024;

//      Integer[] input = rng.ints().limit(len).boxed().toArray(Integer[]::new);
      Integer[] input = stream( gen.nextMixed(len) ).boxed().toArray(Integer[]::new);
//      Integer[] input = range(0,len).map( j -> len-j ).boxed().toArray(Integer[]::new);

      long t0;
      var input_jdk  = input.clone(); System.gc(); Thread.sleep(10_000); t0 = nanoTime();          Arrays.parallelSort(input_jdk ); double dt_jdk = nanoTime() - t0;
//      var input_jaaa = input.clone(); System.gc(); Thread.sleep(10_000); t0 = nanoTime(); ParallelSkipMergeSortV5.sort(input_jaaa); double dt_jaaa= nanoTime() - t0;
      var input_jaaa = input.clone(); System.gc(); Thread.sleep(10_000); t0 = nanoTime(); ParallelRecMergeSort.sort(input_jaaa); double dt_jaaa= nanoTime() - t0;

      speedupSum  += dt_jdk / dt_jaaa;
      speedupCount++;

      assert Arrays.equals(input_jdk, input_jaaa);
      System.out.printf( "[len:%9d] jdk: %6.3f; jaaa: %6.3f; avg. speedup: %.3f\n", len, dt_jdk/1e9, dt_jaaa/1e9, speedupSum/speedupCount );
    }
  }
}
