package com.github.jaaa.select;

import com.github.jaaa.permute.RandomShuffleAccess;

import static com.github.jaaa.select.HeapSelect.performance_average;


public interface HeapSelectRandomAccess extends HeapSelectAccess, RandomShuffleAccess
{
  default void heapSelectRandom( int from, int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    int m =   mid - from,
        n = until - mid;
    if( 1 < n && performance_average(m+1,n-1) < performance_average(n,m) ) {
      if( from < mid && m < n )
        randomShuffle(from,until);
      heapSelectL(from,mid,until);
    }
    else {
      if( from < mid && mid < until-1 && m > n )
        randomShuffle(from,until);
      heapSelectR(from,mid,until);
    }
  }

  default void heapSelectRandomL( int from, int mid, int until ) {
    if( from < mid && mid < until-1 )
      randomShuffle(from,until);
    heapSelectL(from,mid,until);
  }

  default void heapSelectRandomR( int from, int mid, int until ) {
    if( from < mid && mid < until-1 )
      randomShuffle(from,until);
    heapSelectR(from,mid,until);
  }
}
