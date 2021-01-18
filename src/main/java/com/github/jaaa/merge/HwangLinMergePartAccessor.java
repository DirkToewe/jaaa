package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static java.lang.Integer.highestOneBit;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static java.lang.Math.min;


// REFERENCES
// ----------
// .. [1] "Optimal merging of 2 elements with n elements"
//         F. K. Hwang & S. Lin
//         Acta Informatica I, pages 145–158 (1971)

public interface HwangLinMergePartAccessor<T> extends CompareRandomAccessor<T>
{
  default void hwangLinMergePartV1_L2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    checkArgs_mergePartL2R(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0, cLen
    );

    for( int cmp=1;; )
    {
      aLen = min(aLen,cLen);
      bLen = min(bLen,cLen);

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
          else                                lo = +1 + mid;
        }
        copyRange(b,b0, c,c0, lo);
        aLen -= 1;
        bLen -= lo;
        cLen -= lo+1;
          b0 += lo;
          c0 += lo;
        copy(a,a0++, c,c0++);
      }
      else {
        // otherwise copy step from b to c and proceed
        ++step;
        copyRange(b,b0, c,c0, step);
        bLen -= step;
        cLen -= step;
          b0 += step;
          c0 += step;
      }
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void hwangLinMergePartV1_R2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    checkArgs_mergePartR2L(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0, cLen
    );

    a0 += aLen;
    b0 += bLen;
    c0 += cLen;

    for( int cmp = 0;; )
    {
      aLen = min(aLen,cLen);
      bLen = min(bLen,cLen);

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

      a0 -= 1;
      b0 -= step;
      if( compare(a,a0, b,b0) > cmp )
      {
        // find pos. of a[0] via binary search
        int lo = 1,
            hi = step-1;
        while( lo <= hi ){   int mid = lo+hi >>> 1;
          if( compare(a,a0, b,b0+mid) > cmp ) lo = +1 + mid;
          else                                hi = -1 + mid;
        }
        b0 += lo;
        c0 -= step -= lo;
        copyRange(b,b0, c,c0, step);
        copy(a,a0, c,--c0);
        aLen -= 1;
        cLen -= 1;
      }
      else {
        // otherwise copy step from b to c and proceed
        a0 += 1;
        c0 -= step; copyRange(b,b0, c,c0, step);
      }
      bLen -= step;
      cLen -= step;
    }

    copyRange(b,b0-bLen, c,c0-bLen, bLen);
  }
}
