package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Math.min;


@Group
public class AkimboSearchAccessTest extends SearchAccessTestTemplate
{
  interface SrchAcc extends SearchAccess, AkimboSearchAccess
  {
    @Override default int search    ( int from, int until, int key ) { return akimboSearch    (from,until, key); }
    @Override default int searchL   ( int from, int until, int key ) { return akimboSearchL   (from,until, key); }
    @Override default int searchR   ( int from, int until, int key ) { return akimboSearchR   (from,until, key); }
    @Override default int searchGap ( int from, int until, int key ) { return akimboSearchGap (from,until, key); }
    @Override default int searchGapL( int from, int until, int key ) { return akimboSearchGapL(from,until, key); }
    @Override default int searchGapR( int from, int until, int key ) { return akimboSearchGapR(from,until, key); }
  }

  @Override public SrchAcc createAccess( CompareAccess acc ) { return acc::compare; }

  @Override public long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 2 + 2L*log2Floor( 1 + min(until-i,i-from) ); }
}
