package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermStableSort.PERM_STABLE_SORTER;

public class PermStableSortTest implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(PermStableSort.class) {
    @Override public boolean isStable    () { return PERM_STABLE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PERM_STABLE_SORTER.isThreadSafe(); }
  };

  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterInplaceTest implements SorterInplaceTestTemplate
  {
    @Override public SorterInplace sorter() { return PERM_STABLE_SORTER; }
  }
}
