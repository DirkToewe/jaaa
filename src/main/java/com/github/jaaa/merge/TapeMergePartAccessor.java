package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartL2R;
import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergePartR2L;


public interface TapeMergePartAccessor<T> extends CompareRandomAccessor<T>
{
  default void tapeMergePartL2R(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartL2R(
      a, a0, aLen,
      b, b0, bLen,
      c, c0, cLen
    );
    aLen += a0;
    bLen += b0;
    cLen += c0;
    for(; c0 <  cLen; c0++ ) {
      if( b0 >= bLen || a0 < aLen && compare(a,a0, b,b0) <= 0 )
           copy(a,a0++, c,c0);
      else copy(b,b0++, c,c0);
    }
  }

  default void tapeMergePartR2L(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, int cLen
  ) {
    checkArgs_mergePartR2L(
      a, a0, aLen,
      b, b0, bLen,
      c, c0, cLen
    );
    for( int i = a0 + aLen-1,
             j = b0 + bLen-1,
             k = c0 + cLen; k-- > c0; )
    {
      if( j < b0 || i >= a0 && compare(a,i, b,j) > 0 )
           copy(a,i--, c,k);
      else copy(b,j--, c,k);
    }
  }
}
