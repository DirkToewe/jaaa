package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Size;

import static com.github.jaaa.ArgMin.argMinL;
import static com.github.jaaa.misc.Rotate.rotate;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;


public class KiwiMergeAccessTest implements MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class KiwiMergeAcc implements KiwiMergeAccess, MergeAccess, ExpMergeV2Access
  {
    private final CompareSwapAccess access;
    public KiwiMergeAcc( CompareSwapAccess acc ) { access = acc; }
    @Override public int compare( int i, int j ) { return access.compare(i,j); }
    @Override public void   swap( int i, int j ) { access.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { kiwiMerge(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override public int maxArraySize() { return 10_000; }

  @Example void test_minMergeBufLen()
  {
    // In case you want to make abso-freaking-lutely sure:
    for( int len=-1; ++len >= 0; ) {
      int      n = KiwiMergeAccess.minBufLen(len);
      if( 3 <  n ) assertThat( (len-n  ) /  n   ).isGreaterThan      (0);
      if( 0 <  n ) assertThat( (len-n  ) /  n   ).isLessThanOrEqualTo(n);
      if( 1 <  n ) assertThat( (len-n+1) / (n-1)).isGreaterThan      (n-1);
      if( 1 == n ) assertThat(len).isIn(1,2);
      if( 0 == n ) assertThat(len).isEqualTo(0);
    }
  }

  @Property( tries = 64*1024 ) void test_rotateMin( @ForAll @Size(max=8192) int[] array, @ForAll @Negative int rot )
  {
    if( array.length > 0 )
    {
      int old = argMinL(array);
      assertThat(array[old]).isEqualTo( stream(array).min().getAsInt() );

      rotate(array,rot);
      int idx = old  +  rot % array.length;
      if( idx < 0 )
          idx += array.length;

      assertThat(array[idx]).isEqualTo( stream(array).min().getAsInt() );
    }
  }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new KiwiMergeAcc(acc); }
}
