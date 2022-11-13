package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.fn.Int3Op;

import java.util.SplittableRandom;


public interface QuickSelectV1Access extends ArgMaxAccess, ArgMinAccess, CompareSwapAccess, HeapSelectAccess
{
  default Int3Op quickSelectV1_newPivotChooser() {
    var rng = new SplittableRandom();
    return (from,mid,until) -> {
      int pivot = rng.nextInt(from,until);
      swap( pivot, pivot=from+until>>>1 );
      return  pivot;
    };
  }

  /** Selects deterministically in situations where it is to be expected
   *  more efficient thant quick selection. Returns <code>true</code> if
   *  deterministic merging was applied, <code>false</code> otherwise.
   */
  default long quickSelectV1_detSelect_performance( int from, int mid, int until )
  {
    return heapSelectMajor_performance(from,mid,until);
  }

  default void quickSelectV1_detSelect( int from, int mid, int until )
  {
    heapSelectMajor(from,mid,until);
  }

  default void quickSelectV1( int from, int mid, int until )
  {
    if( from < 0 || from > mid | mid > until ) throw new IllegalArgumentException();
    if( from == mid || mid == until ) return;

    var choosePivot = quickSelectV1_newPivotChooser();

    while( from < until-1 && quickSelectV1_detSelect_performance(from,mid,until) > 3L*(until - from) )
    {
      // SELECT RANDOM PIVOT
      // -------------------
      int pivot = choosePivot.applyAsInt(from,mid,until),
        l=pivot,   i=l,
        r=pivot+1, j=r;

      // PARTITION BY PIVOT
      // ------------------
      partition: while( i-- > from )
      {
        // find left element that belongs right
        int il = compare(i,l);
        if( il == 0 ) swap(i,--l); // <- add to pivots
        if( il <= 0 ) continue; // <- keep looking
        // find right element that belongs left
        for(;;) {
          if( j >= until ) {
            // no more right elements -> roll left element through pivots
            swap(i,--l);
            swap(l,--r);
            continue partition;
          }
          int k = j++;
          int lj = compare(l,k);
          if( lj== 0 ) swap(k,r++); // <- add to pivots
          if( lj > 0 ) {
            swap(i,k); // <- found left and right element so lets swap
            continue partition;
          }
        }
      }
      // handle remaining right elements
      for( ; j < until; j++ ) {
        int   c = compare(l,j);
        if  ( c>= 0 ){swap(r,j);
          if( c > 0 ) swap(r,l++);
                             r++;
        }
      }

      // UPDATE BOUNDS
      // -------------
           if( mid <  l ) until = l;
      else if( mid >= r ) from  = r;
      else return;
    }

    quickSelectV1_detSelect(from,mid,until);
  }

//  default void quickSelectV1( int from, int mid, int until )
//  {
//    if( 0 > from || from > mid | mid > until ) throw new IllegalArgumentException();
//
//    var rng = quickSelectV1_newRNG();
//
//    while( from < mid && mid < until )
//    {
//      // SELECT RANDOM PIVOT
//      int  pivot = rng.applyAsInt(from,until);
//      swap(pivot, pivot = from+until >>> 1); // <- move pivot to center
//
//      int l=pivot,   i=l,
//          r=pivot+1, j=r;
//      // PARTITION
//      partition: while( i-- > from )
//      {
//        // find left element that belongs right
//        int il = compare(i,l);
//        if( il == 0 ) swap(i,--l); // <- add to pivots
//        if( il <= 0 ) continue; // <- keep looking
//        // find right element that belongs left
//        search_r: for(;;) {
//          if( j >= until ) {
//            // no more right elements -> roll left element through pivots
//            swap(i,--l);
//            swap(l,--r);
//            continue partition;
//          }
//          int k = j++;
//          int      lj = compare(l,k);
//               if( lj== 0 ) swap(k,r++); // <- add to pivots
//          else if( lj > 0 ) {
//            swap(i,k); // <- found left and right element so lets swap
//            continue partition;
//          }
//        }
//      }
//      // handle remaining right elements
//      for( ; j < until; j++ ) {
//        int   c = compare(l,j);
//        if(   c>= 0 ) { swap(r,j);
//          if( c > 0 )   swap(r,l++);
//                               r++;
//        }
//      }
//
//      // UPDATE BOUNDS
//           if( mid <  l ) until = l;
//      else if( mid >= r ) from  = r;
//      else return;
//    }
//  }

//  default void quickSelectV1( int from, int mid, int until )
//  {
//    if( 0 > from || from > mid | mid > until ) throw new IllegalArgumentException();
//
//    var rng = quickSelectV1_newRNG();
//
//    while( from < mid && mid < until )
//    {
//      // SELECT RANDOM PIVOT
//      int  pivot = rng.applyAsInt(from,until);
//      swap(pivot,from);
//
//      // PARTITION
//      int l=from,
//          r=until;
//      for( int i=from; ++i < r; ) {
//        int c = compare(i,l);
//             if( c < 0) swap(i,l++);
//        else if( c > 0) swap(i--,--r);
//      }
//
//      // UPDATE BOUNDS
//           if( mid <  l ) until = l;
//      else if( mid >= r ) from  = r;
//      else return;
//    }
//  }
}
