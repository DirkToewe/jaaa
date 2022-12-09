package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;


public interface RandomAccessorArrInt extends RandomAccessor<int[]>
{
  @Override default int[] malloc( int len ) { return new int[len]; }
  @Override default void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( int[] a, int i, int[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
