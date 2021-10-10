package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInPlaceTestTemplate;
import com.github.jaaa.sort.SorterInPlace;
import com.github.jaaa.sort.StaticMethodsSorterInPlace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV1.NET_V1_SORTER;


public class NetSortV1Test implements SorterInPlaceTestTemplate
{
  private final SorterInPlace sorter = new StaticMethodsSorterInPlace(NetSortV1.class) {
    @Override public boolean isStable    () { return NET_V1_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V1_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 8; }
  @Override public SorterInPlace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInPlaceTestTemplate
  {
    @Override public int maxArraySize() { return 8; }
    @Override public SorterInPlace sorter() { return NET_V1_SORTER; }
  }
}
