package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;
import static java.lang.Math.min;

// Like V1 but trying to avoid degenerate cases where only elements from one side
// are drawn, at which point expMergeV1 essentially functions like a tapeMerge.
// V2 instead swaps A and B if too many elements have been drawn from A without
// taking any from B.
public interface ExpMergeAccessor<T> extends CompareRandomAccessor<T>
{
  default void expMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  )
  {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      expMerge_R2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      expMerge_L2R(a,a0,aLen, b,b0,bLen, c,c0);
  }


  default void expMerge_L2R(
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

    final int STUCK_LIMIT = 7;

    for( int cmp=1, stuckometer=0;; )
    {
      if( aLen > bLen || stuckometer >= STUCK_LIMIT ) {
        cmp ^= 1;        stuckometer = 0;
        T d=a; a=b; b=d;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o;
      }

      if( aLen <= 0 ) break;

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

      stuckometer = lo > 0 ? 0 : stuckometer+1;

      copyRange(b,b0, c,c0, lo);
      b0 += lo;
      c0 += lo; copy(a,a0++, c,c0++);
      aLen -= 1;
      bLen -= lo;
    }

    copyRange(b,b0, c,c0, bLen);
  }


  default void expMerge_R2L(
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

    final int STUCK_LIMIT = 7;

    c0 += aLen+bLen;

    for( int cmp=0, stuckometer=0;; )
    {
      if( aLen > bLen || stuckometer >= STUCK_LIMIT ) {
        T d=a; a=b; b=d; stuckometer = 0;
        int l=a0; a0=b0; b0=l;
        int o=aLen; aLen=bLen; bLen=o; cmp ^= -1;
      }

      if( aLen <= 0 ) break;
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

      stuckometer = lo > 0 ? 0 : stuckometer+1;

      copyRange(b,b0+bLen, c,c0-=lo, lo);
      copy(a,a0+aLen, c,--c0);
    }

    copyRange(b,b0, c,c0-bLen, bLen);
  }
}
