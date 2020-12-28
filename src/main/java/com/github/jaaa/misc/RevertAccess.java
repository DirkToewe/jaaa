package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;

public interface RevertAccess extends SwapAccess
{
  public default void revert( int from, int until )
  {
    if( from > until )
      throw new IllegalArgumentException();
    for(;--until>from; ++from)
      swap(from,until);
  }
}
