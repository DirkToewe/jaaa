package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.StaticMethodsSorter;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV3.NET_V3_SORTER;

public class NetSortV3Test extends TinySorterTestTemplate
{
  NetSortV3Test() {
    super(new StaticMethodsSorter(NetSortV3.class) {
      @Override public boolean isStable    () { return NET_V3_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return NET_V3_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return NET_V3_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterTest extends TinySorterTestTemplate
  {
    SorterTest() { super(NET_V3_SORTER); }
  }
}
