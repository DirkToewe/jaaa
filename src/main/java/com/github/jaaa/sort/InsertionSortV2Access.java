package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

// Modified implementation of the stable binary insertion sort algorithm.
// The implementation only takes O(n) comparisons on already sorted input.
//
// Performance
// -----------
// Comparisons best : O(n)
// Comparisons worst: O(n*log(n))
//  Swaps best : O(n)
//  Swaps worst: O(n*n)
public interface InsertionSortV2Access extends CompareSwapAccess
{
  public default void insertionSortV2( int from, int until )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    for( int i=from; ++i < until; )
    {
           int lo = from;
      for( int hi = i-1,
         mid = hi;;
         mid = hi+lo >>> 1 )
      {
        int c = compare(i,mid);
        if( c < 0 )  hi = mid-1;
        else         lo = mid+1;

        if( lo > hi ) break;
      }

      for( int k=i; k > lo; )
        swap(k,--k);
    }
  }
}
