package com.github.jaaa.merge;

import com.github.jaaa.permute.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;

public interface ExpMergeV4Access extends RotateAccess,
                                    ExpL2RSearchAccess,
                                    ExpR2LSearchAccess
{
  default void expMergeV4( int from, int mid, int until )
  {
    if( from >= 0 && mid >= from && mid <= until )
      for( int l,r,s;; rotate(l,r,s) )
      {
        s=mid;
        if( (mid<<1) - until <= from ) // <- if( (mid-from) <= (until-mid) )
        { // MERGE FORWARD
          from = expL2RSearchGap(from,mid,   mid++, /*rightBias=*/true ); if( s == from ) return;
          mid  = expL2RSearchGap(mid,until, from++, /*rightBias=*/false);
          l = from-1;
          r = mid;
          from += s = mid-s;
        }
        else
        { // MERGE BACKWARD
          until = expR2LSearchGap(s,until,  --mid,   /*rightBias=*/false); if( s == until ) return;
          mid   = expR2LSearchGap(from,mid, --until, /*rightBias=*/true );
          l = mid;
          r = until+1;
          until += s = mid-s;
        }
      }
    throw new IllegalArgumentException();
  }
}
