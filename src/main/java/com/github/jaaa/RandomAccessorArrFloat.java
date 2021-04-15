package com.github.jaaa;

import static java.lang.System.arraycopy;

public interface RandomAccessorArrFloat extends RandomAccessor<float[]>
{
  @Override default float[] malloc( int len ) { return new float[len]; }
  @Override default void      swap( float[] a, int i, float[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( float[] a, int i, float[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( float[] a, int i, float[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
