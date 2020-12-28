package com.github.jaaa.heap;

import com.github.jaaa.CompareSwapAccess;


public interface BinaryHeapAccess extends CompareSwapAccess
{
  public default void buildHeap( int from, int until )
  {
    throw new Error("Not yet implemented!");
  }
}
