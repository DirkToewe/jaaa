package com.github.jaaa.permute;

import static java.lang.Integer.highestOneBit;
import static java.lang.Integer.lowestOneBit;


public interface InShuffleAccess extends BitReversalPermuteAccess, RevertAccess
{
  default void inShuffle( int from, int until ) {
    if( 0 > from || from > until )
      throw new IllegalArgumentException();
    // SAME AS inShuffle BUT IN REVERSE ORDER
    int bits = until - from >>> 1, off = 0, // <- number of collected even elements
         rem = until - from + 1 >>> 1;
    while( bits != 0 ) {
      int     len = highestOneBit(bits);
      rem  -= len;
      bits ^= len;

      int a = off + from,
          b = len + a;

      off |= len << 1;

      int c = off + from,
          d = rem + c;

      revert(b,d);
      revert(c,d);
      revert(b,c);
      bitReversalPermute(b,c);
      bitReversalPermute(a,b);
      bitReversalPermute(a,c);
    }
  }

  /**
   * In-place permutation that moves the evenly-indexed elements
   * (starting with index from+0 considered as even) to left half
   * of the array and oddly-indexed elements to the right half
   * of the array in a stable fashion.
   *
   * @param from  Inclusive start of the partitioned range.
   * @param until Exclusive end of the partitioned range.
   * @tparam T Type of partitioned elements.
   */
  default void unShuffle( int from, int until ) {
    if( 0 > from || from > until )
      throw new IllegalArgumentException();
    int off = until - from,
        rem = off & 1; // <- number of collected even elements
    off &= ~1;
    while( off != 0 ) {
      // determined next power-of-2 block
      int c = off + from,
        len = lowestOneBit(off);
      off ^= len;
      len >>>= 1;
      // use bit-reversal permutation to partition power-of-2 block.
      int a = off + from,
          b = len + a;
      bitReversalPermute(a,c);
      bitReversalPermute(a,b);
      bitReversalPermute(b,c);
      // rotate collected even elements over to the left
      int d = rem + c;
      revert(b,c);
      revert(c,d);
      revert(b,d);
      // count additional collected even elements
      rem += len;
    }
  }
}
