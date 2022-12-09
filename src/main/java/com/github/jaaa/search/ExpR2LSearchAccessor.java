package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccessor;

public interface ExpR2LSearchAccessor<T> extends CompareAccessor<T>
{
  default int expR2LSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c >  0 ){     from = k+1; break; }
                       until = k;
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

  default int expR2LSearchL( T a, int from, int until, T b, int key ) { return expR2LSearch(a,from,until, b,key, false); }
  default int expR2LSearchR( T a, int from, int until, T b, int key ) { return expR2LSearch(a,from,until, b,key, true ); }
  default int expR2LSearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=1; step < until-from; step <<= 1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k); found |= 0==c;
      if( c > bias ){   from = k+1; break; }
                       until = k;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )    from = mid+1;
      else             until = mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  default int expR2LSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c >  0 ) {    from = k+1; break; }
                       until = k;
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

  default int expR2LSearchGapL( T a, int from, int until, T b, int key ) { return expR2LSearchGap(a,from,until, b,key, false); }
  default int expR2LSearchGapR( T a, int from, int until, T b, int key ) { return expR2LSearchGap(a,from,until, b,key, true ); }
  default int expR2LSearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;

    // GALLOPING PHASE
    for( int step=1; step < until-from; step <<= 1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c > bias ) {  from = k+1; break; }
                       until = k;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c > bias )    from = mid+1;
      else             until = mid;
    }
    return from;
  }
}
