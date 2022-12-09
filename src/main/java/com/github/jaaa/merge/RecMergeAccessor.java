package com.github.jaaa.merge;

import com.github.jaaa.search.BinarySearchAccessor;
import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeR2L;


//  REFERENCES
//  ----------
//  .. [1] "On a Stable Minimum Storage Merging Algorithm"
//          Kreysztof Dudzińki & Andrzej Dydek
//  .. [2] https://rklaehn.github.io/2016/01/05/binarymerge/
//
//  Implementation Details
//  ----------------------
//  RecMerge is a recursive merging algorithm, inspired by [1]. It
//  recursively splits the smaller of the two merged sequences in
//  half. Via binary search, the insertion point of the mid value
//  from the halved sequence is searched into the other sequence.
//  Thus the larger sequence is split as well, and we have two smaller
//  merging problems. The mid value of the smaller sequence can already
//  be moved to its final position thanks to the binary search.
//

public interface RecMergeAccessor<T> extends CompareRandomAccessor<T>,
                                              BinarySearchAccessor<T>
{
  abstract class RecMergeFn
  {
    public int c0;
    public RecMergeFn( int _c0 ) { c0 =_c0; }
    public abstract void merge( int a0, int a1, int b0, int b1 );
  }

  default void recMerge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    if(  a==c && c0 < a0+aLen && a0 <= c0
      || b==c && c0 < b0+bLen && b0 <= c0 )
      recMergeR2L(a,a0,aLen, b,b0,bLen, c,c0); // <- depending on overlap, merge from right to left
    else
      recMergeL2R(a,a0,aLen, b,b0,bLen, c,c0);
  }

  default void recMergeL2R(
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
      @Override public void merge( int a0, final int a1, int b0, final int b1 )
      {
        int am = a1 - a0,
            bm = b1 - b0;
        if( am <= bm )
        {
          if( a0 == a1 ) { copyRange(b,b0, c, c0, bm); c0 += bm; return; }
          am = a0 + (am>>>1);
          bm = binarySearchGapL(b,b0,b1, a,am);
          merge(a0,am, b0,bm);
          copy(a,am++, c,c0++);
        }
        else
        {
          if( b0 == b1 ) { copyRange(a,a0, c, c0, am); c0 += am; return; }
          bm = b0 + (bm>>>1);
          am = binarySearchGapR(a,a0,a1, b,bm);
          merge(a0,am, b0,bm);
          copy(b,bm++, c,c0++);
        }
        merge(am,a1, bm,b1);
      }
    }.merge(a0,a0+aLen, b0,b0+bLen);
  }

  default void recMergeR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0
  ) {
    checkArgs_mergeR2L(
      a,a0,aLen,
      b,b0,bLen,
      c,c0
    );

    new RecMergeFn(c0+aLen+bLen) {
      @Override public void merge( final int a0, int a1, final int b0, int b1 )
      {
        int am = a1 - a0,
            bm = b1 - b0;
        if( am <= bm )
        {
          if( a0 == a1 ) { copyRange(b,b0, c, c0 -=bm, bm); return; }
          am = a0 + (am>>>1);
          bm = binarySearchGapL(b,b0,b1, a,am);
          merge(1+am,a1, bm,b1);
          copy(a,am, c,--c0);
        }
        else
        {
          if( b0 == b1 ) { copyRange(a,a0, c, c0 -=am, am); return; }
          bm = b0 + (bm>>>1);
          am = binarySearchGapR(a,a0,a1, b,bm);
          merge(am,a1, 1+bm,b1);
          copy(b,bm, c,--c0);
        }
        merge(a0,am, b0,bm);
      }
    }.merge(a0,a0+aLen, b0,b0+bLen);
  }
}
