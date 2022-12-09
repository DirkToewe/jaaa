package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;

public interface RandomAccessorArrDouble extends RandomAccessor<double[]>
{
  @Override default double[] malloc( int len ) { return new double[len]; }
  @Override default void      swap( double[] a, int i, double[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( double[] a, int i, double[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( double[] a, int i, double[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
