package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.SelectionSort.SELECTION_SORTER;

public class SelectionSortTest implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(SelectionSort.class) {
    @Override public boolean isStable    () { return SELECTION_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return SELECTION_SORTER.isThreadSafe(); }
  };

  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterInplaceTest implements SorterInplaceTestTemplate
  {
    @Override public SorterInplace sorter() { return SELECTION_SORTER; }
  }
}
