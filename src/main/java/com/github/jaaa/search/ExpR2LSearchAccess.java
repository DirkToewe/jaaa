package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

public interface ExpR2LSearchAccess extends CompareAccess
{
  default int expR2LSearch( int from, int until, int key )
  {
    if( key < 0    ) throw new IllegalArgumentException();
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                  k = until-step,
          c = compare(key, k);
      if( c == 0 )  return k;
      if( c >  0 ){ from = k+1; break; }
                   until = k;
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

  default int expR2LSearchL( int from, int until, int key ) { return expR2LSearch(from,until, key, false); }
  default int expR2LSearchR( int from, int until, int key ) { return expR2LSearch(from,until, key, true ); }
  default int expR2LSearch ( int from, int until, int key, boolean rightBias )
  {
    if( key < 0    ) throw new IllegalArgumentException();
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=1; step < until-from; step <<= 1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                    k = until-step,
          c  =  compare(key, k); found |= 0==c;
      if( c > bias ){ from = k+1; break; }
                     until = k;
    }

    // BINARY SEARCH PHASE
    while(from < until){ int mid = from+until >>> 1,
          c  =  compare(key, mid);
      if( c > bias )  from = mid+1;
      else           until = mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  default int expR2LSearchGap( int from, int until, int key )
  {
    if( key < 0    ) throw new IllegalArgumentException();
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                 k = until-step,
          c = compare(key,k);
      if( c == 0 ) return k;
      if( c >  0 ){from = k+1; break; }
                  until = k;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
               c = compare(key,mid);
           if( c < 0 ) until = mid;
      else if( c > 0 )  from = mid+1;
      else              return mid;
    }
    return from;
  }

  default int expR2LSearchGapL( int from, int until, int key ) { return expR2LSearchGap(from,until, key, false); }
  default int expR2LSearchGapR( int from, int until, int key ) { return expR2LSearchGap(from,until, key, true ); }
  default int expR2LSearchGap ( int from, int until, int key, boolean rightBias )
  {
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();

    int bias = rightBias ? -1 : 0;

    // GALLOPING PHASE
    for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                   k = until-step,
          c  =  compare(key,k);
      if( c > bias ){from = k+1; break; }
                    until = k;
    }

    // BINARY SEARCH PHASE
    while( from < until ){ int mid = from+until >>> 1,
          c   =   compare(key, mid);
      if( c > bias )    from = mid+1;
      else             until = mid;
    }
    return from;
  }
}
