package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;


public interface RandomAccessorArrObj<T> extends RandomAccessor<T[]>
{
  @Override default void      swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( T[] a, int i, T[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
