package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.QuickSort.QUICK_SORTER;


@Group
public class QuickSortTest
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(QuickSort.class) {
    @Override public boolean isStable    () { return QUICK_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return QUICK_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SortTestLarge implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return QUICK_SORTER; }
  }
  @Group class SorterTestMedium implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return QUICK_SORTER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SorterTestLarge implements SorterInplaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInplace sorter() { return QUICK_SORTER; }
  }
}
