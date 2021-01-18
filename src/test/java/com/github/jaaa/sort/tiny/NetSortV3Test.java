package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.NewSorterInplaceTestTemplate;
import com.github.jaaa.sort.SorterInplace;
import com.github.jaaa.sort.StaticMethodsSorterInplace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV3.NET_V3_SORTER;


public class NetSortV3Test implements NewSorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(NetSortV3.class) {
    @Override public boolean isStable    () { return NET_V3_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V3_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterTest implements NewSorterInplaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInplace sorter() { return NET_V3_SORTER; }
  }
}
