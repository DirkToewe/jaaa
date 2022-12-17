package com.github.jaaa.sort;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.IntStream.rangeClosed;


@Group
public class KiwiSortAccessTest
{
  @Example void test_bufLen()
  {
    // In case you want to make abso-freaking-lutely sure:
    rangeClosed(0,Integer.MAX_VALUE).parallel().forEach( len -> {
      int        N = KiwiSortAccess.bufLen(len);
      assertThat(N).isBetween(0,len);
      if( 0 < len )
        assertThat( Integer.bitCount(N) ).isEqualTo(1);
      int n  = N>>>1;
      if( 2 <  N ) assertThat( (len-N) / 2 / N ).isGreaterThan      (0);
      if( 0 <  N ) assertThat( (len-N) / 2 / N ).isLessThanOrEqualTo(N);
      if( 0 <  n ) assertThat( (len-n) / 2 / n ).isGreaterThan      (n);
      if( 1 == N ) assertThat(len).isBetween(1,4);
      if( 0 == N ) assertThat(len).isEqualTo(0);
    });
  }

  private static final class Acc<T> implements SortAccessorTestTemplate.SortAccessor<T>
  {
    private final CompareRandomAccessor<T>  acc;
    private  Acc( CompareRandomAccessor<T> _acc ) { acc = _acc; }
    @Override public void sort( T arr, int from, int until) {
      new KiwiSortAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.kiwiSort(from,until);
    }
  }

  private interface TestTemplate extends SortAccessorTestTemplate {
    @Override default <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) { return new Acc<>(acc); }
    @Override default boolean isStable() { return true; }
  }

  @PropertyDefaults( tries = 100 )
  @Group class Large  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @Group class Medium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @Group class Small  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
