package com.github.jaaa.select;

import com.github.jaaa.permute.RandomShuffleAccess;

import static com.github.jaaa.select.HeapSelect.performance_average;
import static java.lang.Math.min;


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
        randomShuffle(from,until); // <- randomize to avoid worst case performance
      heapSelectL(from,mid,until);
    }
    else {
      if( from < mid && mid < until-1 && m > n )
        randomShuffle(from,until); // <- randomize to avoid worst case performance
      heapSelectR(from,mid,until);
    }
  }


  /**
   * Returns the estimated number of comparisons required by
   * `heapSelect` in order to select from a randomly shuffled
   * input of the given dimensions.
   *
   * @param from  Start (inclusive) of the selection range.
   * @param mid   Index of the selected element.
   * @param until End (exclusive) fo the selection range.
   * @return Expected number of comparisons required by
   *         `heapSelect(from,mid,until` given a randomly
   *         shuffled input.
   */
  default long heapSelectRandom_performance( int from, int mid, int until ) {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();
    if( mid == until )
      return 0;
    int m =   mid - from,
        n = until - mid;
    long p = performance_average(m+1,n-1);
    long q = performance_average(n,m);
    return min(p,q);
  }
}
