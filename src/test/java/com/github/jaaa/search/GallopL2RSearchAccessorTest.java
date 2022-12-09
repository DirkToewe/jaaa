package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class GallopL2RSearchAccessorTest extends SearchAccessorTestTemplate
{
  private record ExpSearchAccessor<T>( CompareAccessor<? super T> compareAccessor ) implements SearchAccessor<T>,
                                                                                                   GallopL2RSearchAccessor<T>
  {
    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }
    @Override public int search    ( T a, int from, int until, T b, int i ) { return gallopL2RSearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return gallopL2RSearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return gallopL2RSearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return gallopL2RSearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return gallopL2RSearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return gallopL2RSearchGapL(a,from,until, b,i); }
  }
  @Override public <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new ExpSearchAccessor<>(cmpAcc); }
  @Override public long comparisonLimit( int from, int until, int i ) {
    if(from==until) return 0;
    i-=from;
    if( i < 2 ) return i+1;
    return 2 + 2L*log2Floor(i);
  }
}
