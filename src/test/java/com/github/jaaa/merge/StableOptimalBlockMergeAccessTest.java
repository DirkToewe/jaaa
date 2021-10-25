package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Size;

import static com.github.jaaa.ArgMin.argMinL;
import static com.github.jaaa.misc.Rotate.rotate;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;


public class StableOptimalBlockMergeAccessTest implements MergeAccessTestTemplate
{
// STATIC FIELDS
  private static class MergeAcc implements StableOptimalBlockMergeAccess, MergeAccess, ExpMergeV2Access
  {
    private final CompareSwapAccess access;
    public MergeAcc( CompareSwapAccess acc ) { access = acc; }
    @Override public int compare( int i, int j ) { return access.compare(i,j); }
    @Override public void   swap( int i, int j ) { access.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { stableOptimalBlockMerge(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Property( tries = 64*1024 ) void testRotationMin( @ForAll @Size(max=8192) int[] array, @ForAll @Negative int rot )
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
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new MergeAcc(acc); }
}
