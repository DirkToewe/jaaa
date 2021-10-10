package com.github.jaaa.select;

import com.github.jaaa.CompareSwapAccess;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.select.SelectAccessTestTemplate.SelectAccess;


@Group
public class HeapSelectV1AccessTest
{
  private static record Acc( CompareSwapAccess acc ) implements SelectAccess, HeapSelectV1V2Access
  {
    @Override public void select( int from, int mid, int until ) { heapSelectV1(from,mid,until); }
    @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
  }
  private interface TestTemplate extends SelectAccessTestTemplate {
    @Override default SelectAccess createAccess( CompareSwapAccess acc ) { return new Acc(acc); }
    @Override default boolean isStable() { return false; }
  }

  @PropertyDefaults(tries =   100) @Group class HeapSelectTestLarge  implements TestTemplate { @Override public int maxArraySize() {return 1_000_000;} }
  @PropertyDefaults(tries =  1000) @Group class HeapSelectTestMedium implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @PropertyDefaults(tries = 10000) @Group class HeapSelectTestSmall  implements TestTemplate { @Override public int maxArraySize() {return       100;} }
}
