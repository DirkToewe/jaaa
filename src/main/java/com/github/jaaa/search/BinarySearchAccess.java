package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

public interface BinarySearchAccess extends CompareAccess
{
  public default int binarySearchGap(int from, int until, int key )
  {
    if( from > until )
      throw new IllegalArgumentException();
    --until;
    while( from <= until ){ int mid = from + (until-from >>> 1),
               c = compare(key, mid);
           if( c < 0 )  until = mid-1;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }
    return from;
  }

  public default int binarySearchGapR(int from, int until, int key )
  {
    if( from > until )
      throw new IllegalArgumentException();
    --until;
    while( from <= until )
    { int                mid = from + (until-from >>> 1),
         c = compare(key,mid);
      if(c < 0)  until = mid-1;
      else        from = mid+1;
    }
    return from;
  }

  public default int binarySearchGapL(int from, int until, int key )
  {
    if( from > until )
      throw new IllegalArgumentException();
    --until;
    while( from <= until )
    { int                mid = from + (until-from >>> 1),
         c = compare(key,mid);
      if(c > 0)   from = mid+1;
      else       until = mid-1;
    }
    return from;
  }
}
