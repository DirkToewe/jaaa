package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;

public interface RandomAccessorArrLong extends RandomAccessor<long[]>
{
  @Override default long[] malloc( int len ) { return new long[len]; }
  @Override default void      swap( long[] a, int i, long[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( long[] a, int i, long[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( long[] a, int i, long[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
