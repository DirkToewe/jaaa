package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccess;

public interface BinarySearchAccess extends CompareAccess
{
  default int binarySearch( int from, int until, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    while( from < until ) { int mid = from+until >>> 1,
               c = compare(key, mid);
           if( c < 0 )  until = mid;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }
    return ~from;
  }

  default int binarySearchL( int from, int until, int key ) { return binarySearch(from,until, key, false); }
  default int binarySearchR( int from, int until, int key ) { return binarySearch(from,until, key, true ); }
  default int binarySearch ( int from, int until, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;
    boolean found = false;

    while(from < until){ int mid = from+until >>> 1,
          c  =  compare(key, mid);
      if( c > bias )  from = mid+1;
      else           until = mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  default int binarySearchGap( int from, int until, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    while( from < until ) { int mid = from+until >>> 1,
               c = compare(key, mid);
           if( c < 0 )  until = mid;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }
    return from;
  }

  default int binarySearchGapL( int from, int until, int key ) { return binarySearchGap(from,until, key, false); }
  default int binarySearchGapR( int from, int until, int key ) { return binarySearchGap(from,until, key, true ); }
  default int binarySearchGap ( int from, int until, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;

    while( from < until ){int mid = from+until >>> 1,
          c   =   compare(key,mid);
      if( c > bias )   from = mid+1;
      else            until = mid;
    }
    return from;
  }
}
