package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

public interface ExpL2RSearchAccessor<T> extends CompareAccessor<T>
{
  public default int expL2RSearch( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c <  0 )   { until = k; break; }
                        from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ) {    int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return ~from;
  }

  public default int expL2RSearchR( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k);
      if( c < 0 )    { until = k; break; }
      found |= 0==c;    from = k+1;

    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  public default int expL2RSearchL( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k); found |= 0==c;
      if( c <= 0 )   { until = k; break; }
                        from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  public default int expL2RSearchGap( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k);
      if( c == 0 )      return k;
      if( c <  0 )   { until = k; break; }
                        from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ) {    int mid = from+until >>> 1,
               c = compare(b,key, a,mid);
           if( c < 0 ) until = -1 + mid;
      else if( c > 0 )  from = +1 + mid;
      else                   return mid;
    }
    return from;
  }

  public default int expL2RSearchGapR( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k);
      if( c < 0 )    { until = k; break; }
                        from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c < 0 ) until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }

  public default int expL2RSearchGapL( T a, int from, int until, T b, int key )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
          c = compare(b,key, a,k);
      if( c <= 0 )   { until = k; break; }
                        from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ){int mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c <= 0 )until = -1 + mid;
      else         from = +1 + mid;
    }
    return from;
  }
}
