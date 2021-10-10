package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.HeapSortFast.HEAP_FAST_SORTER;


@Group
public class HeapSortFastTest
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(HeapSortFast.class) {
    @Override public boolean isStable    () { return HEAP_FAST_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return HEAP_FAST_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SortTestLarge implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInPlace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return HEAP_FAST_SORTER; }
  }
  @Group class SorterTestMedium implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return HEAP_FAST_SORTER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SorterTestLarge implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInPlace sorter() { return HEAP_FAST_SORTER; }
  }
}
