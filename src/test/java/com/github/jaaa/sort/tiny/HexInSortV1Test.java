package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInplaceTestTemplate;
import com.github.jaaa.sort.SorterInplace;
import com.github.jaaa.sort.StaticMethodsSorterInplace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.HexInSortV1.HEX_IN_V1_SORTER;


public class HexInSortV1Test implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(HexInSortV1.class) {
    @Override public boolean isStable    () { return HEX_IN_V1_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return HEX_IN_V1_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInplaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInplace sorter() { return HEX_IN_V1_SORTER; }
  }
}
