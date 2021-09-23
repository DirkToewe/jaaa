package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

import static com.github.jaaa.util.IMath.log2Floor;


public class ExpL2RSearchAccessTest implements SearchAccessTestTemplate
{
  interface SrchAcc extends SearchAccess, ExpL2RSearchAccess
  {
    @Override default int search    ( int from, int until, int key ) { return expL2RSearch    (from,until, key); }
    @Override default int searchL   ( int from, int until, int key ) { return expL2RSearchL   (from,until, key); }
    @Override default int searchR   ( int from, int until, int key ) { return expL2RSearchR   (from,until, key); }
    @Override default int searchGap ( int from, int until, int key ) { return expL2RSearchGap (from,until, key); }
    @Override default int searchGapL( int from, int until, int key ) { return expL2RSearchGapL(from,until, key); }
    @Override default int searchGapR( int from, int until, int key ) { return expL2RSearchGapR(from,until, key); }
  }

  @Override public SrchAcc createAccess( CompareAccess acc ) { return acc::compare; }

  @Override public long comparisonLimit( int from, int until, int i )
  {
    return from==until ? 0 : 1 + 2*log2Floor(i-from+1);
  }
}
