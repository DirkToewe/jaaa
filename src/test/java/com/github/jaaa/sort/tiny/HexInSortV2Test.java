package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.NewSorterInplaceTestTemplate;
import com.github.jaaa.sort.SorterInplace;
import com.github.jaaa.sort.StaticMethodsSorterInplace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.HexInSortV2.HEX_IN_V2_SORTER;


public class HexInSortV2Test implements NewSorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(HexInSortV2.class) {
    @Override public boolean isStable    () { return HEX_IN_V2_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return HEX_IN_V2_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterTest implements NewSorterInplaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInplace sorter() { return HEX_IN_V2_SORTER; }
  }
}
