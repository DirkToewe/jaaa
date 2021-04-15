package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.util.Objects.requireNonNull;

public class GallopL2RSearchAccessorTest extends SearchAccessorTestTemplate
{
  private static final class ExpSearchAccessor<T> implements SearchAccessor<T>,
                                                    GallopL2RSearchAccessor<T>
  {
    private final CompareAccessor<? super T> compareAccessor;

    public ExpSearchAccessor( CompareAccessor<? super T> _compareAccessor )
    {
      compareAccessor = requireNonNull(_compareAccessor);
    }

    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }

    @Override public int search    ( T a, int from, int until, T b, int i ) { return gallopL2RSearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return gallopL2RSearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return gallopL2RSearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return gallopL2RSearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return gallopL2RSearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return gallopL2RSearchGapL(a,from,until, b,i); }
  }
  @Override protected <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new ExpSearchAccessor<>(cmpAcc); }
  @Override protected long comparisonLimit( int from, int until, int i ) {
    if(from==until) return 0;
    i-=from;
    if( i < 2 ) return i+1;
    return 2 + 2*log2Floor(i);
  }
}
