package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

public interface BinarySearchAccessor<T> extends CompareAccessor<T>
{
  public default int binarySearch( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
                 --until;
    while( from <= until ) {    int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return ~from;
  }

  public default int binarySearchR( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    boolean found = false;
                 --until;
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  public default int binarySearchL( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    boolean found = false;
                 --until;
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  public default int binarySearchGap ( T a, int from, int until, T b, int key )
  {
    if( from > until ) throw new IllegalArgumentException();
                 --until;
    while( from <= until ) {    int mid = from + (until-from >>> 1),
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return from;
  }

  public default int binarySearchGapR( T a, int from, int until, T b, int key )
  {
    if( from > until ) throw new IllegalArgumentException();
                 --until;
    while( from <= until ){int mid = from + (until-from >>> 1),
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }

  public default int binarySearchGapL( T a, int from, int until, T b, int key )
  {
    if( from > until ) throw new IllegalArgumentException();
                 --until;
    while( from <= until ){int mid = from + (until-from >>> 1),
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }
}
