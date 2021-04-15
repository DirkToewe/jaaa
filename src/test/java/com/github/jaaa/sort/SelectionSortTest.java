package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.SelectionSort.SELECTION_SORTER;


@Group
public class SelectionSortTest
{

  private final SorterInplace sorter = new StaticMethodsSorterInplace(SelectionSort.class) {
    @Override public boolean isStable    () { return SELECTION_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return SELECTION_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return SELECTION_SORTER; }
  }
  @Group class SorterTestMedium implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return SELECTION_SORTER; }
  }
}
