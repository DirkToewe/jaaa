package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;
import static java.lang.Math.min;

// Tries to combat the same degenerate cases as V2, but instead of
// some clever solution, A and B are just swapped constantly.
public interface ExpMergeV4Accessor<T> extends CompareRandomAccessor<T>
{
  public default void expMergeV4(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      expMergeV4_R2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      expMergeV4_L2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  public default void expMergeV4_L2R(
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

    for( int cmp=1; bLen > 0; )
    {
      // SWAP
      cmp ^= 1;
      T   d=a; a=b; b=d;
      int l=a0; a0=b0; b0=l;
      int o=aLen; aLen=bLen; bLen=o;

      int lo = 0,
          hi = bLen;
      // GALLOPING PHASE
      for( int step=0; step < hi-lo; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
      {
        int                    next = lo+step;
        if( compare(a,a0, b,b0+next) < cmp ) {
                          hi = next; break;
        }                 lo = next+1;
      }

      // BINARY SEARCH PHASE
      while( lo < hi ) {   int mid = lo+hi >>> 1;
        if( compare(a,a0, b,b0+mid) < cmp )
                          hi = mid;
        else              lo = mid+1;
      }

      copyRange(b,b0, c,c0, lo);
      b0 += lo;
      c0 += lo; copy(a,a0++, c,c0++);
      aLen -= 1;
      bLen -= lo;
    }

    copyRange(a,a0, c,c0, aLen);
  }


  public default void expMergeV4_R2L(
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

    for( int cmp=0; bLen > 0; )
    {
      // SWAP
      T   d=a; a=b; b=d;
      int l=a0; a0=b0; b0=l;
      int o=aLen; aLen=bLen; bLen=o; cmp ^= -1;

      --aLen;

      int lo = 0,
          hi = bLen;
      // galloping phase
      for( int step=1; step < hi-lo; step<<=1 )  // <- make step have all bits set such that binary search is optimally efficient
      {
        int                         next = hi-step;
        if( compare(a,a0+aLen, b,b0+next) > cmp ) {
                               lo = next+1; break;
        }                      hi = next;
      }
      // binary search phase
      while( lo < hi ) {        int mid = lo+hi >>> 1;
        if( compare(a,a0+aLen, b,b0+mid) > cmp )
                               lo = mid+1;
        else                   hi = mid;
      }

      bLen -= lo = bLen-lo;
      copyRange(b,b0+bLen, c,c0-=lo, lo);
      copy(a,a0+aLen, c,--c0);
    }

    copyRange(a,a0, c,c0-aLen, aLen);
  }
}
