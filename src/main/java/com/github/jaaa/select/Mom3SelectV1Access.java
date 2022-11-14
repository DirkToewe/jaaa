package com.github.jaaa.select;

import com.github.jaaa.misc.BlockSwapAccess;

import static com.github.jaaa.util.IMath.sign;
import static java.lang.Math.max;
import static java.lang.Math.min;


/** An implementation of the Repeated Step Algorithm as described in:
 *
 * "Select with Groups of 3 or 4"
 * Ke Chen, Adrian Dumitrescu
 * October 21, 2015
 *
 * http://www.cs.uwm.edu/faculty/ad/select.pdf
 *
 * With further optimizations as described in:
 *
 * "Fast Deterministic Selection"
 * Andrei Alexandrescu
 *
 * https://arxiv.org/abs/1606.00484
 */
public interface Mom3SelectV1Access extends HeapSelectAccess, BlockSwapAccess
{
  default void mom3SelectV1_median3( int a, int b, int c )
  {
    int ab = sign( compare(a,b) ),
        bc = sign( compare(b,c) );
    if( ab*bc >= 0 ) {
      if( (ab|bc) > 0 ) swap(a,c);
    }
    else {
      if( compare(a,c) > 0 ) {
        swap(a,c);
        ab = -bc;
      }
      if( ab > 0 ) c = a;
      swap(b,c);
    }
  }

  default long mom3SelectV1_smallSelect_performance( int from, int mid, int until ) {
    return heapSelectMajor_performance(from,mid,until);
  }
  default void mom3SelectV1_smallSelect( int from, int mid, int until ) {
    heapSelectMajor(from,mid,until);
  }

  default void mom3SelectV1( int from, final int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    while( from < mid && mid < until-1 && until-from > 8 && mom3SelectV1_smallSelect_performance(from,mid,until) > 4.8*(until-from) )
    {
      int l = from,
          r = until,
        trd = until - from;
      // Perform two Rounds of Group Selection
      // -------------------------------------
      for( int repeat=2; repeat-- > 0; )
      {
        int rem = trd % 3;
        trd /= 3;
        // Make sure last group is size 3 to 7
        if( rem != 0 ) {
          trd -= 1;
          rem += 3;
        }

        // Select Groups of 3
        // ------------------
        for( int lhs = l+trd, m = lhs; l < lhs; ) {
          r -= 1; mom3SelectV1_median3(l,m,r);
          l += 1;
          m += 1;
        }

        if( rem == 0 )
          continue;

        // Select Last Group of size 3 to 4
        // --------------------------------
        int rhs = r,
            sel =(r -= rem) + rem/2;
        mom3SelectV1_smallSelect(r, sel, rhs);
        swap(sel,r++);
        trd++;
      }

      // Recursively Compute Median of Medians
      // -------------------------------------
      int       piv = l + trd/2; // <- piv as in "pivot"
      piv = min(piv,  l + (  mid - from  )/3); // if `mid` far left,  we can safely move piv to the left
      piv = max(piv,  r - (until - mid +2)/3); // if `mid` far right, we can safely move piv to the right
      mom3SelectV1(l, piv, r);

      // Partition Right of Middle Third
      // -------------------------------
      int p = piv;
      for( ; r < until; r++ )
        if( compare(p,r) > 0 )
          swap(++piv,r);
      swap(p,p=piv);

      // Partition Left of Middle Third
      // ------------------------------
      while( l-- > from )
        if( compare(l,p) > 0 )
          swap(l,--piv);
      swap(piv,p);

      // Choose Bracket to Continue in
      // -----------------------------
      if( piv < mid ) from  = piv; else
      if( piv > mid ) until = piv; else return;
    }

    if( mid == until ) return;
    if( mid == until-1 ) { swap(mid,  argMaxR(from,until)); return; }
    if( mid == from    ) { swap(from, argMinL(from,until)); return; }

    mom3SelectV1_smallSelect(from,mid,until);
  }
}
