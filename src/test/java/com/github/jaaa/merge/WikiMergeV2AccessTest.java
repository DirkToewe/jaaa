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
      long m = WikiMergeV2Access.minBufLenMIB(len),
           n = WikiMergeV2Access.minBufLenMER(len);

      assertThat(m).isNotNegative();
      assertThat(m).isLessThanOrEqualTo(n);
      assertThat(n).isLessThanOrEqualTo(len);
      assertThat(m+n).isLessThan(len);
      assertThat(m*n).isGreaterThanOrEqualTo(len - m - n);
      assertThat( (m-1)*n ).isLessThan(len - (m-1) - n);
      assertThat( (n-1)*m ).isLessThan(len - (n-1) - m);
      assertThat( (n-1)*(n-1) ).isLessThan( len - (n-1) - (n-1) );
    }
  }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new WikiMergeAcc(acc); }
}
