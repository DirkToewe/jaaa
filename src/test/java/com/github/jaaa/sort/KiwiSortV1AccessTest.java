package com.github.jaaa.sort;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static org.assertj.core.api.Assertions.assertThat;


@Group
public class KiwiSortV1AccessTest
{
  @Example void test_minBufLen()
  {
    // In case you want to make abso-freaking-lutely sure:
    for( int len=-1; ++len >= 0; ) {
      int      n = KiwiSortV1Access.minBufLen(len);
      if( 3 <  n ) assertThat( (len-n  ) / 2 /  n   ).isGreaterThan      (0);
      if( 0 <  n ) assertThat( (len-n  ) / 2 /  n   ).isLessThanOrEqualTo(n);
      if( 1 <  n ) assertThat( (len-n+1) / 2 / (n-1)).isGreaterThan      (n-1);
      if( 1 == n ) assertThat(len).isBetween(1,4);
      if( 0 == n ) assertThat(len).isEqualTo(0);
    }
  }

  private record Acc<T>( CompareRandomAccessor<T> acc ) implements SortAccessorTestTemplate.SortAccessor<T>
  {
    @Override public void sort( T arr, int from, int until) {
      class SortAcc implements KiwiSortV1Access
      {
        @Override public void   swap( int i, int j ) { acc.swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }
      new SortAcc().kiwiSortV1(from,until);
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
