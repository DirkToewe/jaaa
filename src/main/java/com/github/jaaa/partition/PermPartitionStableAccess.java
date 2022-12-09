package com.github.jaaa.partition;

import com.github.jaaa.permute.BlockSwapAccess;
import com.github.jaaa.permute.RotateAccess;

import static java.util.stream.IntStream.range;

import static com.github.jaaa.util.IMath.log2Ceil;

// {A,B}-sort method by J. Ian Munro, Venkatesh Raman and Jeffrey S. Salowe
//
// Overview
// --------
// A stable variant of cycle sort (a.k.a. permutation sort) for arrays whose
// elements by their associate keys. The number of possible keys has to be
// finite and small.
//
// The algorithm builds on top of `PermSortStable` which is itself is a variation
// of unstable cycle sort (a.k.a. permuation sort). `PermSortStable` uses O(n)
// bits of additional memory to keep track of elements that have already been
// moved to their final position. `PermPartitionStable` additional uses O(k*n)
// additional bits to store for every log2(n)-th index and key `k`, how many
// elements with key `k` are left of said index. This means only O(log(n))
// additional comparisons are necessary to find the final position of an
// element.
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

public interface PermPartitionStableAccess extends PartitionAccess, RotateAccess, BlockSwapAccess
{
  default void permPartitionStable( int from, int until )
  {
    if( from >  until                   ) throw new IllegalArgumentException();
    if( from <  until-Integer.MAX_VALUE ) throw new IllegalArgumentException();
    if( from >  until-2                 ) return;

    int nKeys = nKeys();

    int   len = until-from,
          lg2 = log2Ceil(len),
     nSamples = (len-1+lg2) / lg2;

    // q[k][i] contains how many element of key `k` are left of
    // index i*lg2 originally (i.e. before permutation commences).
    int[][] q = new int[nKeys][nSamples];

    for( int i=from; i < until; i++ )
      ++q[key(i)][ (i-from) / lg2 ];

    // accumulate
    int qSum = from;
    for( int key=0; key < nKeys   ; key++ )
    for( int   i=0;   i < nSamples;   i++ ) {
      int qi = q[key][i];
               q[key][i] = qSum;
                           qSum += qi;
    }

    // M keeps track of elements that are already in their correct positions
    boolean[] M = new boolean[len];

    for( int k=from; k < until; k++ )
      if(  M[k-from] == false )
      {
        int i=k;
        do {
          int iKey = key(k),
              iQ   = (i-from) / lg2,
              iTo  = q[iKey][iQ]; // <- start with closest preceding element in q

          assert from + iQ*lg2 <= i;

          // FIND POSITION OF CURRENT ELEMENT AMONG EQUALS THAT ARE NOT YET IN POSITION
          int       pos = 0,
                     j0 = Math.max(k+1, from + iQ*lg2); // <- element k is the current and all elements left of k are already in position, so only count elements right of k.
          for( int j=j0; j < i; j++ )                   //    Also, we start with closest preceding element in q so start at counting at `from + iQ*lg2`.
            if( M[j-from] == false && key(j) == iKey )
              ++pos;

          // FIND FINAL POSITION OF CURRENT ELEMENT
          // --------------------------------------
          // we can do by starting from `iTo` and moving `pos` steps to
          // the right NOT COUNTING elements that are already in place
          for(;;) {
            if( M[iTo-from] == false && --pos < 0 )
              break;
            ++iTo;
          }

          assert ! M[iTo-from];

          swap(iTo,k);
             M[iTo-from] = true;
           i = iTo;
        }
        while( i != k );
      }

    assert range(0,len).mapToObj( i -> M[i] ).allMatch( x -> x );
  }
}
