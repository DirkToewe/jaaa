package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.KiwiSortV6.KIWI_V6_SORTER;


@Group
public class KiwiSortV6Test
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(KiwiSortV6.class) {
    @Override public boolean isStable    () { return KIWI_V6_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return KIWI_V6_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SortTestLarge implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInPlace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInPlace sorter() { return KIWI_V6_SORTER; }
  }
  @Group class SorterTestMedium implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return KIWI_V6_SORTER; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SorterTestLarge implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInPlace sorter() { return KIWI_V6_SORTER; }
  }
}