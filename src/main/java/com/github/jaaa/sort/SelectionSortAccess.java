package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

public interface SelectionSortAccess extends CompareSwapAccess
{
  public default void selectionSort( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();

    for( int i=from; i < until-1; i++ )
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
}
