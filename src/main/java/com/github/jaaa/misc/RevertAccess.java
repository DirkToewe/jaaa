package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;

public interface RevertAccess extends SwapAccess
{
  default void revert( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    for(;from < --until; ++from)
      swap(from,until);
  }
}
