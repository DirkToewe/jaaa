package com.github.jaaa.copy;

import com.github.jaaa.permute.Swap;

import static java.lang.System.arraycopy;

public interface RandomAccessorArrChar extends RandomAccessor<char[]>
{
  @Override default char[] malloc( int len ) { return new char[len]; }
  @Override default void      swap( char[] a, int i, char[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( char[] a, int i, char[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( char[] a, int i, char[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
