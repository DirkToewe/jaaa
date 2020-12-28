package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.QuickSort.QUICK_SORTER;

public class QuickSortTest extends SorterInplaceTestTemplate
{
  QuickSortTest() {
    super(new StaticMethodsSorter(QuickSort.class) {
      @Override public boolean isStable    () { return QUICK_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return QUICK_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return QUICK_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterInplaceTest extends SorterInplaceTestTemplate
  {
    SorterInplaceTest() {
      super(QUICK_SORTER);
    }
  }
}
