package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;

public interface ExpL2RSearchAccessor<T> extends CompareAccessor<T>
{
  default int expL2RSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 | step<<1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      next = from+step,
          c = compare(b,key, a,next);
      if( c == 0 )      return next;
      if( c <  0 )   { until = next; break; }
                        from = next+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {     int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )      until = mid;
      else if( c > 0 )       from = mid+1;
      else                   return mid;
    }
    return ~from;
  }

  default int expL2RSearchL( T a, int from, int until, T b, int key ) { return expL2RSearch(a,from,until, b,key, false); }
  default int expL2RSearchR( T a, int from, int until, T b, int key ) { return expL2RSearch(a,from,until, b,key, true ); }
  default int expL2RSearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? 0 : 1;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 | step<<1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      next = from+step,
          c = compare(b,key, a,next); found |= 0==c;
      if( c < bias ) { until = next; break; }
                        from = next+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < bias )   until = mid;
      else              from = mid+1;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  default int expL2RSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 | step<<1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      next = from+step,
          c = compare(b,key, a,next);
      if( c == 0 )      return next;
      if( c <  0 )   { until = next; break; }
                        from = next+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {     int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 )      until = mid;
      else if( c > 0 )       from = mid+1;
      else                   return mid;
    }
    return from;
  }

  default int expL2RSearchGapL( T a, int from, int until, T b, int key ) { return expL2RSearchGap(a,from,until, b,key, false); }
  default int expL2RSearchGapR( T a, int from, int until, T b, int key ) { return expL2RSearchGap(a,from,until, b,key, true ); }
  default int expL2RSearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? 0 : 1;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 | step<<1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      next = from+step,
          c = compare(b,key, a,next);
      if( c < bias ) { until = next; break; }
                        from = next+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < bias )   until = mid;
      else              from = mid+1;
    }
    return from;
  }
}
