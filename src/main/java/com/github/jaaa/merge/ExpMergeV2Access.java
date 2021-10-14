package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;


public interface ExpMergeV2Access extends RotateAccess,
                                    ExpL2RSearchAccess,
                                    ExpR2LSearchAccess
{
//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from < 0   ) throw new IllegalArgumentException();
//    if( from > mid ) throw new IllegalArgumentException();
//    if(until < mid ) throw new IllegalArgumentException();
//
//    final int STUCK_LIMIT = 7;
//    int nL =   mid-from,
//        nR = until-mid, stuckometer = 0,
//         m = mid;
//
//    for(;;)
//      if( nL <= nR )
//      { // MERGE FORWARD
//        if( nL <= 0 ) return;
//        --nL;
//        m = expL2RSearchGap(m,until, from, /*rightBias=*/false);
//        if( mid < m )
//        {
//          mid = m;
//          rotate(from,mid, ~nL);
//          from = mid - nL;
//          nR = until-mid;
//          stuckometer = 0;
//        }
//        else {
//          ++from;
//          if( STUCK_LIMIT < ++stuckometer ) {
//            nL += from - (from = expL2RSearchGap(from,mid, mid, /*rightBias=*/true));
//            ++m; // <- guarantees that loop terminates next iteration
//          }
//        }
//      }
//      else
//      { // MERGE BACKWARD
//        if( nR <= 0 ) return;
//        m = expR2LSearchGap(from,m, --until, /*rightBias=*/true);
//        if( mid > m )
//        {
//          mid = m;
//          rotate(mid,until+1, nR);
//          until = mid + --nR;
//          nL = mid-from;
//          stuckometer = 0;
//        }
//        else {
//          --nR;
//          if( STUCK_LIMIT < ++stuckometer )
//            nR -= until - (until = expR2LSearchGap(mid,until, --m, /*rightBias=*/false));
//        }
//      }
//  }

//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from < 0   ) throw new IllegalArgumentException();
//    if( from > mid ) throw new IllegalArgumentException();
//    if(until < mid ) throw new IllegalArgumentException();
//
//    final int STUCK_LIMIT = 7;
//    int nL =       mid-from,
//        nR = until-mid;
//
//    loop: for(;;)
//    {
//      int m=mid, stuckometer=0;
//      if( nL <= nR )
//        for(;;)
//        { // MERGE FORWARD
//          if( --nL < 0 ) return;
//                  mid = expL2RSearchGap(mid,until, from, /*rightBias=*/false);
//          if( m < mid ) {
//            rotate(from,mid, ~nL);
//            from = mid - nL;
//            nR = until-mid;
//            continue loop;
//          }
//          ++from;
//          if( STUCK_LIMIT <= ++stuckometer )
//            nL += from - (from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true)); // <- mid++ guarantees that loop terminates next iteration
//        }
//      else for(;;)
//      { // MERGE BACKWARD
//        if( --nR < 0 ) return;
//                mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true);
//        if( m > mid ) {
//          rotate(mid,until+1, nR+1);
//          until = mid + nR;
//          nL = mid-from;
//          continue loop;
//        }
//        if( STUCK_LIMIT <= ++stuckometer )
//          nR -= until - (until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false)); // <- --mid guarantees that loop terminates next iteration
//      }
//    }
//  }

  default void expMergeV2( int from, int mid, int until )
  {
    if( 0 > from || from > mid || mid > until )
      throw new IllegalArgumentException();

    int nL =   mid-from,
        nR = until-mid;

    loop: for(;;) {
      int m=mid, stuckometer=6;
      if( nL <= nR )
        for(;;)
        { // MERGE FORWARD
          if( --nL < 0 ) return;
          if( m < (mid = expL2RSearchGap(mid,until, from, /*rightBias=*/false)) ) {
            rotate(from,mid, ~nL);
            from = mid - nL;
            nR = until-mid;
            continue loop;
          }
          ++from;
          if( --stuckometer < 0 )
            nL += from - (from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true)); // <- mid++ guarantees that loop terminates next iteration
        }
      else
        for(;;)
        { // MERGE BACKWARD
          if( --nR < 0 ) return;
          if( m > (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
            rotate(mid,until+1, nR+1);
            until = mid + nR;
            nL = mid-from;
            continue loop;
          }
          if( --stuckometer < 0 )
            nR -= until - (until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false)); // <- --mid guarantees that loop terminates next iteration
        }
    }
  }

//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from < 0 || from > mid || until < mid )
//      throw new IllegalArgumentException();
//
//    int nL =   mid-from,
//        nR = until-mid;
//
//    loop: for(;;) {
//      int m=mid, stuckometer=6;
//      if( nL <= nR )
//        for(;;)
//        { // MERGE FORWARD
//          if( --nL < 0 ) return;
//          if( m == (mid = expL2RSearchGap(mid,until, from++, /*rightBias=*/false)) && --stuckometer < 0 )
//            nL += from - (from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true)); // <- mid++ guarantees that loop terminates next iteration
//          else {
//            rotate(from-1,mid, ~nL);
//            from = mid - nL;
//            nR = until-mid;
//            continue loop;
//          }
//        }
//      else
//        for(;;)
//        { // MERGE BACKWARD
//          if( --nR < 0 ) return;
//          if( m == (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) && --stuckometer < 0 )
//            nR -= until - (until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false)); // <- --mid guarantees that loop terminates next iteration
//          else {
//            rotate(mid,until+1, nR+1);
//            until = mid + nR;
//            nL = mid-from;
//            continue loop;
//          }
//        }
//    }
//  }
}
