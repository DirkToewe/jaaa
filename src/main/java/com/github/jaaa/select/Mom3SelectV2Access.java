package com.github.jaaa.select;

import com.github.jaaa.permute.BlockSwapAccess;

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
public interface Mom3SelectV2Access extends HeapSelectAccess, BlockSwapAccess
{
  default void mom3SelectV2_median3( int a, int b, int c )
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

  default long mom3SelectV2_smallSelect_performance( int from, int mid, int until ) {
    return heapSelectMajor_performance(from,mid,until);
  }
  default void mom3SelectV2_smallSelect( int from, int mid, int until ) {
    heapSelectMajor(from,mid,until);
  }

  default void mom3SelectV2( int from, final int mid, int until )
  {
    if( from < 0 || from > mid || mid > until )
      throw new IllegalArgumentException();

    while( from < mid && mid < until-1 && until-from > 8 && mom3SelectV2_smallSelect_performance(from,mid,until) > 4.8*(until-from) )
    {
      int i = from,
          j = until,
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
        for( int lhs = i+trd, m = lhs; i < lhs; ) {
          j -= 1; mom3SelectV2_median3(i,m,j);
          i += 1;
          m += 1;
        }

        if( rem == 0 )
          continue;

        // Select Last Group of size 3 to 4
        // --------------------------------
        int rhs = j,
            sel =(j -= rem) + rem/2;
        mom3SelectV2_smallSelect(j, sel, rhs);
        swap(sel,j++);
        trd++;
      }

      // Recursively Compute Median of Medians
      // -------------------------------------
      int       piv = i + trd/2; // <- piv as in "pivot"
      piv = min(piv,  i + (  mid - from  )/3); // if `mid` far left,  we can safely move piv to the left
      piv = max(piv,  j - (until - mid +2)/3); // if `mid` far right, we can safely move piv to the right
      mom3SelectV2(i, piv, j);

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

    mom3SelectV2_smallSelect(from,mid,until);
  }
}
