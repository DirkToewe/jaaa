package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.InsertionSort.INSERTION_SORTER;
import static com.github.jaaa.util.IMath.log2Ceil;
import static com.github.jaaa.util.IMath.log2Floor;
import static java.util.stream.IntStream.range;


@Group
public class InsertionSortTest
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(InsertionSort.class) {
    @Override public boolean isStable    () { return INSERTION_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return INSERTION_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return INSERTION_SORTER; }
  }
  @Group class SorterTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return INSERTION_SORTER; }
  }

  interface TestTemplate extends SorterInplaceTestTemplate
  {
    @Override default long nCompMax( int len ) { return range(1,len).mapToLong( n -> log2Ceil (n+1) ).sum(); }
    @Override default long nCompMin( int len ) { return range(1,len).mapToLong( n -> log2Floor(n+1) ).sum(); }
    @Override default long nSwapMax( int len ) { return len*(len-1L) / 2; }
  }
}
