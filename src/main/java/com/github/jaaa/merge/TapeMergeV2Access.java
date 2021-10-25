package com.github.jaaa.merge;

import com.github.jaaa.CompareAccess;
import com.github.jaaa.misc.RotateAccess;


public interface TapeMergeV2Access extends CompareAccess, RotateAccess
{
//  default void tapeMergeV2( int from, int mid, int until )
//  {
//    if( from < 0 || from > mid || until < mid )
//      throw new IllegalArgumentException();
//
//    for( int l=0,m,r=0;; rotate(l,r,m) )
//    {
//      m = mid;
//      int delta = (mid<<1) - until - from;
//      switch( delta>>>31 )
//      {
//        case 0 -> {
//          // BACKWARD MERGE
//          for(;;) if( mid == until ) return;
//             else if( compare(mid-1,until-1) <= 0 ) until--;
//             else break;
//          do {} while( from < --mid && compare(mid-1,until-1) > 0 );
//          l = mid;
//          m = mid - m;
//          r = until;
//          until += m-1;
//        }
//        case 1 -> {
//          // FORWARD MERGE
//          for(;;) if( from == mid ) return;
//             else if( compare(from,mid) <= 0 ) from++;
//             else break;
//          do {} while( ++mid < until && compare(from,mid) > 0 );
//          l = from;
//          m = mid - m;
//          r = mid;
//          from += m+1;
//        }
//      }
//    }
//  }

  default void tapeMergeV2( int from, int mid, int until )
  {
    if( until >= mid && mid >= from && from >= 0 )
      for( int l,r,s;; rotate(l,r,s) )
      {
        s = mid;
//        int delta = (mid<<1) - until - from; // = (mid-from) - (until-mid)
//        if( delta <= 0 )
        if( (mid<<1) - until <= from )
        { // FORWARD MERGE
          for(;;) if( from == mid ) return;
             else if( compare(from,mid) <= 0 ) from++;
             else break;
          do {} while( ++mid < until && compare(from,mid) > 0 );
          r = mid;
          s = mid - s;
          l = from;
          from += s+1;
        }
        else
        { // BACKWARD MERGE
          for(;;) if( mid == until ) return;
             else if( compare(mid-1,--until) > 0 ) break;
          do {} while( from < --mid && compare(mid-1,until) > 0 );
          l = mid;
          s = mid - s;
          r = until+1;
          until += s;
        }
      }

    throw new IllegalArgumentException();
  }

//  default void tapeMergeV2( int from, int mid, int until )
//  {
//    if( from < 0 || from > mid || until < mid )
//      throw new IllegalArgumentException();
//
//    int nL =   mid-from,
//        nR = until-mid;
//
//    for(;;)
//      if( nL <= nR )
//      { // FORWARD MERGE
//        for(;;)
//          if( from < mid )
//            if( compare(from,mid) <= 0 ) from++;
//            else break;
//          else return;
//        nL = mid-from;
//        do {} while( ++mid < until && compare(from,mid) > 0 );
//        rotate(from,mid, -nL);
//        from = mid - --nL;
//        nR = until-mid;
//      }
//      else
//      { // BACKWARD MERGE
//        for(;;)
//          if( mid < until )
//            if( compare(mid-1,until-1) <= 0 ) until--;
//            else break;
//          else return;
//        nR = until-mid;
//        do {} while( from < --mid && compare(mid-1,until-1) > 0 );
//        rotate(mid,until, nR);
//        until = mid + --nR;
//        nL = mid-from;
//      }
//  }

//  default void tapeMergeV2( int from, int mid, int until )
//  {
//    if( from < 0 || from > mid || until < mid )
//      throw new IllegalArgumentException();
//
//    int nL =   mid-from,
//        nR = until-mid;
//
//    for( int l,r,s;; rotate(l,r, s) )
//      if( nL <= nR )
//      { // FORWARD MERGE
//        for(;;) if( from == mid ) return;
//           else if( compare(from,mid) <= 0 ) from++;
//           else break;
//        nL = mid-from;
//        do {} while( ++mid < until && compare(from,mid) > 0 );
//        l = from;
//        r = mid;
//        s = -nL;
//        from = mid - --nL;
//        nR = until-mid;
//      }
//      else
//      { // BACKWARD MERGE
//        for(;;) if( mid == until ) return;
//           else if( compare(mid-1,until-1) <= 0 ) until--;
//           else break;
//        nR = until-mid;
//        do {} while( from < --mid && compare(mid-1,until-1) > 0 );
//        l = mid;
//        r = until;
//        s = nR;
//        until = mid + --nR;
//        nL = mid-from;
//      }
//  }
}
