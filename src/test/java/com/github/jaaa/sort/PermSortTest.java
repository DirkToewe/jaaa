package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermSort.PERM_SORTER;

public class PermSortTest extends SorterInplaceTestTemplate
{
  PermSortTest() {
    super(new StaticMethodsSorter(PermSort.class) {
      @Override public boolean isStable    () { return PERM_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return PERM_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return PERM_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterInplaceTest extends SorterInplaceTestTemplate
  {
    SorterInplaceTest() {
      super(PERM_SORTER);
    }
  }
}
