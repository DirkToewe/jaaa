package com.github.jaaa.util;


import java.util.Arrays;
import java.util.SplittableRandom;
import java.util.function.IntUnaryOperator;

import static com.github.jaaa.permute.Swap.swap;


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
  public static double[][] lhs( int nSamples, double[] lower, double[] upper ) { return lhs(nSamples,lower,upper, new SplittableRandom()::nextInt); }
  public static double[][] lhs( int nSamples, double[] lower, double[] upper, IntUnaryOperator randInt )
  {
    if( lower.length != upper.length )
      throw new IllegalArgumentException();

    int nFeatures = lower.length;
    var result = lhs(nSamples,nFeatures,randInt);

    for( var row: result )
    for( int j=0; j < nFeatures; j++ )
    {
      double s = row[j];
      row[j] = lower[j]*(1-s) + s*upper[j];
    }

    return result;
  }
}
