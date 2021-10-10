package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import net.jqwik.api.Example;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;


@Group
public class HeapSortFastAccessTest
{
  @Example void experiment() {
    int[] x = {-1, 0, 1, 1, -1, 0};
    new HeapSortFastAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(x,i,j); }
      @Override public int compare( int i, int j ) { return Integer.compare(x[i],x[j]); }
    }.binHeapMinR_buildFast(0,6);
    System.out.println( Arrays.toString(x) );
  }

  private static class Acc<T> implements SortAccessorTestTemplate.SortAccessor<T>
  {
    private final CompareRandomAccessor<T>  acc;
    public Acc(   CompareRandomAccessor<T> _acc ) { acc=_acc; }
    @Override public void sort( T arr, int from, int until) {
      new HeapSortFastAccess() {
        @Override public void   swap( int i, int j ) { acc.swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.heapSortFast(from,until);
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
