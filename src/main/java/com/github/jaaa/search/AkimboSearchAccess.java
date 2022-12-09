package com.github.jaaa.search;

import com.github.jaaa.compare.CompareAccess;


public interface AkimboSearchAccess extends CompareAccess
{
  default int akimboSearch( int from, int until, int key )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                 mid = from+until >>> 1,
          c = compare(key,mid);
      if( c == 0 ) return mid;

      // GALLOPING PHASE
      // ---------------
      if( c < 0 )
      {
        // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key, mid = from+step);
          if( c == 0 )  return mid;
          if( c <  0 ){until = mid; break; }
                        from = mid+1;
        }
      }
      else
      {
        // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key, mid = until-step);
          if( c == 0 )  return mid;
          if( c >  0 ){ from = mid+1; break; }
                       until = mid;
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compare(key,mid = from+until >>> 1);
        if( c == 0 ) return mid;
        if( c >  0 ) from = mid+1;
        else        until = mid;
      }
    }
    return ~from;
  }

  default int akimboSearchL( int from, int until, int key ) { return akimboSearch(from,until, key, false); }
  default int akimboSearchR( int from, int until, int key ) { return akimboSearch(from,until, key, true ); }
  default int akimboSearch ( int from, int until, int key, boolean rightBias )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from == until) return ~from;

    int               mid = from+until >>> 1,
      c = compare(key,mid);

    boolean found = 0==c;

    // GALLOPING PHASE
    // ---------------
    int     bias = rightBias ? -1 : 0;
    if( c > bias )
    { // gallop right to left
                                    from = mid+1;
      for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compare(key,  mid = until-step); found |= 0==c;
        if( c > bias ){from = mid+1; break; }
                      until = mid;
      }
    }
    else
    { // gallop left to right
                              until = mid;
      for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compare(key,  mid = from+step); found |= 0==c;
        if( c > bias ) from = mid+1;
        else        { until = mid; break; }
      }
    }

    // BINARY SEARCH PHASE
    // -------------------
    while( from < until ) {
          c = compare(key,  mid = from+until >>> 1); found |= 0==c;
      if( c > bias ) from = mid+1;
      else          until = mid;
    }

    return found ? from : ~from;
  }

  default int akimboSearchGap( int from, int until, int key )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                 mid = from+until >>> 1,
          c = compare(key,mid);
      if( c == 0 ) return mid;

      // GALLOPING PHASE
      // ---------------
      if( c <  0 )
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key, mid = from+step);
          if( c == 0 )  return mid;
          if( c <  0 ){until = mid; break; }
                        from = mid+1;
        }
      }
      else
      {
        // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key, mid = until-step);
          if( c == 0 )  return mid;
          if( c >  0 ){ from = mid+1; break; }
                       until = mid;
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compare(key,mid = from+until >>> 1);
        if( c == 0 ) return mid;
        if( c >  0 ) from = mid+1;
        else        until = mid;
      }
    }
    return from;
  }

  default int akimboSearchGapL( int from, int until, int key ) { return akimboSearchGap(from,until, key, false); }
  default int akimboSearchGapR( int from, int until, int key ) { return akimboSearchGap(from,until, key, true ); }
  default int akimboSearchGap ( int from, int until, int key, boolean rightBias )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                 mid = from+until >>> 1,
          c = compare(key,mid);

      // GALLOPING PHASE
      // ---------------
      int     bias = rightBias ? -1 : 0;
      if( c > bias )
      { // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key,  mid = until-step);
          if( c > bias ){from = mid+1; break; }
                        until = mid;
        }
      }
      else
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(key,  mid = from+step);
          if( c > bias ) from = mid+1;
          else        { until = mid; break; }
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compare(key, mid = from+until >>> 1);
        if( c > bias ) from = mid+1;
        else          until = mid;
      }
    }
    return from;
  }
}
