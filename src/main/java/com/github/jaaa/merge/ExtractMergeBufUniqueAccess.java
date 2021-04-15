package com.github.jaaa.merge;

import com.github.jaaa.misc.RotateAccess;
import com.github.jaaa.search.ExpL2RSearchAccess;
import com.github.jaaa.search.ExpR2LSearchAccess;

public interface ExtractMergeBufUniqueAccess extends RotateAccess,
                                               ExpL2RSearchAccess,
                                               ExpR2LSearchAccess
{
  default int extractMergeBufUniqueMin( int from, int until, int bufLen )
  {
    if( from > until) throw new IllegalArgumentException();
    if( from   <  0 ) throw new IllegalArgumentException();
    if( bufLen <  0 ) throw new IllegalArgumentException();
    if( bufLen == 0 ) return 0;

    int i=from,
        n=1;
    for(; n < bufLen; n++ ) {
      int j = expL2RSearchGapR(i+1,until, i);
      if( j >= until ) break;
      rotate(i+1-n, i=j, -n);
    }
    rotate(from,i+1,n);
    return n;
  }

  default int extractMergeBufUniqueMax( int from, int until, int bufLen )
  {
    if( from > until) throw new IllegalArgumentException();
    if( from   <  0 ) throw new IllegalArgumentException();
    if( bufLen <  0 ) throw new IllegalArgumentException();
    if( bufLen == 0 ) return 0;

    int i=until-1,
        n=1;
    for(; n < bufLen; n++ ) {
      int j = expR2LSearchGapL(from,i,i);
      if( j <= from ) break;
      rotate(j,i+n, n);
      i=j-1;
    }
    rotate(i,until, -n);
    return n;
  }
}
