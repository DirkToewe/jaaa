package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.search.BinarySearchAccessor;


public interface InsertionSortAccessor<T> extends CompareRandomAccessor<T>,
                                                   BinarySearchAccessor<T>
{
  default void insertionSort( T arr, int from, int until )
  {
    if( from >  until ) throw new IllegalArgumentException();
    if( from == until ) return;

    for( int i=from; ++i < until; )
      for( int j = binarySearchGapR(arr,from,i, arr,i), k = i; k > j; )
        swap(arr,k, arr,--k);
  }
}
