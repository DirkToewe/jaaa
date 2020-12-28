package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;

public interface ExpR2LSearchAccess extends CompareAccess
{
  default int expR2LSearchGap( int from, int until, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                 k = until-step,
          c = compare(key,k);
      if( c == 0 ) return k;
      if( c >  0 ){from = k+1; break; }
                  until = k-1;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) {   int mid = from + (until-from >>> 1),
                   c = compare(key,mid);
           if( 0 > c )until = -1 + mid;
      else if( 0 < c ) from = +1 + mid;
      else                  return mid;
    }
    return from;
  }

  default int expR2LSearchGapR( int from, int until, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                 k = until-step,
          c = compare(key,k);
      if( c >= 0 ){from = k+1; break; }
                  until = k-1;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from + (until-from >>> 1),
              c = compare(key, mid);
      if( 0 > c ) until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }

  default int expR2LSearchGapL( int from, int until, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                 k = until-step,
          c = compare(key,k);
      if( c > 0 ){ from = k+1; break; }
                  until = k-1;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){ int mid = from + (until-from >>> 1),
               c = compare(key, mid);
      if( 0 >= c ) until = -1 + mid;
      else          from = +1 + mid;
    }
    return from;
  }
}
