package com.github.jaaa.sort.tiny;

import com.github.jaaa.sort.StaticMethodsSorter;
import net.jqwik.api.Group;

import static com.github.jaaa.sort.tiny.NetSortV1.NET_V1_SORTER;

public class NetSortV1Test extends TinySorterTestTemplate
{
  NetSortV1Test() {
    super(new StaticMethodsSorter(NetSortV1.class) {
      @Override public boolean isStable    () { return NET_V1_SORTER.isStable    (); }
      @Override public boolean isInplace   () { return NET_V1_SORTER.isInplace   (); }
      @Override public boolean isThreadSafe() { return NET_V1_SORTER.isThreadSafe(); }
    });
  }

  @Group
  class SorterTest extends TinySorterTestTemplate
  {
    SorterTest() {
      super(NET_V1_SORTER);
    }
  }
}
