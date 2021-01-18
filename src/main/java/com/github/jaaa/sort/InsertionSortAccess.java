package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.search.BinarySearchAccess;

// Implementation of the stable binary insertion sort algorithm
//
// Performance
// -----------
// Comparisons : O(n*log(n))
//  Swaps best : O(n)
//  Swaps worst: O(n*n)
public interface InsertionSortAccess extends BinarySearchAccess, CompareSwapAccess
{
  default void insertionSort( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();

    for( int i=from; ++i < until; )
      for( int j = binarySearchGapR(from,i,i), k = i; k > j; )
        swap(k,--k);
  }
}
