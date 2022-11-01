package com.github.jaaa.sort;

import com.github.jaaa.merge.KiwiMergeAccess;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;


// KiwiSort is closely related to WikiSort.
public interface KiwiSortV1Access extends ExtractSortBufOrdinalAccess, InsertionSortAccess, KiwiMergeAccess
{
  int MIN_RUN_LEN = 16;

  static int minBufLen( int len )
  {
    if( len < 0 ) throw new IllegalArgumentException();
    int bufLen = (int) ceil( ( sqrt(len*8d + 1) - 5 ) / 4 );
    int l = len-bufLen;
        l -= l+1>>>1;
    if(1 > bufLen || l/bufLen > bufLen )
         ++bufLen;
    return bufLen;
  }

  default  int kiwiSortV1_runLen( int n ) { return TimSort.optimalRunLength(MIN_RUN_LEN,n); }
  default void kiwiSortV1_sortRun( int from, int until )
  {
    insertionSort(from,until);
  }
  default void kiwiSortV1( int from, int until )
  {
    if( from < 0 || from >  until ) throw new IllegalArgumentException();
    if( from >= until-MIN_RUN_LEN*3 ) {
      kiwiSortV1_sortRun(from,until);
      return;
    }

    int         len = until-from,
      desiredBufLen = minBufLen(len),
             bufLen = extractSortBuf_ordinal_l_sorted(from,until, desiredBufLen, from),
             buf    = from;
     from += bufLen;
      len -= bufLen;

    int n = kiwiSortV1_runLen(len);

    // SORT
    // ----
    for( int l = from, r = l + len%n;; )
    {
      kiwiSortV1_sortRun(l,r);
      if( r==until ) break;
      l = r;
      r+= n;
    }

    // MERGE
    // -----
    for( int N; n < len; n=N )
    {
      if((N = 2*n) < 0 ) // <- avoid underflow
          N = Integer.MAX_VALUE;

      int l = from,
          m = from + len%n,
          r = from + len%N;

      for(;; r = n + ( m = n + (l=r) ) ) {
        kiwiMergeL2R_usingBuffer(l,m,r, buf,bufLen);
        if( r==until ) break;
      }
    }

    kiwiMerge_mergeInPlace(buf,from,until);
  }
}
