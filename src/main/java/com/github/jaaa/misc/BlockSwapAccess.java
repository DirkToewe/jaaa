package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;

import static java.lang.Math.abs;

public interface BlockSwapAccess extends SwapAccess
{
  public default void blockSwap( int from, int until, int len )
  {
    if( len < 0               ) throw new IllegalArgumentException();
    if( from == until         ) return;
    if( abs(until-from) < len ) throw new IllegalArgumentException();
    while(len-- != 0) swap(from+len, until+len);
  }
}
