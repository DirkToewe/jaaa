package com.github.jaaa.partition;

import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.misc.RotateAccess;

import java.util.function.IntBinaryOperator;

import static java.lang.Math.subtractExact;

// A recursive stable in-place {A,B}-sort method conceived by Dirk Toewe.
//
// Overview
// --------
//
// Complexity
// ----------
// Comparisons: O(m+n)
//       Swaps: ?
//      Memory: O( log(m+n) )
//
// Where `m` is the number of A-elements and `n` is number of B-elements.

public interface RecBiPartitionAccess extends RotateAccess, BlockSwapAccess
{
  public boolean predicate( int i );

  public default void recBiPartition( int from, int until )
  {
    if( from >  until ) throw new IllegalArgumentException();
    if( from == until ) return;

    new IntBinaryOperator()
    {
      /** Collects up to n B-elements in the index range of [from,until)
       *  into one consecutive block and returns the first index in said
       *  box.
       *
       * @param from Where to start searching collecting B-elements.
       * @param n    Maximum number of B-elements to be collected.
       * @return     Starting position `pos of the block of collected elements,
       *             i.e. the collected elements are in the range [from, min{from+n,until})
       */
      @Override public int applyAsInt( int from, int n )
      {
        assert n > 0;
        assert from < until;
        if( n <= 1 ) {
          for( int i=from; i < until; i++ )
            if( predicate(i) )
              return i;
          return until;
        }
        int                     nL = n >>> 1,
            l = applyAsInt(from,nL); // <- collect half of the elements
        if( l < until-nL ) // <- if there is potentially more than nL B-elements
        {
          int                   nR = n - nL,
            r = applyAsInt(l+nL,nR); // <- collect other half
          if( l < r-nL ) { rotate(l,r, -nL);
              l = r-nL;
          }
        }
        return l;
      }
    }.applyAsInt(from, subtractExact(until,from));
  }
}
