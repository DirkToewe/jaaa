package com.github.jaaa.permute;

import static java.lang.Math.abs;

public interface BlockSwapAccess extends SwapAccess
{
  default void blockSwap( int i, int j, int len )
  {
    if( len < 0        ) throw new IllegalArgumentException();
    if( i == j         ) return;
    if( abs(j-i) < len ) throw new IllegalArgumentException();
    while(len-- != 0) swap(i+len, j+len);
  }
}
