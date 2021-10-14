package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class BinarySearchAccessTest extends SearchAccessTestTemplate
{
  interface SrchAcc extends SearchAccess, BinarySearchAccess
  {
    @Override default int search    ( int from, int until, int key ) { return binarySearch    (from,until, key); }
    @Override default int searchL   ( int from, int until, int key ) { return binarySearchL   (from,until, key); }
    @Override default int searchR   ( int from, int until, int key ) { return binarySearchR   (from,until, key); }
    @Override default int searchGap ( int from, int until, int key ) { return binarySearchGap (from,until, key); }
    @Override default int searchGapL( int from, int until, int key ) { return binarySearchGapL(from,until, key); }
    @Override default int searchGapR( int from, int until, int key ) { return binarySearchGapR(from,until, key); }
  }

  @Override public SrchAcc createAccess( CompareAccess acc ) { return acc::compare; }

  @Override public long comparisonLimit( int from, int until, int i )
  {
    return from==until ? 0 : 1 + log2Floor(until-from);
  }
}
