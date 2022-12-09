package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.search.ExpSearchAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static java.lang.Math.max;
import static java.lang.Math.min;


public interface RexMergePartAccessor<T> extends CompareRandomAccessor<T>,
                                                             ExpSearchAccessor<T>
{
  abstract class RecMergePartFn
  {
    public int c0, cLen;
    public RecMergePartFn( int _c0, int _cLen ) { c0 =_c0; cLen =_cLen; }
    public abstract void mergePart( int a0, int a1, int b0, int b1 );
  }

  default void rexMergePartL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartL2R(
      a, a0, aLen,
      b, b0, bLen,
      c, c0, cLen
    );

    new RecMergePartFn(c0,cLen) {
      @Override public final void mergePart( int a0, int a1, int b0, int b1 )
      {
        int aLen = a1 - a0, am = a0 + (aLen>>>1),
            bLen = b1 - b0, bm = b0 + (bLen>>>1);
        if( aLen <= bLen )
        {
          if( 0 == aLen ) { copyRange(b,b0, c,c0, bLen); c0 += bLen; cLen -= bLen; return; }
          bm = expSearchGapL(b,b0,b1,bm, a,am);
          mergePart(a0,am, b0,bm);
          if( cLen <= 0 ) return;
            --cLen;
          copy(a,am++, c, c0++);
        }
        else
        {
          if( 0 == bLen ) { copyRange(a,a0, c,c0, aLen); c0 += aLen; cLen -= aLen; return; }
          am = expSearchGapR(a,a0,a1,am, b,bm);
          mergePart(a0,am, b0,bm);
          if( cLen <= 0 ) return;
            --cLen;
          copy(b,bm++, c, c0++);
        }
        mergePart( am, min(a1,am+cLen),
                bm, min(b1,bm+cLen) );
      }
    }.mergePart(
      a0, a0+min(aLen,cLen),
      b0, b0+min(bLen,cLen)
    );
  }

  default void rexMergePartR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartR2L(
      a, a0, aLen,
      b, b0, bLen,
      c, c0, cLen
    );

    new RecMergePartFn(c0+cLen, cLen) {
      @Override public final void mergePart( int a0, int a1, int b0, int b1 )
      {
        int aLen = a1 - a0, am = a0 + (aLen>>>1),
            bLen = b1 - b0, bm = b0 + (bLen>>>1);
        if( aLen <= bLen )
        {
          if( 0 == aLen ) { copyRange(b,b0, c,c0-=bLen, bLen); cLen -= bLen; return; }
          bm = expSearchGapL(b,b0,b1,bm, a,am);
          mergePart(1+am,a1, bm,b1);
          if( cLen <= 0 ) return;
            --cLen;
          copy(a,am, c,--c0);
        }
        else
        {
          if( 0 == bLen ) { copyRange(a,a0, c,c0-=aLen, aLen); cLen -= aLen; return; }
          am = expSearchGapR(a,a0,a1,am, b,bm);
          mergePart(am,a1, 1+bm,b1);
          if( cLen <= 0 ) return;
            --cLen;
          copy(b,bm, c,--c0);
        }
        mergePart(
          max(a0, am-cLen), am,
          max(b0, bm-cLen), bm
        );
      }
    }.mergePart(
      a0+max(0,aLen-cLen), a0+aLen,
      b0+max(0,bLen-cLen), b0+bLen
    );
  }
}
