package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.Example;

import static org.assertj.core.api.Assertions.assertThat;


public class WikiMergeV1AccessTest implements MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class WikiMergeAcc implements WikiMergeV1Access, MergeAccess
  {
    private final CompareSwapAccess access;
    public WikiMergeAcc( CompareSwapAccess acc ) { access = acc; }
    @Override public int compare( int i, int j ) { return access.compare(i,j); }
    @Override public void   swap( int i, int j ) { access.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { wikiMergeV1(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override public int maxArraySize() { return 10_000; }

  @Example void minBufLengths()
  {
    for( int len=2; ++len >= 0; )
    {
      long mib = WikiMergeV1Access.minBufLenMIB(len),
           mer = WikiMergeV1Access.minBufLenMER(len);

      assertThat(mib).isNotNegative();
      assertThat(mib).isLessThanOrEqualTo(mer);
      assertThat(mer).isLessThanOrEqualTo(len);
      assertThat(mib+mer).isLessThan(len);
      assertThat(mib*mer).isGreaterThanOrEqualTo( len    - mer - mib ); // ◀─── make sure buffers are large enough
      assertThat(  mer   *(mib-1)   ).isLessThan( len+1L - mer - mib ); // ◀─┬─ make sure total buffer is as small as possible
      assertThat( (mer-1)* mib      ).isLessThan( len+1L - mer - mib ); // ◀─╯
      assertThat( (mer+1)*(mib-1)   ).isLessThan( len    - mer - mib ); // ◀─── make sure merge buffer is as large as possible
    }
  }
//  @Property( tries = 1_000_000 ) void test_desiredLen( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int len )
//  {
//    if( 3 < len ) {
//      var acc = new BinMrgAcc(null);
//      long m = acc.stableOptimalBlockMergeV3_MIB_desiredLen(len),
//           n = acc.stableOptimalBlockMergeV3_MER_desiredLen(len);
//
//      assertThat(m).isNotNegative();
//      assertThat(m).isLessThanOrEqualTo(n);
//      assertThat(n).isLessThanOrEqualTo(len);
//      assertThat(m+n).isLessThan(len);
//      assertThat(m*n).isGreaterThanOrEqualTo(len - m - n);
//      assertThat( (m-1)*n ).isLessThan(len - (m-1) - n);
//      assertThat( (n-1)*m ).isLessThan(len - (n-1) - m);
//      assertThat( (n-1)*(n-1) ).isLessThan( len - (n-1) - (n-1) );
//    }
//  }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new WikiMergeAcc(acc); }
}
