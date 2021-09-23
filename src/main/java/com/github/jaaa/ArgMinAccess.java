package com.github.jaaa;

public interface ArgMinAccess extends CompareAccess
{
  default int argMinL( int from, int until )
  {
    if( from < 0 ) throw new IllegalArgumentException();
    if( from >= until ) throw new IllegalArgumentException();
    int min = from;
    for( int i=from; ++i < until; )
      if( compare(i,min) < 0 )
        min = i;
    return min;
  }

  default int argMinR( int from, int until )
  {
    if( from < 0 ) throw new IllegalArgumentException();
    if( from >= until ) throw new IllegalArgumentException();
    int min = until-1;
    for( int i=min; i-- > from; )
      if( compare(i,min) < 0 )
        min = i;
    return min;
  }
}
