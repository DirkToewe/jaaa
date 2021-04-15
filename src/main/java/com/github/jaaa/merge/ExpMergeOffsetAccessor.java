package com.github.jaaa.merge;

import com.github.jaaa.CompareAccessor;
import com.github.jaaa.search.ExpSearch;

import static java.lang.Math.max;
import static java.lang.Math.min;

public interface ExpMergeOffsetAccessor<T> extends CompareAccessor<T>
{
  default int expMergeOffset( T a, int a0, int aLen,
                              T b, int b0, int bLen, int nSkip )
  {
    if( aLen  < 0 ) throw new IllegalArgumentException();
    if( bLen  < 0 ) throw new IllegalArgumentException();
    if( nSkip < 0 ) throw new IllegalArgumentException();
    if( aLen < nSkip-bLen ) throw new IllegalArgumentException();
    if( aLen == 0 ) return 0;

    int lo = max(nSkip-bLen, 0),
        hi = min(nSkip,aLen),          cLen = aLen+bLen,
        l0 = (int) ( (long) nSkip*aLen/cLen );

    return ExpSearch.searchGap( lo,hi, l0, l -> { int r = nSkip - l;
      return      compare(a,a0+l,   b,b0+r-1) <= 0 ? +1 :
        lo < l && compare(a,a0+l-1, b,b0+r  ) >  0 ? -1 : 0;
    });
  }
}
