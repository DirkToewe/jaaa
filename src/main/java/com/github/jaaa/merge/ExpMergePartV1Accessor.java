package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static java.lang.Math.min;


public interface ExpMergePartV1Accessor<T> extends CompareRandomAccessor<T>
{
  default void expMergePartV1_L2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  )
  {
    checkArgs_mergePartL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    for( int cmp = 1;; )
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

      int lo = 0,
          hi = bLen-1;
      // galloping phase
      for( int step=0; step <= hi-lo; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
      {
        int                    next = lo+step;
        if( compare(a,a0, b,b0+next) < cmp ) {
                     hi = -1 + next; break;
        }            lo =  1 + next;
      }
      // binary search phase
      while( lo <= hi ){ int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) < cmp ) hi = -1 + mid;
        else                                lo =  1 + mid;
      }

      copyRange(b,b0, c,c0, lo);
         cLen -= lo;
      if(cLen <= 0) return;
         cLen -= 1;
      aLen -= 1;
      bLen -= lo;
      b0 += lo;
      c0 += lo; copy(a,a0++, c,c0++);
    }

    copyRange(b,b0, c,c0, cLen);
  }


  default void expMergePartV1_R2L(
    T a,       int a0, int aLen,
    T b,       int b0, int bLen,
    T c, final int c0, int cLen
  )
  {
    checkArgs_mergePartR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0,cLen
    );

    a0 += aLen;
    b0 += bLen;

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

      if(0>=aLen) break;
          --aLen;
      b0 -= bLen;
      a0 -= 1;

      int lo = 0,
          hi = bLen-1;
      // galloping phase
      for( int step=0; step <= hi-lo; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
      {
        int                    next = hi-step;
        if( compare(a,a0, b,b0+next) > cmp ) {
                     lo =  1 + next; break;
        }            hi = -1 + next;
      }
      // binary search phase
      while( lo <= hi ) {  int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) > cmp ) lo =  1 + mid;
        else                                hi = -1 + mid;
      }
            b0 += lo;
                  lo = bLen-lo;
          cLen -= lo; copyRange(b,b0, c,c0+cLen, lo);
      if( cLen <= 0 ) return;
          cLen -= 1;
          bLen -= lo;
      copy(a,a0, c,c0+cLen);
    }

    copyRange(b,b0-cLen, c,c0, cLen);
  }
}
