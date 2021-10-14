package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;

public interface AkimboSearchAccessor<T> extends CompareAccessor<T>
{
  default int akimboSearch( T a, int from, int until, T b, int key )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                      mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c == 0 )      return mid;

      // GALLOPING PHASE
      // ---------------
      if( c <  0 )
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = from+step);
          if( c == 0 )  return mid;
          if( c <  0 ){until = mid; break; }
                        from = mid+1;
        }
      }
      else
      { // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = until-step);
          if( c == 0 )      return mid;
          if( c >  0 )    { from = mid+1; break; }
                           until = mid;
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compare(b,key, a,mid = from+until >>> 1);
        if( c == 0 )      return mid;
        if( c >  0 )      from = mid+1;
        else             until = mid;
      }
    }
    return ~from;
  }

  default int akimboSearchL( T a, int from, int until, T b, int key ) { return akimboSearch(a,from,until, b,key, false); }
  default int akimboSearchR( T a, int from, int until, T b, int key ) { return akimboSearch(a,from,until, b,key, true ); }
  default int akimboSearch ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from ==until) return ~from;

    int                    mid = from+until >>> 1,
      c = compare(b,key, a,mid);

    boolean found = 0==c;

    // GALLOPING PHASE
    // ---------------
    int     bias = rightBias ? -1 : 0;
    if( c > bias )
    { // gallop right to left
                                    from = mid+1;
      for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compare(b,key, a,mid = until-step); found |= 0==c;
        if( c > bias )  { from = mid+1; break; }
                         until = mid;
      }
    }
    else
    { // galllop left to right
                              until = mid;
      for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compare(b,key, a,mid = from+step); found |= 0==c;
        if( c > bias )    from = mid+1;
        else           { until = mid; break; }
      }
    }

    // BINARY SEARCH PHASE
    // -------------------
    while( from < until ) {
          c = compare(b,key, a,mid = from+until >>> 1); found |= 0==c;
      if( c > bias )    from = mid+1;
      else             until = mid;
    }

    return found ? from : ~from;
  }

  default int akimboSearchGap( T a, int from, int until, T b, int key )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                      mid = from+until >>> 1,
          c = compare(b,key, a,mid);
      if( c == 0 )      return mid;

      // GALLOPING PHASE
      // ---------------
      if( c < 0 )
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = from+step);
          if( c == 0 )      return mid;
          if( c <  0 )   { until = mid; break; }
                            from = mid+1;
        }
      }
      else
      { // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = until-step);
          if( c == 0 )      return mid;
          if( c >  0 )    { from = mid+1; break; }
                           until = mid;
        }
      }

      // BINARY SEARCH PHASE
      while( from < until ) {
            c = compare(b,key, a,mid = from+until >>> 1);
        if( c == 0 ) return mid;
        if( c >  0 ) from = mid+1;
        else        until = mid;
      }
    }
    return from;
  }

  default int akimboSearchGapL( T a, int from, int until, T b, int key ) { return akimboSearchGap(a,from,until, b,key, false); }
  default int akimboSearchGapR( T a, int from, int until, T b, int key ) { return akimboSearchGap(a,from,until, b,key, true ); }
  default int akimboSearchGap ( T a, int from, int until, T b, int key, boolean rightBias )
  {
    if(from < 0 || from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                     mid = from+until >>> 1,
         c = compare(b,key, a,mid);

      // GALLOPING PHASE
      // ---------------
      int     bias = rightBias ? -1 : 0;
      if( c > bias )
      { // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = until-step);
          if( c > bias ){from = mid+1; break; }
                        until = mid;
        }
      }
      else
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compare(b,key, a,mid = from+step);
          if( c > bias )    from = mid+1;
          else           { until = mid; break; }
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compare(b,key, a,mid = from+until >>> 1);
        if( c > bias )    from = mid+1;
        else             until = mid;
      }
    }
    return from;
  }
}
