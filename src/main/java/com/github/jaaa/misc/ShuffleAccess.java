package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;


public interface ShuffleAccess extends SwapAccess
{
  int randInt( int from, int until );

  default void shuffle( int from, int until )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    while( from < until-1 ) {
      int  i = randInt(from,until);
      swap(i,--until);
    }
  }
}
