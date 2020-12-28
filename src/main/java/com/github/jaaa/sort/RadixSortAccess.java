package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;

public interface RadixSortAccess extends CompareSwapAccess
{
  public long key( int i );

  public void swap( int i, int j );

  public default void radixSort( int from, int until )
  {
    int[] stack = new int[64]; // <- constant size allows stack allocation ?


  }
}
