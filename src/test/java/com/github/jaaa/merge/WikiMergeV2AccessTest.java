package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.Example;

import static org.assertj.core.api.Assertions.assertThat;


public class WikiMergeV2AccessTest implements MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class WikiMergeAcc implements WikiMergeV2Access, MergeAccess
  {
    private final CompareSwapAccess access;
    public WikiMergeAcc( CompareSwapAccess acc ) { access = acc; }
    @Override public int compare( int i, int j ) { return access.compare(i,j); }
    @Override public void   swap( int i, int j ) { access.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { wikiMergeV2(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Example void minBufLengths()
  {
    for( int len=3; ++len >= 0; )
    {
      long mib = WikiMergeV2Access.minBufLenMIB(len),
           mer = WikiMergeV2Access.minBufLenMER(len);

      assertThat(mib).isNotNegative();
      assertThat(mib).isLessThanOrEqualTo(mer);
      assertThat(mer).isLessThanOrEqualTo(len);
      assertThat(mib+mer).isLessThan(len);
      assertThat(mib*mer).isGreaterThanOrEqualTo( len - mib - mer );
      assertThat( (mib-1)*mer ).isLessThan(len - (mib-1) - mer);
      assertThat( (mer-1)*mib ).isLessThan(len - (mer-1) - mib);
      assertThat( (mer-1)*(mer-1) ).isLessThan( len - (mer-1) - (mer-1) );
      assertThat( (mer+1)*(mib-1) ).isLessThan( len - mib - mer ); // make sure mer is as large as possible
    }
  }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new WikiMergeAcc(acc); }
}
