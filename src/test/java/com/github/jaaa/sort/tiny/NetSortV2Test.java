package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInPlaceTestTemplate;
import com.github.jaaa.sort.SorterInPlace;
import com.github.jaaa.sort.StaticMethodsSorterInPlace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV2.NET_V2_SORTER;


public class NetSortV2Test implements SorterInPlaceTestTemplate
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(NetSortV2.class) {
    @Override public boolean isStable    () { return NET_V2_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V2_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInPlace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInPlaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInPlace sorter() { return NET_V2_SORTER; }
  }
}
