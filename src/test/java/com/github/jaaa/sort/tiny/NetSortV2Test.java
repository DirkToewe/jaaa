package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.StaticMethodsSorter;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV2.NET_V2_SORTER;

public class NetSortV2Test extends TinySorterTestTemplate
{
  NetSortV2Test() {
    super(new StaticMethodsSorter(NetSortV2.class) {
      @Override public boolean isStable    () { return NET_V2_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return NET_V2_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return NET_V2_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterTest extends TinySorterTestTemplate
  {
    SorterTest() { super(NET_V2_SORTER); }
  }
}
