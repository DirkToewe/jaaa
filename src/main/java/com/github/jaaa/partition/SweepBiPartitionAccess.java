package com.github.jaaa.partition;

// A fairly simple, straight-forward in-place {A,B}-sort implementation.
//
// Overview
// --------
// The algorithm traverses the list collecting all B-elements along the
// way. The elements are collected and moved forward in-order.
//
// Complexity
// ----------
// Comparisons: O( m+n  )
//       Swaps: O( m+n² )
//      Memory: O(1)
//
// Where `m` is the number of A-elements and `n` is number of B-elements.

import com.github.jaaa.misc.RotateAccess;

public interface SweepBiPartitionAccess extends RotateAccess
{
  public boolean predicate( int i );

  public default void sweepBiPartition( int from, int until )
  {
    if( from > until ) throw new IllegalArgumentException();

    int len = 0,    // <- number of collected elements
        pos = from; // <- position of collected elements

    loop:for( int i=from;; i++ )
      if( i == until || predicate(i) )
      {
        // move up collected elements
        if( 0 < len )
          rotate(pos,i,-len);

        if( i == until )
          break loop;

        if( len == Integer.MAX_VALUE )
          throw new ArithmeticException("Integer overflow.");
        pos = i - len++;
      }
  }
}
