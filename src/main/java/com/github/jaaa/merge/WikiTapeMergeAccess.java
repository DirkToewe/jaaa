package com.github.jaaa.merge;

import static com.github.jaaa.util.IMath.sqrtCeil;
import static java.lang.Math.min;


public interface WikiTapeMergeAccess extends ExtractMergeBufUniqueAccess
{
  default void wikiTapeMerge( int from, int mid, int until )
  {
    if( from <  0 ) throw new IllegalArgumentException();
    if( from > mid) throw new IllegalArgumentException();
    if(until < mid) throw new IllegalArgumentException();

    int nL = mid-from,
        nR = until-mid,
      nBuf = sqrtCeil( min(nL,nR) );



    throw new UnsupportedOperationException("Not yet implemented.");
  }
}
