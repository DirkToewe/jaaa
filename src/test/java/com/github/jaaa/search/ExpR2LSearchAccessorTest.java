package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.util.Objects.requireNonNull;

public class ExpR2LSearchAccessorTest extends SearchAccessorTestTemplate
{
  private static final class ExpSearchAccessor<T> implements SearchAccessor<T>,
                                                       ExpR2LSearchAccessor<T>
  {
    private final CompareAccessor<? super T> compareAccessor;

    public ExpSearchAccessor( CompareAccessor<? super T> _compareAccessor )
    {
      compareAccessor = requireNonNull(_compareAccessor);
    }

    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }

    @Override public int search    ( T a, int from, int until, T b, int i ) { return expR2LSearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return expR2LSearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return expR2LSearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return expR2LSearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return expR2LSearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return expR2LSearchGapL(a,from,until, b,i); }
  }
  @Override protected <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new ExpSearchAccessor<>(cmpAcc); }
  @Override protected long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 1 + 2*log2Floor(until-i+1); }
}
