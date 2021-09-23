package com.github.jaaa.search;

import net.jqwik.api.Group;

import static com.github.jaaa.search.AkimboSearch.AKIMBO_SEARCHER;
import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Math.min;


public class AkimboSearchTest extends SearcherTestTemplate
{
  AkimboSearchTest() {
    super(new StaticMethodsSearcher(AkimboSearch.class));
  }

  private static long compLim( int from, int until, int i ) { return from==until ? 0 : 2 + 2*log2Floor( 1 + min(until-i,i-from) ); }

  @Group
  class SearcherTest extends SearcherTestTemplate
  {
    SearcherTest() {
      super(AKIMBO_SEARCHER);
    }

    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
  }

  @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
}
