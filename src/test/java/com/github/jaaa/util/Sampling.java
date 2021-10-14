package com.github.jaaa.util;


import static com.github.jaaa.Swap.swap;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.SplittableRandom;
import java.util.function.IntUnaryOperator;


public class Sampling
{
  public static double[][] lhs( int nSamples, int nFeatures ) { return lhs(nSamples,nFeatures, new SplittableRandom()::nextInt); }
  public static double[][] lhs( int nSamples, int nFeatures, IntUnaryOperator randInt )
  {
    double[][] result = new double[nSamples][nFeatures];

    // INIT
    for( int i=1; i < nSamples; i++ )
      Arrays.fill(result[i], i / (nSamples-1d));

    // SHUFFLE
    for( int i=nSamples; i > 0; i-- )
    {
      var result_i = result[i-1];
      for( int j=0; j < nFeatures; j++ )
        swap( result_i,j, result[randInt.applyAsInt(i)],j );
    }

    return result;
  }

  public static PrimitiveIterator.OfDouble linSpace( double from, double to, int n )
  {
    if( n < 2 )
      throw new IllegalArgumentException();
    return new PrimitiveIterator.OfDouble()
    {
      private int i=0;
      @Override
      public boolean hasNext() { return i < n; }

      @Override
      public double nextDouble() {
        if( i >= n ) throw new NoSuchElementException();
        double s = i++ / (n-1d);
        return from*(1-s) + s*to;
      }
    };
  }
}
