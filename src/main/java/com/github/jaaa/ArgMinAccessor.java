package com.github.jaaa;

public interface ArgMinAccessor<T> extends CompareAccessor<T>
{
  default int argMinL( T arr, int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int min = from;
    for( int i=from; ++i < until; )
      if( compare(arr,i, arr,min) < 0 )
        min = i;
    return min;
  }

  default int argMinR( T arr, int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int min = until-1;
    for( int i=min; i-- > from; )
      if( compare(arr,i, arr,min) < 0 )
        min = i;
    return min;
  }
}
