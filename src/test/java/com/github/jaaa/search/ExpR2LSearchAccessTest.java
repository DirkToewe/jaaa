package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccess;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class ExpR2LSearchAccessTest extends SearchAccessTestTemplate
{
  interface SrchAcc extends SearchAccess, ExpR2LSearchAccess
  {
    @Override default int search    ( int from, int until, int key ) { return expR2LSearch    (from,until, key); }
    @Override default int searchL   ( int from, int until, int key ) { return expR2LSearchL   (from,until, key); }
    @Override default int searchR   ( int from, int until, int key ) { return expR2LSearchR   (from,until, key); }
    @Override default int searchGap ( int from, int until, int key ) { return expR2LSearchGap (from,until, key); }
    @Override default int searchGapL( int from, int until, int key ) { return expR2LSearchGapL(from,until, key); }
    @Override default int searchGapR( int from, int until, int key ) { return expR2LSearchGapR(from,until, key); }
  }

  @Override public SrchAcc createAccess( CompareAccess acc ) { return acc::compare; }

  @Override public long comparisonLimit( int from, int until, int i )
  {
    return from==until ? 0 : 1 + 2L*log2Floor(until-i+1);
  }
}
