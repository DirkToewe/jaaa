package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInPlaceTestTemplate;
import com.github.jaaa.sort.SorterInPlace;
import com.github.jaaa.sort.StaticMethodsSorterInPlace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV3.NET_V3_SORTER;


public class NetSortV3Test implements SorterInPlaceTestTemplate
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(NetSortV3.class) {
    @Override public boolean isStable    () { return NET_V3_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V3_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInPlace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInPlaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInPlace sorter() { return NET_V3_SORTER; }
  }
}
