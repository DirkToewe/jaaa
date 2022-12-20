package com.github.jaaa.sort;

import com.github.jaaa.permute.RotateAccess;
import com.github.jaaa.search.AkimboSearchAccess;


public interface ExtractSortBufOrdinalAccess extends AkimboSearchAccess, RotateAccess
{
  /** Attempts to extract the desired number of unique elements from a given range
    * and moves them to a specified destination as a sorted block. If there are
    * less than the desired number of elements available, it extracts as many as
    * possible. The method returns the actual number of extracted unique elements.
    * The extraction is stable in the sense that amongst duplicate elements, the
    * leftmost element (i.e. with the lowest index) is always extracted.
    *
    * @param from Start index (inclusive) of the range to be extracted from.
    * @param until End index (exclusive) of the range to be extracted from.
    * @param desiredLen The number of unique elements that are to be extracted.
    * @param destination The start index to which the extracted elements are moved to as a block.
    * @return The actual number of unique elements that could be extracted.
    */
  default int extractSortBuf_ordinal_l_sorted( int from, int until, int desiredLen, int destination )
  {
    if( from > until) throw new IllegalArgumentException();
    if( from <   0  ) throw new IllegalArgumentException();
    if( destination < 0 ) throw new IllegalArgumentException();
    if( desiredLen <  0 ) throw new IllegalArgumentException();
    if( desiredLen == 0 || from==until ) return 0;

    int len = 1,
        pos = from;

    for( int i=from; len < desiredLen && ++i < until; )
    {
      int j = ~akimboSearch(pos,pos+len, i);
      if( j >= 0 ) {
        // Two birds with one stone:
        //   * rotate forward block of collected unique elements
        //   * insert new unique element at correct index
        //
        //    |  uniqL |  uniqR |  gap | i |
        // => |  uniqL | ~gap | ~uniqR | i |
        // => |  gap | ~uniqL | ~uniqR | i |
        // => |  gap |  uniqL | ~uniqR | i |
        // => |  gap |  uniqL | i |  uniqR |
        revert(j,i);int n = len - (j-pos);
        revert(pos,   i-n);
        revert(i-len, i-n);
        revert(       i-n, i+1);
        pos = i - len++;
      }
    }

    if( pos < destination ) rotate(pos,destination+len, -len);
    else                    rotate(destination,pos+len, +len);

    return len;
  }
}
