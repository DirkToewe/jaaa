package com.github.jaaa.permute;

import static com.github.jaaa.util.IMath.isPowerOf3;


public interface TritReversalPermuteAccessor<A> extends SwapAccessor<A>
{
  private static int tritReverseIncrement( int reverseInt, int addedTrit ) {
    while( addedTrit != 0 ) {
      int x = reverseInt / addedTrit;
      if( x % 3 != 2 ) break;
      reverseInt -= addedTrit<<1;
      addedTrit /= 3;
    }
    return reverseInt + addedTrit;
  }

  default void tritReversalPermute( A arr, int from, int until ) {
    int len = until - from;
    if( from < 0 || from > until || ! isPowerOf3(len) )
      throw new IllegalArgumentException();
    int bit = len / 3,
          i = 1,
          j = bit;
    while( i < len ) {
      if( i < j )
        swap(arr, from+i, arr, from+j);
      i++;
      j = tritReverseIncrement(j,bit);
    }
  }
}
