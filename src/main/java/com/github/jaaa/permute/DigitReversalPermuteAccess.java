package com.github.jaaa.permute;

import static com.github.jaaa.util.IMath.isPowerOf;


public interface DigitReversalPermuteAccess extends SwapAccess
{
  private static int digitReverseIncrement( int radix, int reverseInt, int addedDigit ) {
    while( addedDigit != 0 ) {
      int x = reverseInt / addedDigit;
      if( x % radix != radix-1 ) break;
      reverseInt -= addedDigit * (radix-1);
      addedDigit /= radix;
    }
    return reverseInt + addedDigit;
  }

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
