package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;

//  Implementation Details
//  ----------------------
//  ZenMerge is a recursive merging algorithm, similar to RecMerge.
//
//  RecMerge is only guaranteed to split smaller of the two sequences
//  in half during each step of recursion. The larger might remain
//  entirely unsplit. As a result, one of the two recursive sub-problems
//  may end up significantly larger than the other. When it comes to
//  single-threaded implementations, this imbalance is not a problem.
//  On the contrary, it can help to identify long sub-sequences from
//  either one of the merged sequences that are sequential int the output
//  as well. If, however, fork-join parallelism is involved, the uneven
//  distribution of the computational task may come at the cost of an
//  uneven CPU utilization.
//
//  ZenMerge splits the merging problem into two equally large sub-problems
//  during each step of the recursion. The BinaryMergeOffset search algorithm
//  is used to perform the splitting.

public interface ZenMergeAccessor<T> extends CompareRandomAccessor<T>,
                                                 BinaryMergeOffsetAccessor<T>
{
  static abstract class RecMergeFn
  {
    public int k;
    public RecMergeFn( int _k ) { k =_k; }
    public abstract void merge( int a0, int a1, int b0, int b1 );
  }

  public default void zenMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      zenMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      zenMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }

  public default void zenMergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    new RecMergeFn(c0) {
      @Override public final void merge( int a0, int aLen, int b0, int bLen )
      {
             if( 0==aLen ) { copyRange(b,b0, c,k, bLen); k+=bLen; }
        else if( 0==bLen ) { copyRange(a,a0, c,k, aLen); k+=aLen; }
        else { int                                     nSkip = aLen+bLen >>> 1,
          nA = binaryMergeOffset(a,a0,aLen, b,b0,bLen, nSkip),
          nB = nSkip - nA;
          merge(a0,        nA, b0,        nB);
          merge(a0+nA,aLen-nA, b0+nB,bLen-nB);
        }
      }
    }.merge(a0,aLen, b0,bLen);
  }

  public default void zenMergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    c0 += aLen+bLen;

    new RecMergeFn(c0) {
      @Override public final void merge( int a0, int aLen, int b0, int bLen )
      {
             if( 0==aLen ) copyRange(b,b0, c,k-=bLen, bLen);
        else if( 0==bLen ) copyRange(a,a0, c,k-=aLen, aLen);
        else { int                                     nSkip = aLen+bLen >>> 1,
          nA = binaryMergeOffset(a,a0,aLen, b,b0,bLen, nSkip),
          nB = nSkip - nA;
          merge(a0+nA,aLen-nA, b0+nB,bLen-nB);
          merge(a0,        nA, b0,        nB);
        }
      }
    }.merge(a0,aLen, b0,bLen);
  }
}
