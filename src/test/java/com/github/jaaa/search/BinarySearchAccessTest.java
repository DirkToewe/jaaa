package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

import static com.github.jaaa.util.IMath.log2Floor;

public class BinarySearchAccessTest extends SearchAccessTestTemplate
{
  private static final class SearchAccessWrapper implements BinarySearchAccess
  {
    private final CompareAccess access;

    private SearchAccessWrapper( CompareAccess _access ) { access =_access; }

    @Override public int compare( int i, int j ) { return access.compare(i,j); }
  }

  @Override protected int searchGap ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).binarySearchGap (from,until, key); }
  @Override protected int searchGapR( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).binarySearchGapR(from,until, key); }
  @Override protected int searchGapL( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).binarySearchGapL(from,until, key); }

  @Override protected long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 1 + log2Floor(until-from); }
}
