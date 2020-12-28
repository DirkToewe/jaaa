package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;
import net.jqwik.api.Group;

import java.util.Objects;
import java.util.function.IntBinaryOperator;

import static com.github.jaaa.search.SearchAccessorTestTemplate.SearchAccessor;
import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@Group
public class ExpSearchAccessorTest
{
  private static long compLim( IntBinaryOperator startIndex, int from, int until, int i ) {
    if( from == until ) return 0;
                           i = abs( startIndex.applyAsInt(from,until) - i );
    return 2 + 2*log2Floor(i+1);
  }

  private static final class ExpSearchAcc<T> implements SearchAccessor<T>,
                                                     ExpSearchAccessor<T>
  {
    private final IntBinaryOperator startIndex;
    private final CompareAccessor<? super T> compareAccessor;

    public ExpSearchAcc( CompareAccessor<? super T> _compareAccessor, IntBinaryOperator _startIndex )
    {
      compareAccessor = requireNonNull(_compareAccessor);
      startIndex      = requireNonNull(_startIndex);
    }

    @Override public int compare( T a, int i, T b, int j ) { return compareAccessor.compare(a,i, b,j); }

    @Override public int search    ( T a, int from, int until, T b, int i ) { return expSearch    (a,from,until, startIndex.applyAsInt(from,until), b,i); }
    @Override public int searchR   ( T a, int from, int until, T b, int i ) { return expSearchR   (a,from,until, startIndex.applyAsInt(from,until), b,i); }
    @Override public int searchL   ( T a, int from, int until, T b, int i ) { return expSearchL   (a,from,until, startIndex.applyAsInt(from,until), b,i); }
    @Override public int searchGap ( T a, int from, int until, T b, int i ) { return expSearchGap (a,from,until, startIndex.applyAsInt(from,until), b,i); }
    @Override public int searchGapR( T a, int from, int until, T b, int i ) { return expSearchGapR(a,from,until, startIndex.applyAsInt(from,until), b,i); }
    @Override public int searchGapL( T a, int from, int until, T b, int i ) { return expSearchGapL(a,from,until, startIndex.applyAsInt(from,until), b,i); }
  }

  private static class ExpSearchAccTestTemplate extends SearchAccessorTestTemplate
  {
    private IntBinaryOperator startIndex;
    public ExpSearchAccTestTemplate( IntBinaryOperator _startIndex ) { startIndex = requireNonNull(_startIndex); }
    @Override protected <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc ) { return new ExpSearchAcc<>(cmpAcc,startIndex); }
    @Override protected long comparisonLimit( int from, int until, int i ) { return compLim(startIndex, from,until, i); }
  }

  @Group class ExpSearchAccessorX extends ExpSearchAccTestTemplate { ExpSearchAccessorX() { super( (i,j) -> (int) ( i-1L + Integer.toUnsignedLong( Objects.hash(i,j) ) % (2L+j-i) ) ); } }
  @Group class ExpSearchAccessorL extends ExpSearchAccTestTemplate { ExpSearchAccessorL() { super( (i,j) -> i ); } }
  @Group class ExpSearchAccessorM extends ExpSearchAccTestTemplate { ExpSearchAccessorM() { super( (i,j) -> i + (j-i >>> 1) ); } }
  @Group class ExpSearchAccessorR extends ExpSearchAccTestTemplate { ExpSearchAccessorR() { super( (i,j) -> j-1 ); } }
}
