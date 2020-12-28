package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

public interface ExpR2LSearchAccessor<T> extends CompareAccessor<T>
{
  public default int expR2LSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c >  0 ){from = +1 + k; break; }
                  until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) {    int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return ~from;
  }

  public default int expR2LSearchR( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k); found |= 0==c;
      if( c >= 0 ){from = +1 + k; break; }
                  until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  public default int expR2LSearchL( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c > 0 ){ from = +1 + k; break; }
                  until = -1 + k;
      found |= 0==c;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  public default int expR2LSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c >  0 ){from = +1 + k; break; }
                  until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) {    int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return from;
  }

  public default int expR2LSearchGapR( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c >= 0 ){from = +1 + k; break; }
                  until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }

  public default int expR2LSearchGapL( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();
             --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = until-step,
          c = compare(b,key, a,k);
      if( c > 0 ){ from = +1 + k; break; }
                  until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }
}
