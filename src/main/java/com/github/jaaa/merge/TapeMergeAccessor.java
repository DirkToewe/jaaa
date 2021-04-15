package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


//  IMPLEMENTATION DETAILS
//  ----------------------
//  The tape merge algorithm, a.k.a. linear merge, is the simplest, most straight-forward
//  implementation of a merge algorithm. It repeatedly pulls the smaller of the heads of
//  two sequences to be merged.
//
//  Despite its simplicity, the tape merge algorithm outperforms any other merging algorithm
//  when it comes to merging random sequences of equal length using a reasonably fast comparator.
//  The main reason for the good performance is the cache and branch prediction friendliness of
//  the algorithm on modern CPUs.
//

public interface TapeMergeAccessor<T> extends CompareRandomAccessor<T>
{
  default void tapeMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      tapeMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      tapeMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }

  default void tapeMergeL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeL2R(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    aLen += a0;
    bLen += b0;

    while( a0 < aLen && b0 < bLen ) {
      if( compare(a,a0, b,b0) <= 0 ) copy(a,a0++, c,c0++);
      else                           copy(b,b0++, c,c0++);
    }
    if( a0 < aLen ) copyRange(a,a0, c,c0, aLen-a0);
    else            copyRange(b,b0, c,c0, bLen-b0);
  }

  default void tapeMergeR2L(
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
    aLen += a0;
    bLen += b0;

    while( aLen > a0 && bLen > b0 )
      if( compare(a,aLen-1, b,bLen-1) > 0 ) copy(a,--aLen, c,--c0);
      else                                  copy(b,--bLen, c,--c0);

    if( 0 < (aLen-=a0) ) { copyRange(a,a0, c,c0-aLen, aLen); }
    else {   bLen-=b0;     copyRange(b,b0, c,c0-bLen, bLen); }
  }
}
