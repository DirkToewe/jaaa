package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;
import net.jqwik.api.Group;

import static com.github.jaaa.util.IMath.log2Floor;


@Group
public class GallopR2LSearchAccessorTest extends SearchAccessorTestTemplate
{
  private record ExpSearchAccessor<T>( CompareAccessor<? super T> compareAccessor ) implements SearchAccessor<T>,
                                                                                                   GallopR2LSearchAccessor<T>
  {
    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }
    @Override public int search    ( T a, int from, int until, T b, int i ) { return gallopR2LSearch    (a,from,until, b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return gallopR2LSearchR   (a,from,until, b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return gallopR2LSearchL   (a,from,until, b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return gallopR2LSearchGap (a,from,until, b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return gallopR2LSearchGapR(a,from,until, b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return gallopR2LSearchGapL(a,from,until, b,i); }
  }
  @Override public <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new ExpSearchAccessor<>(cmpAcc); }
  @Override public long comparisonLimit( int from, int until, int i ) {
    if( until == from ) return 0;
    i = until-i;
    if( i < 2 ) return i+1;
    return 2 + 2L*log2Floor(i);
  }
}
