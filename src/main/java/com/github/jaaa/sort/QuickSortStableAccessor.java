package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;

import java.util.function.IntBinaryOperator;

public interface QuickSortStableAccessor<T> extends CompareRandomAccessor<T>
{
  public IntBinaryOperator newRNG();

  public default void quickSortStable( T a, int a0, T b, int b0, int len )
  {
    new Object() {
      private final IntBinaryOperator rng = newRNG();
      private int nextInt( int from, int until ) { return rng.applyAsInt(from,until); }

      private void quickSort( int a0, int b0, int len )
      {
        throw new UnsupportedOperationException("Not yet implemented!");
      }

      { quickSort(a0,b0,len); }
    };
    throw new UnsupportedOperationException("Not yet implemented!");
  }
}
