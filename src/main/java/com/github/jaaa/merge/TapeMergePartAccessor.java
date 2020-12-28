package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import static java.lang.Math.max;
import static java.lang.Math.min;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;


public interface TapeMergePartAccessor<T> extends CompareRandomAccessor<T>
{
  public default void tapeMergePartL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartL2R(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0, cLen
    );
    for( int i=0,j=0,k=0; k < cLen; k++ )
      if( j >= bLen || i < aLen && compare(a,a0+i, b,b0+j) <= 0 )
           copy(a,a0+i++, c,c0+k);
      else copy(b,b0+j++, c,c0+k);
  }

  public default void tapeMergePartR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartR2L(
      this, a, a0, aLen,
            b, b0, bLen,
            c, c0, cLen
    );
    for( int i=aLen-1,
             j=bLen-1,
             k=cLen; k > 0; )
    {
      if( j < 0 || i >= 0 && compare(a,a0+i, b,b0+j) > 0 )
           copy(a,a0 + i--, c,c0 + --k);
      else copy(b,b0 + j--, c,c0 + --k);
    }
  }
}
