package com.github.jaaa.merge;

import com.github.jaaa.CompareAccessor;
import static java.lang.Math.max;
import static java.lang.Math.min;

import static com.github.jaaa.merge.CheckArgsMerge.checkArgs_mergeOffset;


public interface BinaryMergeOffsetAccessor<T> extends CompareAccessor<T>
{
  default int binaryMergeOffset( T a, int a0, int aLen,
                                 T b, int b0, int bLen, int nSkip )
  {
    checkArgs_mergeOffset(a0,aLen,
                          b0,bLen,nSkip);

         int lo = max(nSkip-bLen, 0);
    for( int hi = min(nSkip,aLen) - 1, lMin=lo; lo <= hi; ) {
      int l = lo+hi >>> 1,
          r = nSkip - l;
           if(             compare(a,a0+l,   b,b0+r-1) <= 0 ) lo = l+1;
      else if( lMin < l && compare(a,a0+l-1, b,b0+r  ) >  0 ) hi = l-1;
      else return l;
    }

    return lo;
  }
}
