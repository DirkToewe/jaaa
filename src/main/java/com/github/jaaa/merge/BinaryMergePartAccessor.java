package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static java.lang.Math.min;


public interface BinaryMergePartAccessor<T> extends CompareRandomAccessor<T>
{
  default void binaryMergePartL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    checkArgs_mergePartL2R(
      this,a,a0,aLen,
           b,b0,bLen,
           c,c0,cLen
    );

    for( int cmp = 1;; )
    {
      aLen = min(aLen,cLen);
      bLen = min(bLen,cLen);

      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T   d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if( aLen <= 0 ) break;

      int lo = 0,
          hi = bLen-1;
      while( lo <= hi ){ int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) < cmp ) hi = -1 + mid;
        else                                lo = +1 + mid;
      }

      copyRange(b,b0, c,c0, lo);

          cLen -= lo;
      if( cLen <= 0 ) return;
          cLen -= 1;

      b0 += lo;
      c0 += lo; copy(a,a0++, c,c0++);
      aLen -= 1;
      bLen -= lo;
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void binaryMergePartR2L(
    T a,       int a0, int aLen,
    T b,       int b0, int bLen,
    T c, final int c0, int cLen
  )
  {
    checkArgs_mergePartR2L(
      this,a,a0,aLen,
           b,b0,bLen,
           c,c0,cLen
    );

    a0 += aLen;
    b0 += bLen;

    for( int cmp = 1;; )
    {
      aLen = min(aLen,cLen);
      bLen = min(bLen,cLen);

      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T   d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if(0>=aLen) break;
          --aLen;
      b0 -= bLen;
      a0 -= 1;

      int lo = 0,
          hi = bLen-1;
      while( lo <= hi ) {  int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) < cmp ) hi = -1 + mid;
        else                                lo = +1 + mid;
      }
            b0 += lo;
                  lo = bLen-lo;
          cLen -= lo; copyRange(b,b0, c,c0+cLen, lo);
          bLen -= lo;
      if( cLen <= 0 ) return;
          cLen -= 1;
      copy(a,a0, c,c0+cLen);
    }

    copyRange(b,b0-bLen, c,c0, bLen);
  }
}
