package com.github.jaaa.select;

import static java.lang.Math.min;
import static java.lang.Math.round;


public final class MergeSelect {
  public static long performance_worstCase( int sortSize, int restSize ) {
    if( sortSize  < 1 || restSize < 0 ) throw new IllegalArgumentException();
    if( sortSize == 1 ) return restSize;

    long n = sortSize,
         N = sortSize + (long) restSize,
      cost = 0;

    for( long i=1; i < N;) {
      i<<=1;
      long   g = min(i,n),    // <- merge length
        nMerge = (N-1+i) / i; // <- number of merges
      cost += g * (nMerge-1);
      cost += min(g, N - (nMerge-1)*i); // <- last merge
      cost -= nMerge; // last item of every merge needs no comparison
    }

    return round(cost);
  }

  private MergeSelect() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
