package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


//  IMPLEMENTATION DETAILS
//  ----------------------
//  BinaryMerge is a simple straight-forward merge algorithm based on binary search.
//  It particularly excels, when one of the two sequences only contains a hand full
//  of elements. The algorithm takes the first element from

public interface BinaryMergeAccessor<T> extends CompareRandomAccessor<T>
{
  default void binaryMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
         binaryMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else binaryMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  default void binaryMergeL2R(
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

    for( int cmp = 1;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if( aLen <= 0 ) break;

      int lo = 0,
          hi = bLen-1;
      while( lo <= hi ){ int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) < cmp ) hi = -1 + mid;
        else                                lo =  1 + mid;
      }

      copyRange(b,b0, c,c0, lo);
        b0 += lo;
        c0 += lo; copy(a,a0++, c,c0++);
      bLen -= lo;
      aLen -= 1;
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void binaryMergeR2L(
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

    c0 += aLen+bLen;

    for( int cmp = 1;; )
    {
      // make sure that `a` is always shorter
      if( aLen > bLen ) {
        T d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= 1;
      }

      if( aLen <= 0 ) break;
        --aLen;

      int lo = 0,
          hi = bLen-1;
      while( lo <= hi ) {  int mid = lo+hi >>> 1;
        if( compare(a,a0+aLen, b,b0+mid) < cmp ) hi = -1 + mid;
        else                                     lo =  1 + mid;
      }

      bLen -= lo = bLen-lo;
      copyRange(b,b0+bLen, c,c0-=lo, lo);
      copy(a,a0+aLen, c,--c0);
    }

    copyRange(b,b0, c,c0-bLen, bLen);
  }
}
