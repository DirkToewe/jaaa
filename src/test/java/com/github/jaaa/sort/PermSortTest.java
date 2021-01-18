package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermSort.PERM_SORTER;

public class PermSortTest implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(PermSort.class) {
    @Override public boolean isStable    () { return PERM_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PERM_SORTER.isThreadSafe(); }
  };

  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterInplaceTest implements SorterInplaceTestTemplate
  {
    @Override public SorterInplace sorter() { return PERM_SORTER; }
  }
}
