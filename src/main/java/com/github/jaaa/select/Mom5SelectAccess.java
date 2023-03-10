package com.github.jaaa.select;

import com.github.jaaa.permute.BlockSwapAccess;

import static java.lang.Math.max;
import static java.lang.Math.min;


public interface Mom5SelectAccess extends HeapSelectAccess, BlockSwapAccess
{
  default void mom5Select_median5( int a, int c, int d )
  {
    int b = a+1,
        e = d+1;
    if( compare(c,a) < 0 )   swap(c,a);
    if( compare(d,b) < 0 )   swap(d,b);
    if( compare(d,c) < 0 ) { swap(d,c); swap(a,b); }
    if( compare(e,b) < 0 )   swap(e,b);
    if( compare(e,c) < 0 ) { swap(e,c); b = a; }
    if( compare(c,b) < 0 )   swap(c,b);
  }

  default long mom5Select_smallSelect_performance( int from, int mid, int until ) { return heapSelect_performance(from,mid,until); }
  default void mom5Select_smallSelect            ( int from, int mid, int until ) {        heapSelect            (from,mid,until); }

  default void mom5Select( int from, final int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    while( from < mid && mid < until-1 && until-from > 2 && mom5Select_smallSelect_performance(from,mid,until) > 4.6*(until-from) )
    {
      int       len = until - from;
      int fth = len/5,
          rem = len-5*fth;
      // Make sure last group is size 3 to 7
      if( 0 < rem && rem < 3 ) {
        fth -= 1;
        rem += 5;
      }

      // Select Groups of 5
      // ------------------
      int i = from,
          j = until;
      for( int lhs = from + fth*2, m = lhs; i < lhs; ) {
        j -= 2; mom5Select_median5(i,m,j);
        i += 2;
        m += 1;
      }

      // Select Last Group of size 3 to 7
      // --------------------------------
      if( rem != 0 ) {
        int rhs = j,
            sel =(j -= rem) + rem/2;
        mom5Select_smallSelect(j, sel, rhs);
        swap(sel,j++);
        fth++;
      }

      // Recursively Compute Median of Medians
      // -------------------------------------
      int       piv = i + fth/2;
      piv = min(piv,  i + (  mid - from  )/3); // if `mid` far left,  we can safely move piv to the left
      piv = max(piv,  j - (until - mid +2)/3); // if `mid` far right, we can safely move piv to the right
      mom5Select(i, piv, j);

      // PARTITION BY PIVOT
      // ------------------
      int l = piv,
          r = piv+1;
      partition: {
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

    if( mid == until ) return;
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }
    if( mid == from    ) { swap(from, argMinL(from,until)); return; }

    mom5Select_smallSelect(from,mid,until);
  }
}
