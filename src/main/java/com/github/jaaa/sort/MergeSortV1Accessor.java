package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;

public interface MergeSortV1Accessor<T> extends CompareRandomAccessor<T>
{
  public default void mergeSort( T a, int a0, T b, int b0, int len )
  {
    boolean isBufExternal = true;


    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
