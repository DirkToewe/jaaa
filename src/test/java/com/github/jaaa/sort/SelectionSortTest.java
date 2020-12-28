package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.SelectionSort.SELECTION_SORTER;

public class SelectionSortTest extends SorterInplaceTestTemplate
{
  SelectionSortTest() {
    super(new StaticMethodsSorter(SelectionSort.class) {
      @Override public boolean isStable    () { return SELECTION_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return SELECTION_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return SELECTION_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterInplaceTest extends SorterInplaceTestTemplate
  {
    SorterInplaceTest() {
      super(SELECTION_SORTER);
    }
  }
}
