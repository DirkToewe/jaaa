package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.SorterInplaceTestTemplate;
import com.github.jaaa.sort.SorterInplace;
import com.github.jaaa.sort.StaticMethodsSorterInplace;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV1.NET_V1_SORTER;


public class NetSortV1Test implements SorterInplaceTestTemplate
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(NetSortV1.class) {
    @Override public boolean isStable    () { return NET_V1_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return NET_V1_SORTER.isThreadSafe(); }
  };

  @Override public int maxArraySize() { return 8; }
  @Override public SorterInplace sorter() { return sorter; }

  @Group
  class SorterTest implements SorterInplaceTestTemplate
  {
    @Override public int maxArraySize() { return 8; }
    @Override public SorterInplace sorter() { return NET_V1_SORTER; }
  }
}
