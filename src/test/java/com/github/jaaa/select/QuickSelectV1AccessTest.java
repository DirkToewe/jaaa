package com.github.jaaa.select;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;


@Group
public class QuickSelectV1AccessTest
{
  private interface TestTemplate extends SelectAccessorTestTemplate {
    @Override default <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) {
      return (arr, from, mid, until) ->
        new QuickSelectV1Access() {
          @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
          @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
        }.quickSelectV1(from,mid,until);
    }
    @Override default boolean isStable() { return false; }
  }

  @PropertyDefaults(tries =   100) @Group class HeapSelectTestLarge  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @PropertyDefaults(tries =  1000) @Group class HeapSelectTestMedium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @PropertyDefaults(tries = 10000) @Group class HeapSelectTestSmall  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
