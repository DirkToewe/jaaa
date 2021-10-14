package com.github.jaaa.search;

import net.jqwik.api.Group;

import static com.github.jaaa.search.BinarySearch.BINARY_SEARCHER;
import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class BinarySearchTest extends SearcherTestTemplate
{
  BinarySearchTest() {
    super(new StaticMethodsSearcher(BinarySearch.class));
  }

  private static long compLim( int from, int until, int i ) { return from==until ? 0 : 1 + log2Floor(until-from); }

  @Group
  class SearcherTest extends SearcherTestTemplate
  {
    SearcherTest() {
      super(BINARY_SEARCHER);
    }

    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
  }

  @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(from,until, i); }
}
