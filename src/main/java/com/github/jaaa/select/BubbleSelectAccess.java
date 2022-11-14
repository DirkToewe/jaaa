package com.github.jaaa.select;


import com.github.jaaa.CompareSwapAccess;

public interface BubbleSelectAccess extends CompareSwapAccess
{
  default void bubbleSelect( int from, int mid, int until ) {
    if( mid-from <= until-mid-2 )
      bubbleSelectL(from,mid,until);
    else
      bubbleSelectR(from,mid,until);
  }

  default void bubbleSelectL( int from, int mid, int until )
  {
    if( from < 0 || from > mid | mid > until ) throw new IllegalArgumentException();
    if( mid < until )
      ++mid;
    for( int i=from; i < mid; i++ )
    {
      int j=i;
      for( int k=j; ++k < until; )
        if( compare(j,k) > 0 )
          j=k;
      swap(i,j);
    }
  }

  default void bubbleSelectR( int from, int mid, int until )
  {
    if( from < 0 || from > mid | mid > until ) throw new IllegalArgumentException();
    for( int i=until; i-- > mid; )
    {
      int j=i;
      for( int k=j; k-- > from; )
        if( compare(j,k) < 0 )
          j=k;
      swap(i,j);
    }
  }
}
