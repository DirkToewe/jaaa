package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.StaticMethodsSorter;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.HexInSortV1.HEX_IN_V1_SORTER;

public class HexInSortV1Test extends TinySorterTestTemplate
{
  HexInSortV1Test() {
    super(new StaticMethodsSorter(HexInSortV1.class) {
      @Override public boolean isStable    () { return HEX_IN_V1_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return HEX_IN_V1_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return HEX_IN_V1_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterTest extends TinySorterTestTemplate
  {
    SorterTest() {
      super(HEX_IN_V1_SORTER);
    }
  }
}
