package com.github.jaaa.select;

import com.github.jaaa.misc.BlockSwapAccess;

import static java.lang.Math.max;
import static java.lang.Math.min;


public interface Mom5SelectV1Access extends HeapSelectAccess, BlockSwapAccess
{
  default void mom5SelectV1_median5( int a, int c, int d )
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

  default long mom5SelectV1_smallSelect_performance( int from, int mid, int until ) {
    return heapSelectMajor_performance(from,mid,until);
  }
  default void mom5SelectV1_smallSelect( int from, int mid, int until ) {
    heapSelectMajor(from,mid,until);
  }

  default void mom5SelectV1( int from, final int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    while( from < mid && mid < until-1 && until-from > 2 && mom5SelectV1_smallSelect_performance(from,mid,until) > 4.6*(until-from) )
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
        j -= 2; mom5SelectV1_median5(i,m,j);
        i += 2;
        m += 1;
      }

      // Select Last Group of size 3 to 7
      // --------------------------------
      if( rem != 0 ) {
        int rhs = j,
            sel =(j -= rem) + rem/2;
        mom5SelectV1_smallSelect(j, sel, rhs);
        swap(sel,j++);
        fth++;
      }

      // Recursively Compute Median of Medians
      // -------------------------------------
      int       piv = i + fth/2;
      piv = min(piv,  i + (  mid - from  )/3); // if `mid` far left,  we can safely move piv to the left
      piv = max(piv,  j - (until - mid +2)/3); // if `mid` far right, we can safely move piv to the right
      mom5SelectV1(i, piv, j);

      // Partition Right of Middle Fifth
      // -------------------------------
      int p = piv;
      for( ; j < until; j++ )
        if( compare(p,j) > 0 )
          swap(++piv,j);
      swap(p,p=piv);

      // Partition Left of Middle Fifth
      // ------------------------------
      while( i-- > from )
        if( compare(i,p) > 0 )
          swap(i,--piv);
      swap(piv,p);

      // Choose Bracket to Continue in
      // -----------------------------
      if( piv < mid ) from  = piv; else
      if( piv > mid ) until = piv; else return;
    }

    if( mid == until ) return;
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }
    if( mid == from    ) { swap(from, argMinL(from,until)); return; }

    mom5SelectV1_smallSelect(from,mid,until);
  }
}
