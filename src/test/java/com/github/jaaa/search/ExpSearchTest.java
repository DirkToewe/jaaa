package com.github.jaaa.search;

import net.jqwik.api.Group;

import java.util.Objects;

import static com.github.jaaa.search.ExpSearch.ExpSearcher;
import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Integer.toUnsignedLong;
import static java.lang.Math.abs;

@Group
public class ExpSearchTest
{
  private static long compLim( ExpSearcher SEARCHER, int from, int until, int i ) {
    if( from == until ) return 0;
                           i = abs( SEARCHER.startIndex(from,until) - i );
    return 2 + 2*log2Floor(i+1);
  }

  static final ExpSearcher
    SEARCHER_X = (i,j) -> (int) ( i-1L + toUnsignedLong( Objects.hash(i,j) ) % (2L+j-i) ),
    SEARCHER_L = (i,j) -> i,
    SEARCHER_M = (i,j) -> i + (j-i >>> 1),
    SEARCHER_R = (i,j) -> j-1;

  @Group class SearcherX extends SearcherTestTemplate {
    SearcherX() { super(SEARCHER_X); }
    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(SEARCHER_X, from,until, i); }
  }

  @Group class SearcherL extends SearcherTestTemplate {
    SearcherL() { super(SEARCHER_L); }
    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(SEARCHER_L, from,until, i); }
  }

  @Group class SearcherM extends SearcherTestTemplate {
    SearcherM() { super(SEARCHER_M); }
    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(SEARCHER_M, from,until, i); }
  }

  @Group class SearcherR extends SearcherTestTemplate {
    SearcherR() { super(SEARCHER_R); }
    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(SEARCHER_R, from,until, i); }
  }
}
