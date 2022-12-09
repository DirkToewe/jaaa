package com.github.jaaa.partition;

import com.github.jaaa.fn.PredicateSwapAccess;
import com.github.jaaa.util.Hex16;

// An in-place {A,B}-sort method for list that contain at most 16 B-elements.
// Conceived by Dirk Toewe.
//
// Overview
// --------
// The algorithm goes through the list from left to right collecting all
// B-elements along the way. The collected elements are rolled through the
// list. The order in which the collected and rolled elements were
// originally encountered in the list is tracked by a single in a list of
// up to 16 4-bit integers. Said list allows to finally stabilize the
// `n` B-elements in O(n) swaps. A list of 16*4bits fits exactly into a
// 64-bit long value which making it very efficient to operate on.
//
// Complexity
// ----------
// Comparisons: O(m+n)
//       Swaps: O(m+n)
//      Memory: O(1)
//
// Where m is number of A-elements n is the number B-elements.

public interface HexBiPartitionAccess extends PredicateSwapAccess
{
  default void hexBiPartition( int from, int until )
  {
    if( from > until   ) throw new IllegalArgumentException();
    if( from < until-16) throw new IllegalArgumentException();

    Hex16 order = new Hex16(); // stores up to 16 4-bit indices that keep track of permutation

    int pos = from; // <- position of the group of elements

    loop:for( int i=from;; i++ )
      if( i == until || predicate(i) )
      {
        int len = order.size();
        if( len != 0 )
        {
          // track rotations
          order.rotate( i-len - pos );

          // move up
          for( ; pos < i-len; pos++ )
            swap(pos,pos+len);
        }
        pos = i-len;

        if( i == until )
          break loop;

        order.append(len);
      }

    // un-kerfuffle the elements
    for( int i=0; i < order.size(); i++ )
      for(
        int  j = order.get(i);
        i != j;
      )
      {
        int k = order.get(j);
                order.set(j,j);
        swap(pos+i, pos+j);
        j = k;
      }
  }
}
