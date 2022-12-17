package com.github.jaaa.select;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.compare.ArgMaxAccess;
import com.github.jaaa.compare.ArgMinAccess;
import com.github.jaaa.fn.Int3Op;
import com.github.jaaa.permute.BlockSwapAccess;

import java.util.SplittableRandom;


public interface QuickSelectAccess extends ArgMaxAccess, ArgMinAccess, CompareSwapAccess, HeapSelectAccess, BlockSwapAccess
{
  default Int3Op quickSelect_newPivotChooser() {
    SplittableRandom rng = new SplittableRandom();
    return (from,mid,until) -> {
      if( from > until-5 )
        throw new AssertionError();
      int a = from,
              b = from + 1,
              c = from + until >>> 1,
              d = until - 1,
              e = until - 2;
      swap(a, rng.nextInt(from++,until  ));
      swap(b, rng.nextInt(from++,until  ));
      swap(d, rng.nextInt(from,  until--));
      swap(e, rng.nextInt(from,  until--));
      swap(c, rng.nextInt(from,  until  ));
      if( compare(c,a) < 0 )   swap(c,a);
      if( compare(d,b) < 0 )   swap(d,b);
      if( compare(d,c) < 0 ) { swap(d,c); swap(a,b); }
      if( compare(e,b) < 0 )   swap(e,b);
      if( compare(e,c) < 0 ) { swap(e,c); b = a; }
      if( compare(c,b) < 0 )   swap(c,b);
//      assert compare(a,c) <= 0;
//      assert compare(b,c) <= 0;
//      assert compare(c,d) <= 0;
//      assert compare(c,e) <= 0;
      return c;
    };
  }

  /** Selects deterministically in situations where it is to be expected
   *  more efficient thant quick selection. Returns <code>true</code> if
   *  deterministic merging was applied, <code>false</code> otherwise.
   */
  default long quickSelect_detSelect_performance( int from, int mid, int until ) {
    return heapSelect_performance(from,mid,until);
  }

  default void quickSelect_detSelect( int from, int mid, int until ) { heapSelect(from,mid,until); }

  default void quickSelect( int from, int mid, int until )
  {
    if( 0 > from || from > mid | mid > until ) throw new IllegalArgumentException();
    if( mid == until ) return;

    Int3Op choosePivot = quickSelect_newPivotChooser();

    while( from < mid && mid < until-1 && until-from > 10 && quickSelect_detSelect_performance(from,mid,until) > 2.1*(until - from) )
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
    quickSelect_detSelect(from,mid,until);
  }
}
