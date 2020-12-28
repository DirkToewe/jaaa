package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.StaticMethodsSorter;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.HexInSortV2.HEX_IN_V2_SORTER;

public class HexInSortV2Test extends TinySorterTestTemplate
{
  HexInSortV2Test() {
    super(new StaticMethodsSorter(HexInSortV2.class) {
      @Override public boolean isStable    () { return HEX_IN_V2_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return HEX_IN_V2_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return HEX_IN_V2_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterTest extends TinySorterTestTemplate
  {
    SorterTest() { super(HEX_IN_V2_SORTER); }
  }
}
