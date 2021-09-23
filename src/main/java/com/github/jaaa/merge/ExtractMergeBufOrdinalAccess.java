package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;


/** Offers utility methods to extract a given number of distinct elements from an
 *  unsorted range. Such distinct values are usually used as ordinal value buffer
 *  for in-place merging algorithms. One use of such buffer is for example a movement
 *  imitation buffer that keeps track of permutations of other elements in the range.
 */
public interface ExtractMergeBufOrdinalAccess extends RotateAccess,
                                                ExpL2RSearchAccess,
                                                ExpR2LSearchAccess
{
  default int extractMergeBuf_ordinal_l_min_unsorted( int from, int until, int desiredLen, int destination )
  {
    if( from > until) throw new IllegalArgumentException();
    if( from <   0  ) throw new IllegalArgumentException();
    if( destination < 0 ) throw new IllegalArgumentException();
    if( desiredLen <  0 ) throw new IllegalArgumentException();
    if( desiredLen == 0 || from==until ) return 0;

    int i=from,
        n=0;
    for(;;) {
      ++i;
      if( ++n >= desiredLen ) break;
      int j = expL2RSearchGap(i,until, i-1, true);
      if( j >= until ) break;
      while( i < j )
        swap(i-n, i++);
    }

    i -= n;
    for(; i   < destination; i++) swap(i,i+n);
    while(i-- > destination     ) swap(i,i+n);

    return n;
  }

  default int extractMergeBuf_ordinal_l_min_sorted( int from, int until, int desiredLen, int destination )
  {
    // TODO: something to ponder:
    //
    // As currently implemented, extracting m elements
    // from a sorted sequence of n element requires:
    //
    // comparisons: O( m*log(n) )
    //       swaps: O( m² + n )
    //
    // If instead we were to use
    // `extractMergeBuf_ordinal_minL_unsorted`
    // and then heap-sort afterwards:
    //
    // comparisons: O( m*log(m) + m*log(n) )
    //       swaps: O( m*log(m) + n )
    if( from > until) throw new IllegalArgumentException();
    if( from <   0  ) throw new IllegalArgumentException();
    if( destination < 0 ) throw new IllegalArgumentException();
    if( desiredLen <  0 ) throw new IllegalArgumentException();
    if( desiredLen == 0 || from==until ) return 0;

    int i=from,
        n=0;
    for(;;) {
          ++i;
      if( ++n >= desiredLen ) break;
      int j = expL2RSearchGap(i,until, i-1, true);
      if( j >= until ) break;
      rotate(i-n, i=j, -n);
    }

    int j = i-n;
    if( j < destination ) rotate(j, destination+n, -n);
    else                  rotate(destination,i, n);

    return n;
  }
}
