package com.github.jaaa;

import static java.lang.System.arraycopy;


public interface RandomAccessorArrByte extends RandomAccessor<byte[]>
{
  @Override default byte[] malloc( int len ) { return new byte[len]; }
  @Override default void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
  @Override default void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
  @Override default void copyRange( byte[] a, int i, byte[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
}
