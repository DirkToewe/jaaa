package com.github.jaaa.search;

import net.jqwik.api.Group;

import static com.github.jaaa.search.ExpR2LSearch.EXP_R2L_SEARCHER;
import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class ExpR2LSearchTest extends SearcherTestTemplate
{
  ExpR2LSearchTest() {
    super(new StaticMethodsSearcher(ExpR2LSearch.class));
  }

  private static long compLim( int from, int until, int i ) { return from==until ? 0 : 1 + 2L*log2Floor(until-i+1); }

  @Group
  class SearcherTest extends SearcherTestTemplate
  {
    SearcherTest() { super(EXP_R2L_SEARCHER); }

    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
  }

  @Override
  protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
}
