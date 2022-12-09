package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;
import static java.lang.Math.min;

// Like V1 (not V2), but using a slightly modified exponential search which:
//   * requires 1 comparison  to find index 0
//   * requires 2 comparisons to find index 1
//   * requires 2 + 2*log2floor(i) comparisons to find index i
public interface ExpMergeV3Accessor<T> extends CompareRandomAccessor<T>
{
  default void expMergeV3(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      expMergeV3_R2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      expMergeV3_L2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  default void expMergeV3_L2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    for( int cmp = 0;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T   d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= -1;
      }

      if( aLen <= 0 )
        break;

      int lo =-1,
          hi = 0;
      // GALLOPING PHASE
      while( hi < bLen && compare(a,a0, b,b0+hi) > cmp ) {
        lo = hi;
        hi = hi*2 + 1;
      } hi = min(hi,bLen) - 1;
      // BINARY SEARCH PHASE
           ++lo;
      while( lo <= hi ) {                    int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) > cmp ) lo = mid + 1;
        else                                hi = mid - 1;
      }

      copyRange(b,b0, c,c0, lo);
      b0 += lo;
      c0 += lo; copy(a,a0++, c,c0++);
      aLen -= 1;
      bLen -= lo;
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void expMergeV3_R2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    checkArgs_mergeR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    a0 += aLen;
    b0 +=   bLen;
    c0 += aLen+bLen;

    for( int cmp = 1;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T   d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if( aLen <= 0 )
        break;

      --a0;
      --b0;
      --aLen;

      int lo =-1,
          hi = 0;
      // GALLOPING PHASE
      while( hi < bLen && compare(a,a0, b,b0-hi) < cmp ) {
        lo = hi;
        hi = hi*2 + 1;
      } hi = min(hi,bLen) - 1;
      // BINARY SEARCH PHASE
           ++lo;
      while( lo <= hi ) {                  int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0-mid) < cmp ) lo = mid + 1;
        else                              hi = mid - 1;
      }

      b0 -= lo-1;
      c0 -= lo; copyRange(b,b0, c,c0, lo);
      bLen -= lo; copy(a,a0, c,--c0);
    }

    copyRange(b,b0-bLen, c,c0-bLen, bLen);
  }
}
