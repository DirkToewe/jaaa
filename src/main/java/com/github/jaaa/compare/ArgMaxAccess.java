package com.github.jaaa.compare;

public interface ArgMaxAccess extends CompareAccess
{
  default int argMaxL( int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int max = from;
    for( int i=from; ++i < until; )
      if( compare(i,max) > 0 )
        max = i;
    return max;
  }

  default int argMaxR( int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int max = until-1;
    for( int i=max; i-- > from; )
      if( compare(i,max) > 0 )
        max = i;
    return max;
  }
}
