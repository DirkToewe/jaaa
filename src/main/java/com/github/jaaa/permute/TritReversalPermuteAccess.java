package com.github.jaaa.permute;

import static com.github.jaaa.permute.TritReversalPermute.tritReverseIncrement;
import static com.github.jaaa.util.IMath.isPowerOf3;


public interface TritReversalPermuteAccess extends SwapAccess
{
  default void tritReversalPermute( int from, int until ) {
    int len = until - from;
    if( from < 0 || from > until || ! isPowerOf3(len) )
      throw new IllegalArgumentException();
    int bit = len / 3,
          i = 1,
          j = bit;
    while( i < len ) {
      if( i < j )
        swap(from+i,from+j);
      i++;
      j = tritReverseIncrement(j,bit);
    }
  }
}
