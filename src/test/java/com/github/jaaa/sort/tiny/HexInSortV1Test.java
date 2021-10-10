package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInPlaceTestTemplate;
import com.github.jaaa.sort.SorterInPlace;
import com.github.jaaa.sort.StaticMethodsSorterInPlace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.HexInSortV1.HEX_IN_V1_SORTER;


public class HexInSortV1Test implements SorterInPlaceTestTemplate
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(HexInSortV1.class) {
    @Override public boolean isStable    () { return HEX_IN_V1_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return HEX_IN_V1_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInPlace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInPlaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInPlace sorter() { return HEX_IN_V1_SORTER; }
  }
}
