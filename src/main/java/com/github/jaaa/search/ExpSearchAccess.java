package com.github.jaaa.search;

public interface ExpSearchAccess extends ExpL2RSearchAccess,
                                         ExpR2LSearchAccess
{
  default int expSearch( int from, int until, int start, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compare(key,start)) )
      return start;

    // GALLOPING PHASE
    // ---------------
    if( c < 0 )
    {
      // gallop right to left
                              until = start;
      for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int                  k = until-step;
            c = compare(key, k);
        if( c == 0 )  return k;
        if( c >  0 ){ from = k+1; break; }
                     until = k;
      }
    }
    else
    {
      // gallop left to right
                                    from = start+1;
      for( int step=0; step < until-from;step = 1 + 2*step ) { // <- make step have all bits set such that binary search is optimally efficient
        int                  next = from+step;
            c = compare(key, next);
        if( c == 0 )  return next;
        if( c <  0 ){until = next; break; }
                      from = next+1;
      }
    }

    // BINARY SEARCH PHASE
    // -------------------
    while( from < until ) { int mid = from+until >>> 1;
               c = compare(key, mid);
           if( c < 0 )  until = mid;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }

    return ~from;
  }

  default int expSearchR( int from, int until, int start, int key ) { return expSearch(from,until, start, key, true ); }
  default int expSearchL( int from, int until, int start, int key ) { return expSearch(from,until, start, key, false); }
  default int expSearch ( int from, int until, int start, int key, boolean rightBias )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compare(key,start);

    int bias = rightBias ? -1 : 0;
    boolean found = 0==c;

    // GALLOPING PHASE
    // ---------------
    if( c > bias )
    {
      // gallop left to right
                                    from = start+1;
      for( int step=0; step < until-from;step = 1 | step<<1 ) { // <- make step have all bits set such that binary search is optimally efficient
        int                  next = from+step;
            c = compare(key, next); found |= 0==c;
        if( c > bias )from = next+1;
        else       { until = next; break; }
      }
    }
    else
    {
      // gallop right to left
                              until = start;
      for( int step=1; step < until-from; step <<= 1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int                    k = until-step;
            c  =  compare(key, k); found |= 0==c;
        if( c > bias ){ from = k+1; break; }
                       until = k;
      }
    }

    // BINARY SEARCH PHASE
    while(from < until){ int mid = from+until >>> 1;
          c  =  compare(key, mid);
      if( c > bias )  from = mid+1;
      else           until = mid;
      found |= 0==c;
    }

    return found ? from : ~from;
  }


  default int expSearchGap( int from, int until, int start, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compare(key,start)) )
      return start;

    // GALLOPING PHASE
    // ---------------
    if( c < 0 )
    {
      // gallop right to left
                              until = start;
      for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int                  k = until-step;
        c = compare(key, k);
        if( c == 0 )  return k;
        if( c >  0 ){ from = k+1; break; }
        until = k;
      }
    }
    else
    {
      // gallop left to right
                                    from = start+1;
      for( int step=0; step < until-from;step = 1 + 2*step ) { // <- make step have all bits set such that binary search is optimally efficient
        int                  next = from+step;
            c = compare(key, next);
        if( c == 0 )  return next;
        if( c <  0 ){until = next; break; }
        from = next+1;
      }
    }

    // BINARY SEARCH PHASE
    // -------------------
    while( from < until ) { int mid = from+until >>> 1;
               c = compare(key, mid);
           if( c < 0 )  until = mid;
      else if( c > 0 )   from = mid+1;
      else               return mid;
    }

    return from;
  }

  default int expSearchGapL( int from, int until, int start, int key ) { return expSearchGap(from,until, start, key, false); }
  default int expSearchGapR( int from, int until, int start, int key ) { return expSearchGap(from,until, start, key, true ); }
  default int expSearchGap ( int from, int until, int start, int key, boolean rightBias )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if(  from == until ) return from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compare(key,start);

    int bias = rightBias ? -1 : 0;

    // GALLOPING PHASE
    // ---------------
    if( c > bias )
    {
      // gallop left to right
                                    from = start+1;
      for( int step=0; step < until-from;step = 1 | step<<1 ) { // <- make step have all bits set such that binary search is optimally efficient
        int                  next = from+step;
            c = compare(key, next);
        if( c > bias )from = next+1;
        else       { until = next; break; }
      }
    }
    else
    {
      // gallop right to left
                              until = start;
      for( int step=1; step < until-from; step <<= 1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int                    k = until-step;
            c  =  compare(key, k);
        if( c > bias ){ from = k+1; break; }
        until = k;
      }
    }

    // BINARY SEARCH PHASE
    while(from < until){ int mid = from+until >>> 1;
          c  =  compare(key, mid);
      if( c > bias )  from = mid+1;
      else           until = mid;
    }

    return from;
  }
}
