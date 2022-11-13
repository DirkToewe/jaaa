package com.github.jaaa.select;

import com.github.jaaa.CompareSwapAccess;


public interface SelectionSelectAccess extends CompareSwapAccess
{
  default void selectionSelectL( int from, int mid, int until ) {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( mid < until )
      ++mid;
    for( int i=from; i < mid; i++ )
    {
      // find min. element in remaining unsorted sequence
      int j=i;
      for( int k=i; ++k < until; )
        if( compare(j,k) > 0 )
          j=k;
      // swap min. element to pos. i where it belongs
      swap(i,j);
    }
  }

  default void selectionSelectR( int from, int mid, int until ) {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    for( int i=until; i-- > from; )
    {
      // find min. element in remaining unsorted sequence
      int j=i;
      for( int k=from; k < i; k++ )
        if( compare(k,j) > 0 )
          j=k;
      // swap min. element to pos. i where it belongs
      swap(i,j);
    }
  }
}
