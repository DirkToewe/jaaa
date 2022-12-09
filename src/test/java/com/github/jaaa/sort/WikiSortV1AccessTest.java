package com.github.jaaa.sort;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static org.assertj.core.api.Assertions.assertThat;


@Group
public class WikiSortV1AccessTest
{
  @Example void minBufLen()
  {
    for( int len=-1; ++len >= 0; )
    {
      int b = WikiSortV1Access.minBufLen(len);

      long mer = WikiSortV1Access.merBufLen(len-b, b),
           mib = b-mer;

      assertThat(mer).isNotNegative();
      assertThat(mib).isNotNegative();

      assertThat(mib).isLessThanOrEqualTo(mer);
      assertThat(mer).isLessThanOrEqualTo(len);

      assertThat(mib+mer).isLessThanOrEqualTo(len);

      assertThat( mib*mer).isGreaterThanOrEqualTo( len -  b    ); // ◀─── make sure buffers are large enough
      assertThat( (mib-1)*mer        ).isLessThan( len - (b-1) ); // ◀─╮
      assertThat( (mer-1)*mib        ).isLessThan( len - (b-1) ); // ◀─┼─ make sure total buffer is as small as possible
      assertThat( (b>>>1)*(b-1L>>>1) ).isLessThan( len - (b-1) ); // ◀─╯
      assertThat( (mer+1)*(mib-1)    ).isLessThan( len -  b    ); // ◀─── make sure merge buffer is as large as possible
    }
  }

  @Example void merBufLen()
  {
    for( long len=-1; len++ < 20_000; )
    for( long buf=-1; buf++ < len;    )
    {
      long mer = WikiSortV1Access.merBufLen( (int)(len-buf), (int)buf ),
           mib = buf-mer,
        minLen = WikiSortV1Access.minBufLen( (int)len );

      if( buf < minLen ) {
        assertThat(mer).isZero();
        assertThat(mib).isEqualTo(buf);
      }
      else {
        if( len == 1 || len == buf ) assertThat(mib).isZero();
        else                         assertThat(mer).isBetween(1L, buf-1);

        assertThat(mib).isLessThanOrEqualTo(mer);

        assertThat(mib*mer).isGreaterThanOrEqualTo(len-buf); // ◀─── make sure buffers are large enough
        assertThat(  (mer+1)*(mib-1)  ).isLessThan(len-buf); // ◀─── make sure merge buffer is as large as possible
      }
    }
  }

  private static class Acc<T> implements SortAccessorTestTemplate.SortAccessor<T>
  {
    private final CompareRandomAccessor<T>  acc;
    public Acc(   CompareRandomAccessor<T> _acc ) { acc=_acc; }
    @Override public void sort( T arr, int from, int until) {
      new WikiSortV1Access() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.wikiSortV1(from,until);
    }
  }
  private interface TestTemplate extends SortAccessorTestTemplate {
    @Override default <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) { return new Acc<>(acc); }
    @Override default boolean isStable() { return false; }
  }

  @PropertyDefaults( tries = 1_000 )
  @Group class SorterTestLarge       implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @Group class SorterTestMedium      implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @Group class SortAccessorTestSmall implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
