package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInplaceTestTemplate;
import com.github.jaaa.sort.SorterInplace;
import com.github.jaaa.sort.StaticMethodsSorterInplace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV2.NET_V2_SORTER;


public class NetSortV2Test implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(NetSortV2.class) {
    @Override public boolean isStable    () { return NET_V2_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V2_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 16; }
  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInplaceTestTemplate
  {
    @Override public int maxArraySize() { return 16; }
    @Override public SorterInplace sorter() { return NET_V2_SORTER; }
  }
}
