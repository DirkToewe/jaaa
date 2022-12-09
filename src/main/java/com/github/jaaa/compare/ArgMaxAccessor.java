package com.github.jaaa.compare;

public interface ArgMaxAccessor<T> extends CompareAccessor<T>
{
  default int argMaxL( T arr, int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int max = from;
    for( int i=from; ++i < until; )
      if( compare(arr,i, arr,max) > 0 )
        max = i;
    return max;
  }

  default int argMaxR( T arr, int from, int until )
  {
    if( from < 0 || from >= until )
      throw new IllegalArgumentException();
    int max = until-1;
    for( int i=max; i-- > from; )
      if( compare(arr,i, arr,max) > 0 )
        max = i;
    return max;
  }
}
