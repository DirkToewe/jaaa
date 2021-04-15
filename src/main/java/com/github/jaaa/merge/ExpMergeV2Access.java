package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;


public interface ExpMergeV2Access extends RotateAccess,
                                    ExpL2RSearchAccess,
                                    ExpR2LSearchAccess
{
  default void expMergeV2( int from, int mid, int until )
  {
    assert from <= mid;
    assert         mid <= until;

    int   STUCK_LIMIT = 7,
      stuckometer_l2r = 0,
      stuckometer_r2l = 0;

    while( from < mid && mid < until )
    {
      int nL =       mid-from,
          nR = until-mid;
      if( nL <= nR )
      { // BLOCK MERGE FORWARD
        stuckometer_r2l = 0;

        if( mid == (mid = expL2RSearchGapL(mid,until, from)) )
          stuckometer_l2r += 1;
        else {
          stuckometer_l2r = 0;
          rotate(from,mid, -nL);
        }

        from = 1-nL+mid;

        if( STUCK_LIMIT <= stuckometer_l2r ) {
          stuckometer_l2r = 0;
          from = expL2RSearchGapR(from,mid, mid);
          rotate(from,++mid, 1);
        }
      }
      else
      { // BLOCK MERGE BACKWARD
        stuckometer_l2r = 0;

        if( mid == (mid = expR2LSearchGapR(from,mid, until-1)) )
          stuckometer_r2l += 1;
        else {
          stuckometer_r2l = 0;
          rotate(mid,until, nR);
        }

        until = mid+nR-1;

        if( STUCK_LIMIT <= stuckometer_r2l ) {
          stuckometer_r2l = 0;
          until = expR2LSearchGapL(mid,until, --mid);
          rotate(mid,until, -1);
        }
      }
    }
  }
}
