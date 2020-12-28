package com.github.jaaa.search;

import net.jqwik.api.Group;

import static com.github.jaaa.search.ExpL2RSearch.EXP_L2R_SEARCHER;
import static com.github.jaaa.util.IMath.log2Floor;

public class ExpL2RSearchTest extends SearcherTestTemplate
{
  ExpL2RSearchTest() {
    super(new StaticMethodsSearcher(ExpL2RSearch.class));
  }

  private static long compLim( int from, int until, int i ) { return from==until ? 0 : 1 + 2*log2Floor(i-from+1); }

  @Group
  class SearcherTest extends SearcherTestTemplate
  {
    SearcherTest() {
      super(EXP_L2R_SEARCHER);
    }

    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
  }

  @Override
  protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
}
