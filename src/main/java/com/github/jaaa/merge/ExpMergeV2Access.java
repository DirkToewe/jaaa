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

//  default void expMergeV2( int _from, int _mid, int _until )
//  {
//    if( _from >= 0 && _mid >= _from && _mid <= _until )
//      new Object()
//      {
//        private boolean l2r() {
//          merge_forward: for(;;)
//            for( int stuckometer=7, m=mid;; )
//            { // MERGE FORWARD
//              assert (mid-from) <= (until-mid);
//              if( m == from ) return false;
//              if( m != (mid = expL2RSearchGap(mid,until, from++, /*rightBias=*/false)) ) {
//                rotate(from-1,mid, m = mid-m);
//                from += m;
//                if( (mid<<1) - until > from )
//                  return true;
//                else
//                  continue merge_forward;
//              }
//              if( --stuckometer <= 0 )
//                from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true); // <- mid++ guarantees that loop terminates next iteration
//            }
//        }
//        private boolean r2l() {
//          merge_backward: for(;;)
//            for( int stuckometer=7, m=mid;; )
//            { // MERGE BACKWARD
//              assert (mid-from) >= (until-mid);
//              if( m == until ) return false;
//              if( m != (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
//                rotate(mid,until+1, m = mid-m);
//                until += m;
//                if( (mid<<1) - until < from )
//                  return true;
//                else
//                  continue merge_backward;
//              }
//              if( --stuckometer <= 0 )
//                until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false); // <- --mid guarantees that loop terminates next iteration
//            }
//        }
//
//        int from = _from, mid = _mid, until = _until;
//
//        {
//          block: {
//            if( (mid<<1) - until > from )
//              if( ! r2l() ) break block;
//            for(;;) {
//              if( ! l2r() ) break block;
//              if( ! r2l() ) break block;
//            }
//          }
//        }
//      };
//    else throw new IllegalArgumentException();
//  }

//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from >= 0 && mid >= from && mid <= until )
//      for( int state = from+until - (mid<<1) >>> 31;; state=0 )
//        switch(state)
//        {
//          default: throw new IllegalArgumentException(""+state);
//          case 0:
//            merge_forward: for(;;)
//            for( int stuckometer=7, m=mid;; )
//            { // MERGE FORWARD
//              assert (mid-from) <= (until-mid);
//              if( m == from ) return;
//              if( m != (mid = expL2RSearchGap(mid,until, from++, /*rightBias=*/false)) ) {
//                rotate(from-1,mid, m = mid-m);
//                from += m;
//                if( (mid<<1) - until > from )
//                  break merge_forward;
//                else
//                  continue merge_forward;
//              }
//              else if( --stuckometer <= 0 )
//                from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true); // <- mid++ guarantees that loop terminates next iteration
//            }
//          case 1:
//            merge_backward: for(;;)
//            for( int stuckometer=7, m=mid;; )
//            { // MERGE BACKWARD
//              assert (mid-from) >= (until-mid);
//              if( m == until ) return;
//              if( m != (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
//                rotate(mid,until+1, m = mid-m);
//                until += m;
//                if( (mid<<1) - until < from )
//                  break merge_backward;
//                else
//                  continue merge_backward;
//              }
//              else if( --stuckometer <= 0 )
//                until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false); // <- --mid guarantees that loop terminates next iteration
//            }
//      }
//    throw new IllegalArgumentException();
//  }

//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from >= 0 && from <= mid && mid <= until )
//      loop: for(;;)
//      {
//        int m=mid, stuckometer=7,
//            nL = mid-from,
//                  nR = until-mid;
//        if( nL <= nR )
//          for(;;) // MERGE FORWARD
//            if( m != from ) {
//              if( m != (mid = expL2RSearchGap(mid,until, from++, /*rightBias=*/false)) ) {
//                rotate(from-1,mid, m = mid-m);
//                from += m;
//                continue loop;
//              }
//              if( --stuckometer <= 0 )
//                from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true); // <- mid++ guarantees that loop terminates next iteration
//            }
//            else return;
//        else
//          for(;;) // MERGE BACKWARD
//            if( m != until ) {
//              if( m != (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
//                rotate(mid,until+1, m = mid-m);
//                until += m;
//                continue loop;
//              }
//              if( --stuckometer <= 0 )
//                until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false); // <- --mid guarantees that loop terminates next iteration
//            }
//            else return;
//      }
//    throw new IllegalArgumentException();
//  }


//  default void expMergeV2( int from, int mid, int until )
//  {
//    if( from < 0 || from > mid || mid > until )
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
//          if( m < (mid = expL2RSearchGap(mid,until, from, /*rightBias=*/false)) ) {
//            rotate(from,mid, ~nL);
//            from = mid - nL;
//            nR = until-mid;
//            continue loop;
//          }
//          ++from;
//          if( --stuckometer < 0 )
//            nL += from - (from = expL2RSearchGap(from,mid, mid++, /*rightBias=*/true)); // <- mid++ guarantees that loop terminates next iteration
//        }
//      else
//        for(;;)
//        { // MERGE BACKWARD
//          if( --nR < 0 ) return;
//          if( m > (mid = expR2LSearchGap(from,mid, --until, /*rightBias=*/true)) ) {
//            rotate(mid,until+1, nR+1);
//            until = mid + nR;
//            nL = mid-from;
//            continue loop;
//          }
//          if( --stuckometer < 0 )
//            nR -= until - (until = expR2LSearchGap(mid,until, --mid, /*rightBias=*/false)); // <- --mid guarantees that loop terminates next iteration
//        }
//    }
//  }


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
