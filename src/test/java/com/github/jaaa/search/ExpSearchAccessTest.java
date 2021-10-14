package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;
import com.github.jaaa.CompareAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import java.util.Objects;
import java.util.function.IntBinaryOperator;

import static com.github.jaaa.util.IMath.log2Floor;
import static java.lang.Math.abs;
import static java.util.Objects.requireNonNull;


@Group
@PropertyDefaults( tries=100_000 )
public class ExpSearchAccessTest
{
  private static long compLim(IntBinaryOperator startIndex, int from, int until, int i ) {
    if( from == until ) return 0;
    i = abs( startIndex.applyAsInt(from,until) - i );
    return 2 + 2L*log2Floor(i+1);
  }

  private static class ExpSearchAcc implements SearchAccessTestTemplate.SearchAccess,
                                                                     ExpSearchAccess
  {
    private final IntBinaryOperator startIndex;
    private final CompareAccess acc;

    public ExpSearchAcc( CompareAccess _acc, IntBinaryOperator _startIndex )
    {
      acc        = requireNonNull(_acc);
      startIndex = requireNonNull(_startIndex);
    }

    @Override public int compare( int i, int j ) { return acc.compare(i,j); }

    @Override public int search    ( int from, int until, int i ) { return expSearch    (from,until, startIndex.applyAsInt(from,until), i); }
    @Override public int searchR   ( int from, int until, int i ) { return expSearchR   (from,until, startIndex.applyAsInt(from,until), i); }
    @Override public int searchL   ( int from, int until, int i ) { return expSearchL   (from,until, startIndex.applyAsInt(from,until), i); }
    @Override public int searchGap ( int from, int until, int i ) { return expSearchGap (from,until, startIndex.applyAsInt(from,until), i); }
    @Override public int searchGapR( int from, int until, int i ) { return expSearchGapR(from,until, startIndex.applyAsInt(from,until), i); }
    @Override public int searchGapL( int from, int until, int i ) { return expSearchGapL(from,until, startIndex.applyAsInt(from,until), i); }
  }

  private static class ExpSearchAccTestTemplate extends SearchAccessTestTemplate
  {
    private IntBinaryOperator startIndex;
    public ExpSearchAccTestTemplate( IntBinaryOperator _startIndex ) { startIndex = requireNonNull(_startIndex); }
    @Override public SearchAccess createAccess( CompareAccess acc ) { return new ExpSearchAcc(acc,startIndex); }
    @Override public long comparisonLimit( int from, int until, int i ) { return compLim(startIndex, from,until, i); }
  }

  @Group class ExpSearchAccessX extends ExpSearchAccTestTemplate { ExpSearchAccessX() { super( (i, j) -> (int) ( i-1L + Integer.toUnsignedLong( Objects.hash(i,j) ) % (2L+j-i) ) ); } }
  @Group class ExpSearchAccessL extends ExpSearchAccTestTemplate { ExpSearchAccessL() { super( (i, j) -> i ); } }
  @Group class ExpSearchAccessM extends ExpSearchAccTestTemplate { ExpSearchAccessM() { super( (i, j) -> i + (j-i >>> 1) ); } }
  @Group class ExpSearchAccessR extends ExpSearchAccTestTemplate { ExpSearchAccessR() { super( (i, j) -> j-1 ); } }
}
