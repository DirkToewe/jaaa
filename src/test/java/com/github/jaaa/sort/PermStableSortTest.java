package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermStableSort.PERM_STABLE_SORTER;


@Group
public class PermStableSortTest
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(PermStableSort.class) {
    @Override public boolean isStable    () { return PERM_STABLE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PERM_STABLE_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return PERM_STABLE_SORTER; }
  }
  @Group class SorterTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return PERM_STABLE_SORTER; }
  }

  interface TestTemplate extends SorterInplaceTestTemplate {
    @Override default long nCompMax( int len ) { return len*(len-1)/2 + len*len; }
    @Override default long nCompMin( int len ) { return len*(len-1)/2; }
    @Override default long nSwapMax( int len ) { return len; }
  }
}