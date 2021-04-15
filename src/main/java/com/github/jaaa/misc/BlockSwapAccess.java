package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;

import static java.lang.Math.abs;

public interface BlockSwapAccess extends SwapAccess
{
  public default void blockSwap( int i, int j, int len )
  {
    if( len < 0        ) throw new IllegalArgumentException();
    if( i == j         ) return;
    if( abs(j-i) < len ) throw new IllegalArgumentException();
    while(len-- != 0) swap(i+len, j+len);
  }
}
