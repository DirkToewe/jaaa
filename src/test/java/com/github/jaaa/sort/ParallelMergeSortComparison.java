package com.github.jaaa.sort;

import com.github.jaaa.sort.datagen.RandomSortDataGenerator;

import java.util.Arrays;
import java.util.SplittableRandom;

import static com.github.jaaa.misc.Boxing.boxed;
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
    int                         nSamples = 1_000_000,
             x[]   = new    int[nSamples];
    double[] y_jdk = new double[nSamples],
             y_jaaa= new double[nSamples];

    double sampleCount = 0;

    var rng = new SplittableRandom();
    var gen = new RandomSortDataGenerator(rng);

    boolean[] order = {false, true};

    double DT_jaaa = 0,
           DT_jdk  = 0;

    for( int i=0; i < nSamples; i++ )
    {
      int len = rng.nextInt(10_000_000);

//      var       input = gen.nextUniform(len);
      var       input = gen.nextMixed(len);
      Integer[] input_jaaa = null,
                input_jdk  = null;

      long t0;

      double dt_jaaa = Double.NaN,
             dt_jdk  = Double.NaN;

      if( rng.nextBoolean() ) {
        order[0] = !order[0];
        order[1] = !order[1];
      }
      assert order[0] != order[1];
      assert order.length == 2;

      for( var toggle: order )
        if(toggle) { input_jdk  = boxed(input); t0 = nanoTime();          Arrays.parallelSort(input_jdk ); dt_jdk = nanoTime() - t0; }
//        else       { input_jaaa = boxed(input); t0 = nanoTime();  ParallelRebelMergeSort.sort(input_jaaa); dt_jaaa= nanoTime() - t0; } // ~6% speedup
        else       { input_jaaa = boxed(input); t0 = nanoTime();    ParallelRecMergeSort.sort(input_jaaa); dt_jaaa= nanoTime() - t0; } // ~4% speedup
//        else       { input_jaaa = boxed(input); t0 = nanoTime();   ParallelSkipMergeSort.sort(input_jaaa); dt_jaaa= nanoTime() - t0; } // ~7% speedup
//        else       { input_jaaa = boxed(input); t0 = nanoTime();    ParallelZenMergeSort.sort(input_jaaa); dt_jaaa= nanoTime() - t0; } // ~4% speedup

      DT_jdk  += dt_jdk;
      DT_jaaa += dt_jaaa;
      sampleCount++;

      assert null != input_jaaa;
      assert null != input_jdk;
      assert Arrays.equals(input_jdk, input_jaaa);
      System.out.printf( "[len:%9d] jdk: %6.3f; jaaa: %6.3f; avg. speedup: %.3f\n", len, dt_jdk/1e9, dt_jaaa/1e9, DT_jdk/DT_jaaa );
    }
  }
}
