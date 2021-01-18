package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.InsertionSort.INSERTION_SORTER;

public class InsertionSortTest implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(InsertionSort.class) {
    @Override public boolean isStable    () { return INSERTION_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return INSERTION_SORTER.isThreadSafe(); }
  };

  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterInplaceTest implements SorterInplaceTestTemplate
  {
    @Override public SorterInplace sorter() { return INSERTION_SORTER; }
  }
}
