package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.misc.BlockSwapAccess;
import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.util.Hex16;
import com.github.jaaa.util.IntBiConsumer;
import com.github.jaaa.util.IntBiFunction;

import static java.lang.Math.subtractExact;

// An in-place {A,B}-sort method for list that contain at most 16 B-elements.
// Conceived by Dirk Toewe.
//
// Overview
// --------
// Applies `HexBiPartition` recursively to collect increasingly larger blocks
// of A- or B-elements (whichever there are less of), until all elements are
// fully bi-partitioned.
//
// Complexity
// ----------
// Comparisons: O( m+n )
//       Swaps: O( (m+n)*log(min{m,n}) )
//      Memory: O(1)
//
// Where m is number of A-elements n is the number B-elements.

public interface HexRecBiPartitionV2Access extends PredicateSwapAccess, BlockSwapAccess, RotateAccess
{
  public boolean predicate( int i );

  public default void hexRecBiPartitionV2( int from, int until )
  {
    if( from >  until ) throw new IllegalArgumentException();
    if( from == until ) return;

    int len = subtractExact(until,from);
    IntBiFunction<IntBiConsumer> swapFn = (pos,n) -> (i,j) -> blockSwap(pos+n*i, pos+n*j, n);
    Hex16 order = new Hex16(); // stores up to 16 4-bit indices that keep track of permutations

    outer_loop:for( int n=1;; n*=16 )
    {
      int pos = from, // <- position of collected blocks
        first = -1;
      for( int off=from; off <= until-n; off += n )
      {
        if( predicate(off) )
        {
          int s = order.size()*n;

          // DUMP COLLECTED BLOCKS IF FULL
          if( order.isFull() )
          {
            // find place for collected blocks
            int target = off-s;
            if( first < 0 )
              first  =  target-from;
            else
              target -= (target-from-first)%s;

            // roll collected blocks into place
            order.rotate((target - pos) / n);
            for( ; pos <  target;  pos += n) blockSwap(pos,    pos+s, n);
            for( ; pos >  target;          ) blockSwap(pos-=n, pos+s, n);
            assert pos == target;

            // un-permute rcollected blocks
            order.sortAndClear( swapFn.apply(pos,n) );
          }
          else if( s > 0 )
          { // move up collected blocks
            assert   0 == (off-s - pos) % n;
            order.rotate( (off-s - pos) / n );
            for( ; pos  <  off-s;  pos+=n ) blockSwap(pos, pos+s, n);
          }

          pos  =  off - order.size()*n; assert pos >= from;
          order.append( order.size() );
        }
      }

      assert pos + order.size()*n <= until;

      // move up remaining collected blocks
      int              gap = until - pos - order.size()*n;
      rotate(pos,until,gap);
      until  =  pos += gap;

      // un-permute remaining collected blocks
      order.sortAndClear( swapFn.apply(pos,n) );

      if( first < 0 )
        break outer_loop;
      from += first;

      assert n < len;
    }
  }
}
