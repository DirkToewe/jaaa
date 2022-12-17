package com.github.jaaa.permute;

import static com.github.jaaa.util.IMath.isPowerOf;
import static com.github.jaaa.permute.DigitReversalPermute.digitReverseIncrement;


public interface DigitReversalPermuteAccess extends SwapAccess
{
  default void digitReversalPermute( int radix, int from, int until ) {
    int len = until - from;
    if( from < 0 || from > until || ! isPowerOf(radix,len) )
      throw new IllegalArgumentException();
    int bit = len / radix,
          i = 1,
          j = bit;
    while( i < len ) {
      if( i < j )
        swap(from+i,from+j);
      i++;
      j = digitReverseIncrement(radix,j,bit);
    }
  }
}
