package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

// A stable variant of the Cycle sort (Permutation sort) algorithm.
//
// Overview
// --------
// `PermSortStable` uses O(n) bits of additional memory to keep track of
// elements that have already been moved to their final position. This
// allows us to find the final position of an element in O(n) comparisons,
// by first counting the number of unmoved elements `m` left of it.
// Starting from the first index among equal elements, we then go `m` steps
// to the right, not counting (i.e. skipping) the elements that have
// already moved in place.
// Thus we have found the index of the element in the sorted list and can
// move it there continuing the sorting with the element that it replaces.
//
// Complexity
// ----------
// Comparisons: O( n*log(n) )
//       Swaps: O( n )
//      Memory: O( k*n/log(n) )  [i.e. O(n) bits]
//
// Where:
//   `n`: number of elements
//   `k`: number of keys
//
// REFERENCES
// ----------
// .. [1] "STABLE IN SITU SORTING AND MINIMUM DATA MOVEMENT"
//         J. IAN MUNRO, VENKATESH RAMAN and JEFFREY S. SALOWE
//

public interface PermStableSortAccess extends CompareSwapAccess
{
  default void permStableSort( int from, int until )
  {
    if( from >  until                   ) throw new IllegalArgumentException();
    if( from <  until-Integer.MAX_VALUE ) throw new IllegalArgumentException();
    if( from == until                   ) return;

    int                       len = until-from;
    boolean[] M = new boolean[len];

    for( int k=from; k < until; k++ )
      if(  M[k-from] == false )
      {
        int i=k;
        do {
          // FIND POSITION OF 1st AMONG EQUALS
          int first = from;
          for( int j=from; j < until; j++ )
            if( compare(j,k) < 0 )
              ++first;

          // FIND POSITION OF CURRENT ELEMENT AMONG EQUALS THAT ARE NOT YET IN POSITION
          int pos = 0;
          for( int j=k; ++j < i; )
            if( M[j-from] == false && compare(j,k) == 0 )
              ++pos;

          // FIND FINAL POSITION OF CURRENT ELEMENT
          // --------------------------------------
          // we can do by starting from `first` and moving `pos` steps to
          // the right NOT COUNTING elements that are already in place
          int iTo = first;
          for( ;; ++iTo )
            if( M[iTo-from] == false && --pos < 0 )
              break;

          assert ! M[iTo-from];

          swap(iTo,k);
             M[iTo-from] = true;
           i = iTo;
        }
        while( i != k );
      }
  }
}
