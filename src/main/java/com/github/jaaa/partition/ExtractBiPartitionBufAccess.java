package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.misc.RotateAccess;


/** Used by in-place {A,B}-sort algorithms to create regions of A-elements
 *  on the left side and B-elements on the right side. Combined these regions
 *  can be used to store information, e.g. by swapping element pairs. The
 *  in-place algorithms commonly use these regions as buffers for other
 *  operations. As long as these regions are not longer than O(sqrt(n)),
 *  the cost of extracting these buffers is not higher than O(n).
 */
public interface ExtractBiPartitionBufAccess extends RotateAccess,
                                              PredicateSwapAccess
{
  default int extractBiPartitionBufA_L( int from, int until, int nA )
  {
    if( 0 == nA ) return 0;

    int n = 0,
      pos = from;

    for( int i=from; i < until; i++ )
      if( ! predicate(i) )
      {
        rotate(pos,i,- n);
               pos=i - n;
        if( nA <= ++n ) break;
      }

    rotate(from, pos+n, n);
    return n;
  }

  default int extractBiPartitionBufB_R( int from, int until, int nB )
  {
    if( 0 == nB ) return 0;

    int n = 0,
      pos = until;

    for( int i=until; i-- > from; )
      if( predicate(i) )
      {
        rotate(i+1, pos+n, n);
        pos=i;
        if( nB <= ++n ) break;
      }

    rotate(pos, until, -n);
    return n;
  }
}
