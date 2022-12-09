package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static java.lang.Integer.highestOneBit;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


// REFERENCES
// ----------
// .. [1] "Optimal merging of 2 elements with n elements"
//         F. K. Hwang & S. Lin
//         Acta Informatica I, pages 145–158 (1971)
//
// Implementation Details
// ----------------------
// This is the dynamic variant of the Hwang-Lin merge algorithm, where 2^⌊log 2 (n/m)⌋
// is computed everytime, i.e. `m` and `n` are always the current sizes of the smaller
// and larger merged sequence respectively, not the initial sizes. It is worth mentioning
// that 2^⌊log 2 (n/m)⌋ is the same as the highest one bit of ⌊m/n⌋ in binary representation.
//

public interface HwangLinMergeAccessor<T> extends CompareRandomAccessor<T>
{
  default void hwangLinMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      hwangLinMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      hwangLinMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  default void hwangLinMergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeL2R(
      a, a0, aLen,
      b, b0, bLen,
      c, c0
    );

    for( int cmp=1;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if( aLen <= 0 ) break;

      // Step by which to jump/look ahead. The step is carefully designed such
      // that at most aLen*log(bLen) such steps are taken (every aLen steps without an
      // element taken from a, about bLen/2 elements are taken from b).
      int step = highestOneBit(bLen/aLen) - 1;

      if( compare(a,a0, b,b0+step) < cmp )
      {
        // find pos. of a[0] via binary search
        int lo = 0,
            hi = step-1;
        while( lo <= hi ){ int mid = lo+hi >>> 1;
          if( compare(a,a0, b,b0+mid) < cmp ) hi = -1 + mid;
          else                                lo =  1 + mid;
        }

        // copy [...b[:lo], a[0]] -> c[:lo+1]
        aLen -= 1;
        bLen -= lo; copyRange(b,b0, c,c0, lo);
          b0 += lo;
          c0 += lo; copy(a,a0++, c,c0++);
      }
      else {
        // otherwise copy step from b to c and proceed
              ++step;
        bLen -= step; copyRange(b,b0, c,c0, step);
          b0 += step;
          c0 += step;
      }
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void hwangLinMergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeR2L(
      a, a0, aLen,
      b, b0, bLen,
      c, c0
    );

    c0 += aLen+bLen;

    for( int cmp = 0;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= -1;
      }

      if( aLen <= 0 ) break;

      // Step by which to jump/look ahead. The step is carefully designed such
      // that at most aLen*log(bLen) such steps are taken (every aLen steps without an
      // element taken from a, about bLen/2 elements are taken from b).
      int step = highestOneBit(bLen/aLen);

      aLen -= 1;
      bLen -= step;
        c0 -= step;
      if( compare(a,a0+aLen, b,b0+bLen) > cmp )
      {
        // find pos. of a[0] via binary search
        int lo = bLen + 1,
            hi = bLen + step-1;
        while( lo <= hi ){   int mid = lo+hi >>> 1;
          if( compare(a,a0+aLen, b,b0+mid) > cmp ) lo =  1 + mid;
          else                                     hi = -1 + mid;
        }
        bLen += lo -= bLen;
          c0 += lo;
        copyRange(b,b0+bLen, c,c0, step-lo);
        copy(a,a0+aLen, c,--c0);
      }
      else {
        // otherwise copy step from b to c and proceed
        copyRange(b,b0+bLen, c,c0, step);
        aLen += 1;
      }
    }

    copyRange(b,b0, c,c0-bLen, bLen);
  }
}
