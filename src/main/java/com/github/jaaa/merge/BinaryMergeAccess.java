package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.BinarySearchAccess;

public interface BinaryMergeAccess extends RotateAccess,
                                            BinarySearchAccess
{
  default void binaryMerge( int from, int mid, int until )
  {
    assert from <= mid;
    assert         mid <= until;
    while( from <  mid && mid < until )
    {
      int nL =       mid-from,
          nR = until-mid;
      if( nL <= nR )
      { // BLOCK MERGE FORWARD
                    mid = binarySearchGap(mid,until, from, false);
        rotate(from,mid, -nL);
        from = 1-nL+mid;
      }
      else
      { // BLOCK MERGE BACKWARD
                mid = binarySearchGap(from,mid, until-1, true);
        rotate( mid, until, nR );
        until = mid+nR-1;
      }
    }
  }
}
