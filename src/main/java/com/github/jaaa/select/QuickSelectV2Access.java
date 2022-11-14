package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.fn.Int3Op;
import com.github.jaaa.misc.BlockSwapAccess;

import java.util.SplittableRandom;

import static java.lang.Math.min;


public interface QuickSelectV2Access extends ArgMaxAccess, ArgMinAccess, CompareSwapAccess, HeapSelectAccess, BlockSwapAccess
{
  default Int3Op quickSelectV2_newPivotChooser() {
    var rng = new SplittableRandom();
    return (from,mid,until) -> {
      int    N = min(5,until-from);
      assert N <= until-from;
      for( int i=0; i < N; i++ ) {
        int j = rng.nextInt(from+i,until);
        swap(from+i,j);
      }
      int pivot = from+(N>>>1); heapSelectMajor(from, pivot, from+N);
      swap( pivot, pivot=from+until>>>1 );
      return  pivot;
    };
  }

  /** Selects deterministically in situations where it is to be expected
   *  more efficient thant quick selection. Returns <code>true</code> if
   *  deterministic merging was applied, <code>false</code> otherwise.
   */
  default long quickSelectV2_detSelect_performance( int from, int mid, int until ) {
    return heapSelectMajor_performance(from,mid,until);
  }

  default void quickSelectV2_detSelect( int from, int mid, int until ) { heapSelectMajor(from,mid,until); }

  default void quickSelectV2( int from, int mid, int until )
  {
    if( 0 > from || from > mid | mid > until ) throw new IllegalArgumentException();
    if( mid == until ) return;

    var choosePivot = quickSelectV2_newPivotChooser();

    while( from < mid && mid < until-1 && until-from > 10 && quickSelectV2_detSelect_performance(from,mid,until) > 2.1*(until - from) )
    {
      // SELECT RANDOM PIVOT
      // -------------------
      int l = choosePivot.applyAsInt(from,mid,until),
          r = l+1;

      // PARTITION BY PIVOT
      // ------------------
      partition: {
        int i=l, j=r;
        loop: while( from < i-- ) {
          // Skip ahead in left elements
          // ---------------------------
          int il = compare(i,l);
          if( il <= 0 ) {
            if( il == 0 )
              swap(i,--l); // <- add to pivots
            continue;
          }
          // Skip ahead in right elements
          // ----------------------------
          for( ; j < until; j++ ) {
            int lj = compare(l,j);
            if( lj > 0 ) {
              swap(i,j++);
              continue loop;
            }
            if( lj == 0 )
              swap(r++,j); // <- add to pivots
          }
          // Handle remaining left elements
          // ------------------------------
          int k = l;
          swap(i,--k);
          while( from < i-- ) {
                il = compare(i,l);
            if( il >= 0 ) { swap(i,--k);
            if( il == 0 )   swap(k,--l); } // <- add to pivots
          }
          // roll pivots over right-hand side elements
          while( k < l )
            swap(--l,--r);
          break partition;
        }
        // Handle remaining right elements
        // -------------------------------
        int k = r;
        for( ; j < until; j++) {
          int lj = compare(l,j);
          if( lj >= 0 ) { swap(j,k);
          if( lj == 0 )   swap(k,r++); k++; }
        }
        // roll pivots over left-hand side elements
        while( r < k )
          swap(l++,r++);
      }

      // UPDATE BOUNDS
      // -------------
           if( mid <  l ) until = l;
      else if( mid >= r ) from  = r;
      else return;
    }

    if( mid == from    ) { swap(from, argMinL(from,until)); return; }
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }
    quickSelectV2_detSelect(from,mid,until);
  }
}
