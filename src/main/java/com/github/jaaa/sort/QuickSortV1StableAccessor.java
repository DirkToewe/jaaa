package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;

import java.util.function.IntBinaryOperator;

public interface QuickSortV1StableAccessor<T> extends CompareRandomAccessor<T>, InsertionSortAccessor<T>
{
  IntBinaryOperator newRNG();

  default void quickSortStable( T arr, int arr0, int arr1, T buf, int buf0, int buf1 )
  {
//    if(  arr0 < 0 || arr0 > arr1
//      || buf0 < 0 || buf0 > buf1
//      || arr==buf &&  ) throw new IllegalArgumentException();

    int arrLen = arr1-arr0,
        bufLen = buf1-buf0;

    new Object() {
      private final IntBinaryOperator rng = newRNG();

      private void quickSort( int a0, int b0, int len )
      {
        throw new UnsupportedOperationException("Not yet implemented!");
      }

//      { quickSort(a0,b0,len); }
    };
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
