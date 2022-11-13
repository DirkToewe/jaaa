package com.github.jaaa.select;

import com.github.jaaa.ArgMaxAccess;
import com.github.jaaa.ArgMinAccess;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.fn.Int3Op;

import java.util.SplittableRandom;


public interface QuickSelectV2Access extends ArgMaxAccess, ArgMinAccess, CompareSwapAccess, HeapSelectAccess
{
  default Int3Op quickSelectV2_newPivotChooser() {
    var rng = new SplittableRandom();
    return (from,mid,until) -> {
      int    N = 3;
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
  default long quickSelectV2_detSelect_performance( int from, int mid, int until )
  {
    return heapSelectMajor_performance(from,mid,until);
  }

  default void quickSelectV2_detSelect( int from, int mid, int until )
  {
    heapSelectMajor(from,mid,until);
  }

  default void quickSelectV2( int from, int mid, int until )
  {
    if( 0 > from || from > mid | mid > until ) throw new IllegalArgumentException();
    if( mid == until ) return;

    var choosePivot = quickSelectV2_newPivotChooser();

    while( quickSelectV2_detSelect_performance(from,mid,until) > 3L*(until - from) )
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

    quickSelectV2_detSelect(from,mid,until);
  }
}
