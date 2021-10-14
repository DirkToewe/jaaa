package com.github.jaaa.merge;

import com.github.jaaa.CompareAccess;
import com.github.jaaa.misc.RotateAccess;


public interface TapeMergeAccess extends CompareAccess, RotateAccess
{
  default void tapeMergeV1( int from, int mid, int until )
  {
    if( from < 0   ) throw new IllegalArgumentException();
    if( from > mid ) throw new IllegalArgumentException();
    if(until < mid ) throw new IllegalArgumentException();

    int nL =   mid-from,
        nR = until-mid;

    for(;;)
      if( nL <= nR )
      { // FORWARD MERGE
        for(;;) if( from == mid ) return;
           else if( compare(from,mid) <= 0 ) from++;
           else break;
        nL = mid-from;
        do {} while( ++mid < until && compare(from,mid) > 0 );
        rotate(from,mid, -nL);
        from = mid - --nL;
        nR = until-mid;
      }
      else
      { // BACKWARD MERGE
        for(;;) if( mid == until ) return;
           else if( compare(mid-1,until-1) <= 0 ) until--;
           else break;
        nR = until-mid;
        do {} while( from < --mid && compare(mid-1,until-1) > 0 );
        rotate(mid,until, nR);
        until = mid + --nR;
        nL = mid-from;
      }

//    int nL =   mid-from,
//        nR = until-mid;
//
//    for( int rotL,rotR,rot;; rotate(rotL,rotR,rot) )
//      if( nL <= nR )
//      { // FORWARD MERGE
//        for(;;) if( from == mid ) return;
//        else if( compare(from,mid) <= 0 ) from++;
//        else break;
//        nL = mid-from;
//        while( ++mid < until && compare(from,mid) > 0 )
//          continue;
//        rotL = from;
//        rotR = mid;
//        rot = -nL;
////        rotate(from,mid, -nL);
//        from = mid - --nL;
//        nR = until-mid;
//      }
//      else
//      { // BACKWARD MERGE
//        for(;;) if( mid == until ) return;
//        else if( compare(mid-1,until-1) <= 0 ) until--;
//        else break;
//        nR = until-mid;
//        while( from < --mid && compare(mid-1,until-1) > 0 )
//          continue;
//        rotL = mid;
//        rotR = until;
//        rot  = nR;
////        rotate(mid,until, nR);
//        until = mid + --nR;
//        nL = mid-from;
//      }
  }
}
