package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

import static com.github.jaaa.util.IMath.log2Ceil;
import static com.github.jaaa.util.IMath.log2Floor;

public class ExpL2RSearchAccessTest extends SearchAccessTestTemplate
{
  private static final class SearchAccessWrapper implements ExpL2RSearchAccess
  {
    private final CompareAccess access;

    private SearchAccessWrapper( CompareAccess _access ) { access =_access; }

    @Override public int compare( int i, int j ) { return access.compare(i,j); }
  }

  @Override protected int searchGap ( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expL2RSearchGap (from,until, key); }
  @Override protected int searchGapR( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expL2RSearchGapR(from,until, key); }
  @Override protected int searchGapL( int from, int until, CompareAccess access, int key ) { return new SearchAccessWrapper(access).expL2RSearchGapL(from,until, key); }

  @Override protected long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 1 + 2*log2Floor(i-from+1); }
}
