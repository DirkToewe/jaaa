package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;


public interface RandomAccessorArrShort extends RandomAccessor<short[]>
{
  @Override default short[] malloc( int len ) { return new short[len]; }
  @Override default void      swap( short[] a, int i, short[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( short[] a, int i, short[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( short[] a, int i, short[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
