package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

import static com.github.jaaa.util.IMath.log2Floor;

public class ExpR2LSearchAccessTest extends SearchAccessTestTemplate
{
  private static final class SearchAccessWrapper implements ExpR2LSearchAccess
  {
    private final CompareAccess access;

    private SearchAccessWrapper( CompareAccess _access ) { access =_access; }

    @Override public int compare( int i, int j ) { return access.compare(i,j); }
  }

  @Override protected int search    ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearch    (from,until, key); }
  @Override protected int searchR   ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearchR   (from,until, key); }
  @Override protected int searchL   ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearchL   (from,until, key); }
  @Override protected int searchGap ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearchGap (from,until, key); }
  @Override protected int searchGapR( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearchGapR(from,until, key); }
  @Override protected int searchGapL( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expR2LSearchGapL(from,until, key); }

  @Override protected long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 1 + 2*log2Floor(until-i+1); }
}
