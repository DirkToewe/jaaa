package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;


public interface InsertionSortAccessor<T> extends CompareRandomAccessor<T>
{
  default void insertionSort( T arr, int from, int until )
  {
    if( 0 > from || from > until )
      throw new IllegalArgumentException();

//    for( int i=from; ++i < until; )
//      for( int j = binarySearchGapR(arr,from,i, arr,i), k = i; k > j; )
//        swap(arr,k, arr,--k);

    for( int i=from; ++i < until; )
    {
           int lo = from;
      for( int hi = i;; )
      {
        int                        mid = lo+hi >>> 1;
        int c = compare(arr,i, arr,mid);
        if( c < 0 )           hi = mid;
        else                  lo = mid+1;

        if( lo >= hi ) break;
      }

      for( int k=i; k > lo; )
        swap(arr,k, arr,--k);
    }
  }
}
