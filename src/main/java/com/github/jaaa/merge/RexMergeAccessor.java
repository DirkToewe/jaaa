package com.github.jaaa.merge;

import com.github.jaaa.search.ExpSearchAccessor;
import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


//  Implementation Details
//  ----------------------
//  RexMerge is the same as RecMerge, except the binary search is
//  replaced with exponential search. Hence the name "Rex" for recursive
//  and exponential.
//
//  Recursively splits the smaller of the two merged sequences roughly in
//  half. Via exponential search, the insertion point of the mid value
//  from the halved sequence is searched into the other sequence. Thus
//  the larger sequence is somewhat split as well, and we have two smaller
//  merging problems. The mid value of the smaller sequence can already
//  be moved to its final position thanks to the binary search.
//
//  For random inputs, each RexMerge should only take O(1) comparisons
//  instead RecMerge's O(log(n)) comparisons during each step of recursion.

public interface RexMergeAccessor<T> extends CompareRandomAccessor<T>,
                                                 ExpSearchAccessor<T>
{
  abstract class RexMergeFn
  {
    public int k;
    public RexMergeFn(int _k ) { k =_k; }
    public abstract void merge( int a0, int a1, int b0, int b1 );
  }

  default void rexMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      rexMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      rexMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }

  default void rexMergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    new RexMergeFn(c0) {
      @Override public void merge( int a0, int a1, int b0, int b1 )
      {
        int aLen = a1-a0, am = a0 + (aLen>>>1),
            bLen = b1-b0, bm = b0 + (bLen>>>1);
        if( aLen <= bLen ) {
          if( 0 == aLen ) { copyRange(b,b0, c,k, bLen); k += bLen; return; }
          bm = expSearchGapL(b,b0,b1,bm, a,am);
          merge(a0,am, b0,bm);
          copy(a,am++, c,k++);
        }
        else {
          if( 0 == bLen ) { copyRange(a,a0, c,k, aLen); k += aLen; return; }
          am = expSearchGapR(a,a0,a1,am, b,bm);
          merge(a0,am, b0,bm);
          copy(b,bm++, c,k++);
        }
        merge(am,a1, bm,b1);
      }
    }.merge(a0,a0+aLen, b0,b0+bLen);
  }

  default void rexMergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    new RexMergeFn(c0+aLen+bLen) {
      @Override public void merge( int a0, int a1, int b0, int b1 )
      {
        int aLen = a1-a0, am = a0 + (aLen>>>1),
            bLen = b1-b0, bm = b0 + (bLen>>>1);
        if( aLen <= bLen ) {
          if( 0 == aLen ) { k -= bLen; copyRange(b,b0, c,k, bLen); return; }
          bm = expSearchGapL(b,b0,b1,bm, a,am);
          merge(1+am,a1, bm,b1);    copy(a,am, c,--k);
        }
        else {
          if( 0 == bLen ) { k -= aLen; copyRange(a,a0, c,k, aLen); return; }
          am = expSearchGapR(a,a0,a1,am, b,bm);
          merge(am,a1, 1+bm,b1);    copy(b,bm, c,--k);
        }
        merge(a0,am, b0,bm);
      }
    }.merge(a0,a0+aLen, b0,b0+bLen);
  }
}
