package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

public interface ExpL2RSearchAccess extends CompareAccess
{
  default int expL2RSearch( int from, int until, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                  next = from+step,
          c = compare(key, next);
      if( c == 0 )  return next;
      if( c <  0 ){until = next; break; }
                    from = next+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) { int mid = from+until >>> 1,
               c = compare(key, mid);
           if( c < 0 )  until = mid;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }
    return ~from;
  }

  default int expL2RSearchL( int from, int until, int key ) { return expL2RSearch(from,until, key, false); }
  default int expL2RSearchR( int from, int until, int key ) { return expL2RSearch(from,until, key, true ); }
  default int expL2RSearch ( int from, int until, int key, boolean rightBias )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? 0 : 1;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                  next = from+step,
          c = compare(key, next); found |= 0==c;
      if( c < bias ){until=next; break; }
                    from = next+1;
    }

    // BINARY SEARCH PHASE
    while(from < until){ int mid = from+until >>> 1,
          c  =  compare(key, mid);
      if( c < bias ) until = mid;
      else            from = mid+1;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  default int expL2RSearchGap( int from, int until, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
    {
      int                  k = from+step,
          c = compare(key, k);
      if( c == 0 )  return k;
      if( c <  0 ){until = k; break; }
                    from = k+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {    int mid = from+until >>> 1,
                   c = compare(key,mid);
           if( 0 > c )     until = mid;
      else if( 0 < c )      from = mid+1;
      else                  return mid;
    }

    return from;
  }

  default int expL2RSearchGapL( int from, int until, int key ) { return expL2RSearchGap(from,until, key, false); }
  default int expL2RSearchGapR( int from, int until, int key ) { return expL2RSearchGap(from,until, key, true ); }
  default int expL2RSearchGap ( int from, int until, int key, boolean rightBias )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? 0 : 1;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c   =   compare(key, k);
      if( c < bias ) { until = k; break; }
                        from = k+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c   =   compare(key, mid);
      if( c < bias )   until = mid;
      else              from = mid+1;
    }

    return from;
  }
}
