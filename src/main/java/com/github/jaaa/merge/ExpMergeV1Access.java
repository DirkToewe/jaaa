package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;


public interface ExpMergeV1Access extends RotateAccess,
                                    ExpL2RSearchAccess,
                                    ExpR2LSearchAccess
{
  default void expMergeV1( int from, int mid, int until )
  {
    assert from <= mid;
    assert         mid <= until;
    while( from <  mid && mid < until )
    {
      int nL =       mid-from,
          nR = until-mid;
      if( nL <= nR )
      { // BLOCK MERGE FORWARD
                    mid = expL2RSearchGapL(mid,until, from);
        rotate(from,mid, -nL);
        from = 1-nL+mid;
      }
      else
      { // BLOCK MERGE BACKWARD
                mid = expR2LSearchGapR(from,mid, until-1);
        rotate( mid, until, nR );
        until = mid+nR-1;
      }
    }
  }
}
