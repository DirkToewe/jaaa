package com.github.jaaa.sort;

import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;

import static com.github.jaaa.sort.ParallelRebelMergeSort.PARALLEL_REBEL_MERGE_SORTER;


@Group
public class ParallelRebelMergeSortTest
{
  private final Sorter sorter = new StaticMethodsSorter(ParallelRebelMergeSort.class) {
    @Override public boolean isStable    () { return PARALLEL_REBEL_MERGE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return PARALLEL_REBEL_MERGE_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SortTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return PARALLEL_REBEL_MERGE_SORTER; }
  }
  @Group class SorterTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return PARALLEL_REBEL_MERGE_SORTER; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SorterTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return PARALLEL_REBEL_MERGE_SORTER; }
  }
}