package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.util.Objects.requireNonNull;

public class BinarySearchAccessorTest extends SearchAccessorTestTemplate
{
  private static final class BinSearchAccessor<T> implements SearchAccessor<T>,
                                                       BinarySearchAccessor<T>
  {
    private final CompareAccessor<? super T> compareAccessor;

    public BinSearchAccessor( CompareAccessor<? super T> _compareAccessor )
    {
      compareAccessor = requireNonNull(_compareAccessor);
    }

    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }

    @Override public int search    ( T a, int from, int until, T b, int i ) { return binarySearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return binarySearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return binarySearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return binarySearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return binarySearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return binarySearchGapL(a,from,until, b,i); }
  }
  @Override protected <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new BinSearchAccessor<>(cmpAcc); }
  @Override protected long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 1 + log2Floor(until-from); }
}
