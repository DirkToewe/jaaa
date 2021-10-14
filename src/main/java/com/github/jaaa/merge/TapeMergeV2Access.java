package com.github.jaaa.merge;

import com.github.jaaa.CompareAccess;
import com.github.jaaa.misc.RotateAccess;


public interface TapeMergeV2Access extends CompareAccess, RotateAccess
{
  default void tapeMergeV2( int from, int mid, int until )
  {
    if( from < 0   ) throw new IllegalArgumentException();
    if( from > mid ) throw new IllegalArgumentException();
    if(until < mid ) throw new IllegalArgumentException();

    for(;;)
    {
      int m = mid;
      if( mid-from <= until-mid )
      { // FORWARD MERGE
        for(;;) if( mid == from ) return;
           else if( compare(mid,from) >= 0 ) from++;
           else break;
        do {} while( until > ++mid && compare(mid,from) < 0 );
        int             n = mid-m;
        rotate(from,mid,n++);
        from += n;
      }
      else
      { // BACKWARD MERGE
        for(;;) if( mid == until ) return;
           else if( compare(mid-1,until-1) <= 0 ) until--;
           else break;
        do {} while( from < --mid && compare(mid-1,until-1) > 0 );
        int              n = mid-m;
        rotate(mid,until,n--);
        until += n;
      }
    }

//    int nL =   mid-from,
//        nR = until-mid;
//
//    for(;;)
//      if( nL <= nR )
//      { // FORWARD MERGE
//        if( nL==0 ) return;
//        while( mid < until && compare(from,mid) > 0 )
//             ++mid;
//        rotate(from,mid,-nL);
//        from = mid - --nL;
//        nR = until-mid;
//      }
//      else
//      { // BACKWARD MERGE
//        if( nR==0 ) return;
//        while( mid > from && compare(mid-1,until-1) > 0 )
//             --mid;
//        rotate(mid,until,nR);
//        until= mid + --nR;
//        nL = mid-from;
//      }
  }
}
