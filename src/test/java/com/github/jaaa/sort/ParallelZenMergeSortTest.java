package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.ParallelZenMergeSort.PARALLEL_ZEN_MERGE_SORTER;


@Group
public class ParallelZenMergeSortTest
{
  private final Sorter sorter = new StaticMethodsSorter(ParallelZenMergeSort.class) {
    @Override public boolean isStable    () { return PARALLEL_ZEN_MERGE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PARALLEL_ZEN_MERGE_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SortTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return PARALLEL_ZEN_MERGE_SORTER; }
  }
  @Group class SorterTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return PARALLEL_ZEN_MERGE_SORTER; }
  }
  @PropertyDefaults( tries = 1_000 )
  @Group class SorterTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return PARALLEL_ZEN_MERGE_SORTER; }
  }
}