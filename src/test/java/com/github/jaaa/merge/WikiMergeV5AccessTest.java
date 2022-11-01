package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.Example;

import static org.assertj.core.api.Assertions.assertThat;


public class WikiMergeV5AccessTest implements MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class WikiMergeAcc implements WikiMergeV5Access, MergeAccess
  {
    private final CompareSwapAccess access;
    public WikiMergeAcc( CompareSwapAccess acc ) { access = acc; }
    @Override public int compare( int i, int j ) { return access.compare(i,j); }
    @Override public void   swap( int i, int j ) { access.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { wikiMergeV5(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

  // METHODS
  @Override public int maxArraySize() { return 10_000; }

  @Example void minBufLen()
  {
    for( int len=-1; ++len >= 0; )
    {
      int b = WikiMergeV5Access.minBufLen(len);

      assertThat(b).isEqualTo(
          WikiMergeV2Access.minBufLenMER(len)
        + WikiMergeV2Access.minBufLenMIB(len)
      );

      long mer = WikiMergeV5Access.merBufLen(len-b, b),
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
    for( long len=-1; len++ < 10_000; )
    for( long buf=-1; buf++ < len;    )
    {
      long mer = WikiMergeV5Access.merBufLen( (int)(len-buf), (int)buf ),
           mib = buf-mer,
        minLen = WikiMergeV5Access.minBufLen( (int)len );

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

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new WikiMergeAcc(acc); }
}
