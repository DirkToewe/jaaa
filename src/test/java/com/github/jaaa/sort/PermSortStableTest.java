package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.PermSortStable.PERM_STABLE_SORTER;

public class PermSortStableTest extends SorterInplaceTestTemplate
{
  PermSortStableTest() {
    super(new StaticMethodsSorter(PermSortStable.class) {
      @Override public boolean isStable    () { return PERM_STABLE_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return PERM_STABLE_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return PERM_STABLE_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterInplaceTest extends SorterInplaceTestTemplate
  {
    SorterInplaceTest() {
      super(PERM_STABLE_SORTER);
    }
  }
}
