package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

import static java.lang.Math.min;

public interface GallopR2LSearchAccessor<T> extends CompareAccessor<T>
{
  default int gallopR2LSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    int  len = until-from;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( lo < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = until-1-lo,
          c = compare(b,key, a,next);
      if( c == 0 ) return      next;
      if( c >  0 ) break;
      hi =   lo+1;
      lo = 2*lo+1;
    }
    lo = until - min(lo,len);
    hi = until - hi;

    // BINARY SEARCH PHASE
    while( lo < hi ) {          int mid = lo+hi >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )         hi = mid;
      else if( c > 0 )         lo = mid+1;
      else                   return mid;
    }
    return ~lo;
  }

  default int gallopR2LSearchL( T a, int from, int until, T b, int key ) { return gallopR2LSearch(a,from,until, b,key, false); }
  default int gallopR2LSearchR( T a, int from, int until, T b, int key ) { return gallopR2LSearch(a,from,until, b,key, true ); }
  default int gallopR2LSearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    int  len = until-from,
        bias = rightBias ? -1 : 0;
    boolean found = false;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( lo < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = until-1-lo,
          c = compare(b,key, a,next); found |= 0==c;
      if( c > bias ) break;
      hi =   lo+1;
      lo = 2*lo+1;
    }
    lo = until - min(lo,len);
    hi = until - hi;

    // BINARY SEARCH PHASE
    while( lo < hi ) {     int mid = lo+hi >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )      lo = mid+1;
      else                hi = mid;
      found |= 0==c;
    }
    return found ? lo : ~lo;
  }


  default int gallopR2LSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    int  len = until-from;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( lo < len ) { // <- make step have all bits set such that binary search is optimally efficient
      int                      next = until-1-lo,
          c = compare(b,key, a,next);
      if( c == 0 ) return      next;
      if( c >  0 ) break;
      hi =   lo+1;
      lo = 2*lo+1;
    }
    lo = until - min(lo,len);
    hi = until - hi;

    // BINARY SEARCH PHASE
    while( lo < hi ) {          int mid = lo+hi >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )         hi = mid;
      else if( c > 0 )         lo = mid+1;
      else                   return mid;
    }
    return lo;
  }

  default int gallopR2LSearchGapL( T a, int from, int until, T b, int key ) { return gallopR2LSearchGap(a,from,until, b,key, false); }
  default int gallopR2LSearchGapR( T a, int from, int until, T b, int key ) { return gallopR2LSearchGap(a,from,until, b,key, true ); }
  default int gallopR2LSearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
    int  len = until-from,
        bias = rightBias ? 0 : 1;

    // GALLOPING PHASE
    int lo=0,
        hi=0;
    while( lo < len && compare(b,key, a,until-1-lo) < bias ) { // <- make step have all bits set such that binary search is optimally efficient
      hi =   lo+1;
      lo = 2*lo+1;
    }
    lo = until - min(lo,len);
    hi = until - hi;

    // BINARY SEARCH PHASE
    while( lo < hi ) {     int mid = lo+hi >>> 1,
          c = compare(b,key, a,mid);
      if( c < bias )      hi = mid;
      else                lo = mid+1;
    }
    return lo;
  }
}
