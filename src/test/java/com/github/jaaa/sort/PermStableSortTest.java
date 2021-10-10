package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermStableSort.PERM_STABLE_SORTER;


@Group
public class PermStableSortTest
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(PermStableSort.class) {
    @Override public boolean isStable    () { return PERM_STABLE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PERM_STABLE_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return PERM_STABLE_SORTER; }
  }
  @Group class SorterTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return PERM_STABLE_SORTER; }
  }

  interface TestTemplate extends SorterInPlaceTestTemplate {
    @Override default long nCompMax( int len ) { return len*(len-1L)/2 + (long) len*len; }
    @Override default long nCompMin( int len ) { return len*(len-1L)/2; }
    @Override default long nSwapMax( int len ) { return len; }
  }
}
