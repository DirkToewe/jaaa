package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

public interface BinarySearchAccessor<T> extends CompareAccessor<T>
{
  default int binarySearch( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    while( from < until ) {     int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )      until = mid;
      else if( c > 0 )       from = mid+1;
      else                   return mid;
    }
    return ~from;
  }

  default int binarySearchL( T a, int from, int until, T b, int key ) { return binarySearch(a,from,until, b,key, false); }
  default int binarySearchR( T a, int from, int until, T b, int key ) { return binarySearch(a,from,until, b,key, true ); }
  default int binarySearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;
    boolean found = false;

    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )    from = mid+1;
      else             until = mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  default int binarySearchGap ( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    while( from < until ) {     int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )      until = mid;
      else if( c > 0 )       from = mid+1;
      else                   return mid;
    }
    return from;
  }

  default int binarySearchGapL( T a, int from, int until, T b, int key ) { return binarySearchGap(a,from,until, b,key, false); }
  default int binarySearchGapR( T a, int from, int until, T b, int key ) { return binarySearchGap(a,from,until, b,key, true ); }
  default int binarySearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;

    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )    from = mid+1;
      else             until = mid;
    }
    return from;
  }
}
