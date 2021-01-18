package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static java.lang.Integer.highestOneBit;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


//  REFERENCES
//  ----------
//  .. [1] "Optimal merging of 2 elements with n elements"
//          F. K. Hwang & S. Lin
//          Acta Informatica I, pages 145–158 (1971)
//  .. [2] "Significant Improvements to the Hwang-Lin Merging Algorithm"
//          Glenn K. Manacher
//          Journal of the ACM, July 1979,
//          https://doi.org/10.1145/322139.322144
//  .. [3] "The Ford-Johnson Sorting Algorithm is Not Optimal"
//          Glenn K. Manacher
//          Journal of the ACM, July 1979,
//          https://doi.org/10.1145/322139.322145
//
//  Implementation Details
//  ----------------------
//  This is the static variant of the Hwang-Lin merge algorithm, where 2^⌊log 2 (n/m)⌋
//  is computed only once, i.e. `m` and `n` are the initial sizes of the initially smaller
//  and larger merged sequence respectively, not the current sizes. It is worth mentioning
//  that 2^⌊log 2 (n/m)⌋ is the same as the highest one bit of ⌊m/n⌋ in binary representation.
//
//  TODO:
//    * Implement the improved Hwang Lin merge as described in [2] and [3].
//

public interface HwangLinMergeStaticAccessor<T> extends CompareRandomAccessor<T>
{
  default void hwangLinMergeV2(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      hwangLinMergeV2_R2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      hwangLinMergeV2_L2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  default void hwangLinMergeV2_L2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeL2R(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0
    );

    int cmp=1;
    // make sure that `a` is always shorter
    if( aLen > bLen ) {
      T d=a; a=b; b=d;
      int l=a0; a0=b0; b0=l;
      int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
    }

    int a1 = a0+aLen,
        b1 = b0+bLen;

    if( a0 < a1 )
    {
      int step = highestOneBit(bLen/aLen) - 1;
      loop: do
      {
        int lo = b0,
            hi = b0 + step;

        if( hi >= b1 )
            hi  = b1;
        else if( compare(a,a0, b,hi) >= cmp ) {
          int s = step+1;
          copyRange(b,b0, c,c0, s);
          b0 += s;
          c0 += s;
          continue loop;
        }

        // find pos. of a0 via binary search
        while( lo < hi ){int  mid = lo+hi >>> 1;
          if( compare(a,a0, b,mid) < cmp )
                         hi = mid;
          else           lo = mid+1;
        }

        // copy part of b, then insert a0 in right place
        int                   len = lo - b0;
        copyRange(b,b0, c,c0, len);
        b0 += len;
        c0 += len;
        copy(a,a0++, c,c0++);
      }
      while( a0 < a1 );
    }

    copyRange(b,b0, c,c0, b1-b0);
  }


  default void hwangLinMergeV2_R2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeR2L(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0
    );

    int cmp=0;
    // make sure that `a` is always shorter
    if( aLen > bLen ) {
      T d=a; a=b; b=d;
      int l=a0; a0=b0; b0=l;
      int o=aLen; aLen=bLen; bLen=o; cmp ^= -1;
    }

    int a1 = a0+aLen,
        b1 = b0+bLen,
        c1 = c0+aLen+bLen;

    if( a0 < a1 )
    {
      int step = highestOneBit(bLen/aLen);
      loop: do
      {
        int hi = b1,
            lo = b1 - step;

        --a1;

        if( lo <= b0 )
            lo  = b0-1;
        else if( compare(a,a1, b,lo) <= cmp ) {
          a1++;
          b1 -= step;
          c1 -= step;
          copyRange(b,b1, c,c1, step);
          continue loop;
        }

        // find pos. of a0 via binary search
             ++lo;
        while( lo < hi ){int  mid = lo+hi >>> 1;
          if( compare(a,a1, b,mid) > cmp )
                         lo = mid+1;
          else           hi = mid;
        }

        // copy part of b, then insert a0 in right place
        int   len = b1-lo;
        b1 -= len;
        c1 -= len;
        copyRange(b,b1, c,  c1, len);
        copy     (a,a1, c,--c1);
      }
      while( a0 < a1 );
    }

    copyRange(b,b0, c,c0, b1-b0);
  }
}
