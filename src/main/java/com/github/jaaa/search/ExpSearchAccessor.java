package com.github.jaaa.search;

public interface ExpSearchAccessor<T> extends ExpR2LSearchAccessor<T>,
                                              ExpL2RSearchAccessor<T>
{
  default int expSearch( T a, int from, int until, int start, T b, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    final int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compare(b,key, a,start)) )
      return start;

    if( c < 0 ) return expR2LSearch(a,from,start,       b,key);
    else        return expL2RSearch(a,   1+start,until, b,key);
  }

  default int expSearchR( T a, int from, int until, int start, T b, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compare(b,key, a,start);

    if( c < 0 )
      return expR2LSearchR(a,from,start,       b,key);
    int  i = expL2RSearchR(a,   1+start,until, b,key);
    return c==0 && ~(start+1)==i ? ~i : i;
  }

  default int expSearchL( T a, int from, int until, int start, T b, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compare(b,key, a,start);

    if( c > 0 )
      return expL2RSearchL(a,   1+start,until, b,key);
    int  i = expR2LSearchL(a,from,start,       b,key);
    return c==0 && ~start==i ? ~i : i;
  }


  default int expSearchGap( T a, int from, int until, int start, T b, int key )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    final int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compare(b,key, a,start)) )
      return start;

    if( c < 0 ) return expR2LSearchGap(a,from,start,       b,key);
    else        return expL2RSearchGap(a,   1+start,until, b,key);
  }

  default int expSearchGapL( T a, int from, int until, int start, T b, int key ) { return expSearchGap(a,from,until, start, b,key, false); }
  default int expSearchGapR( T a, int from, int until, int start, T b, int key ) { return expSearchGap(a,from,until, start, b,key, true ); }
  default int expSearchGap ( T a, int from, int until, int start, T b, int key, boolean rightBias )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compare(b,key, a,start);

    if( c < (rightBias ? 0 : 1) ) return expR2LSearchGap(a,from,start,       b,key, rightBias);
    else                          return expL2RSearchGap(a,   1+start,until, b,key, rightBias);
  }
}
