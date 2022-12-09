package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Math.min;


@Group
public class AkimboSearchAccessorTest extends SearchAccessorTestTemplate
{
  private record BinSearchAccessor<T>( CompareAccessor<? super T> compareAccessor ) implements SearchAccessor<T>,
                                                                                                AkimboSearchAccessor<T>
  {
    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }
    @Override public int search    ( T a, int from, int until, T b, int i ) { return akimboSearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return akimboSearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return akimboSearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return akimboSearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return akimboSearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return akimboSearchGapL(a,from,until, b,i); }
  }
  @Override public <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new BinSearchAccessor<>(cmpAcc); }
  @Override public long comparisonLimit( int from, int until, int i ) { return from==until ? 0 : 2 + 2L*log2Floor( 1 + min(until-i,i-from) ); }
}
