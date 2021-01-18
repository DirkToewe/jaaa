package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;

import static java.lang.Math.subtractExact;

// Modified implementation of the stable binary insertion sort algorithm.
// This never requires more than as many comparisons than swaps.
//
// Performance
// -----------
// Comparisons best : O(n)
// Comparisons worst: O(n*log(n))
//  Swaps best : O(n)
//  Swaps worst: O(n*n)
public interface InsertionExpSortAccess extends ExpR2LSearchAccess, CompareSwapAccess
{
  public default void insertionExpSort( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();
    for( int i=from; ++i < until; )
      for( int j = expR2LSearchGapR(from,i,i), k = i; k > j; )
        swap(k,--k);
  }
}
