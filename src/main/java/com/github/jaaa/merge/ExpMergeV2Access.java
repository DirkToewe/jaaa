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
    if( from >= 0 && mid >= from && mid <= until )
      loop: for( int l,r,s;; rotate(l,r,s) )
      {
        int stuckometer=7;
        s=mid;
        if( (mid<<1) - until <= from ) // <- if( (mid-from) <= (until-mid) )
          for(;;)
          { // MERGE FORWARD
            if( s == from ) return;
            if( s != (mid = expL2RSearchGap(mid,until, from++, /*rightBias=*/false)) ) {
              l = from-1;
              r = mid;
              from += s = mid-s;
              continue loop;
            }
            if( --stuckometer <= 0 )
              from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true); // <- mid++ guarantees that loop terminates next iteration
          }
        else
          for(;;)
          { // MERGE BACKWARD
            if( s == until ) return;
            if( s != (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
              l = mid;
              r = until+1;
              until += s = mid-s;
              continue loop;
            }
            if( --stuckometer <= 0 )
              until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false); // <- --mid guarantees that loop terminates next iteration
          }
      }
    throw new IllegalArgumentException();
  }
}
