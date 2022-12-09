package com.github.jaaa.permute;

import static com.github.jaaa.util.IMath.bitReverseIncrement;
import static com.github.jaaa.util.IMath.hasOneBitOrNone;


public interface BitReversalPermuteAccessor<A> extends SwapAccessor<A>
{
  default void bitReversalPermute( A arr, int from, int until ) {
    int len = until - from;
    if( from < 0 || from > until || ! hasOneBitOrNone(len) )
      throw new IllegalArgumentException();
    int bit = len >>> 1,
          i = 1,
          j = bit;
    while( i < len ) {
      if( i < j )
        swap(arr, from+i, arr, from+j);
      i++;
      j = bitReverseIncrement(j,bit);
    }
  }
}
