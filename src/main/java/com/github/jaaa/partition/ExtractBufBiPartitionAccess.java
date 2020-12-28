package com.github.jaaa.partition;

import com.github.jaaa.PredicateSwapAccess;
import com.github.jaaa.misc.RotateAccess;

import static java.lang.Math.min;


public interface ExtractBufBiPartitionAccess extends RotateAccess,
                                              PredicateSwapAccess
{
  default int extractBufA( int from, int until, int nA )
  {
    int n = 0,
      pos = from;
    for( int i=from; i < until; i++ )
      if( ! predicate(i) )
      {
        rotate(pos,i,- n);
               pos=i - n;
        if( ++n > nA ) break;
      }
    rotate(from, pos+n, n);
    return n;
  }
  default int extractBufB( int from, int until, int nB )
  {
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
