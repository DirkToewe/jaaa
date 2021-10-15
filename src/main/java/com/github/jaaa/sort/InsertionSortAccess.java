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
public interface InsertionSortAccess extends CompareSwapAccess
{
  default void insertionSort( int from, int until )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

//    for( int i=from; ++i < until; )
//      for( int j = binarySearchGapR(from,i,i), k = i; k > j; )
//        swap(k,--k);

    for( int i=from; ++i < until; )
    {
           int lo = from;
      for( int hi = i;; )
      {
        int               mid = lo+hi >>> 1;
        int c = compare(i,mid);
        if( c < 0 )  hi = mid;
        else         lo = mid+1;

        if( lo >= hi ) break;
      }

      for( int k=i; k > lo; )
        swap(k,--k);
    }
  }
}
