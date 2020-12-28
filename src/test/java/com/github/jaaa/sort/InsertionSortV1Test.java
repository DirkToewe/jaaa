package com.github.jaaa.sort;

import net.jqwik.api.Group;

import static com.github.jaaa.sort.InsertionSortV1.INSERTION_V1_SORTER;

public class InsertionSortV1Test extends SorterInplaceTestTemplate
{
  InsertionSortV1Test() {
    super(new StaticMethodsSorter(InsertionSortV1.class) {
      @Override public boolean isStable    () { return INSERTION_V1_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return INSERTION_V1_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return INSERTION_V1_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterInplaceTest extends SorterInplaceTestTemplate
  {
    SorterInplaceTest() {
      super(INSERTION_V1_SORTER);
    }
  }
}
