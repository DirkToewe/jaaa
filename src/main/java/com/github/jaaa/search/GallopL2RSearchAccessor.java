package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;

import static java.lang.Math.min;


public interface GallopL2RSearchAccessor<T> extends CompareAccessor<T>
{
  default int gallopL2RSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    int len = until-from;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( hi < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = from+hi;
      int c = compare(b,key, a,next);
      if( c == 0 ) return      next;
      if( c <  0 ) break;
      lo =   hi+1;
      hi = 2*hi+1;
    }
    hi = from + min(hi,len);
    lo+= from;

    // BINARY SEARCH PHASE
    while( lo < hi ) {          int mid = lo+hi >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )         hi = mid;
      else if( c > 0 )         lo = mid+1;
      else                   return mid;
    }
    return ~lo;
  }

  default int gallopL2RSearchL( T a, int from, int until, T b, int key ) { return gallopL2RSearch(a,from,until, b,key, false); }
  default int gallopL2RSearchR( T a, int from, int until, T b, int key ) { return gallopL2RSearch(a,from,until, b,key, true ); }
  default int gallopL2RSearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    int len = until-from,
       bias = rightBias ? 0 : 1;
    boolean found = false;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( hi < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = from+hi,
          c = compare(b,key, a,next); found |= 0==c;
      if( c < bias ) break;
      lo =   hi+1;
      hi = 2*hi+1;
    }
    hi = from + min(hi,len);
    lo+= from;

    // BINARY SEARCH PHASE
    while( lo < hi ) {     int mid = lo+hi >>> 1,
          c = compare(b,key, a,mid);
      if( c < bias )      hi = mid;
      else                lo = mid+1;
      found |= 0==c;
    }
    return found ? lo : ~lo;
  }

  default int gallopL2RSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    int  len = until-from;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( hi < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = from+hi;
      int c = compare(b,key, a,next);
      if( c == 0 ) return      next;
      if( c <  0 ) break;
      lo =   hi+1;
      hi = 2*hi+1;
    }
    hi = from + min(hi,len);
    lo+= from;

    // BINARY SEARCH PHASE
    while( lo < hi ) {          int mid = lo+hi >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )         hi = mid;
      else if( c > 0 )         lo = mid+1;
      else                   return mid;
    }
    return lo;
  }

  default int gallopL2RSearchGapL( T a, int from, int until, T b, int key ) { return gallopL2RSearchGap(a,from,until, b,key, false); }
  default int gallopL2RSearchGapR( T a, int from, int until, T b, int key ) { return gallopL2RSearchGap(a,from,until, b,key, true ); }
  default int gallopL2RSearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
    int len = until-from,
       bias = rightBias ? -1 : 0;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( hi < len && compare(b,key, a,from+hi) > bias ) { // <- make step have all bits set such that binary search is optimally efficient
      lo =   hi+1;
      hi = 2*hi+1;
    }
    hi = from + min(hi,len);
    lo+= from;

    // BINARY SEARCH PHASE
    while( lo < hi ) {     int mid = lo+hi >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )      lo = mid+1;
      else                hi = mid;
    }
    return lo;
  }
}
