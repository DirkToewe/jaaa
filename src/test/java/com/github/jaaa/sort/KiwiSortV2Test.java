package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.KiwiSortV2.KIWI_V2_SORTER;


@Group
public class KiwiSortV2Test
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(KiwiSortV2.class) {
    @Override public boolean isStable    () { return KIWI_V2_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return KIWI_V2_SORTER.isThreadSafe(); }
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
    @Override public SorterInPlace sorter() { return KIWI_V2_SORTER; }
  }
  @Group class SorterTestMedium implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInPlace sorter() { return KIWI_V2_SORTER; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SorterTestLarge implements SorterInPlaceTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public SorterInPlace sorter() { return KIWI_V2_SORTER; }
  }
}
