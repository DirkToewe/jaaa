package com.github.jaaa.search;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;


public class ExpSearch
{
  public interface ExpSearcher extends Searcher
  {
    int startIndex( int from, int until );

    @Override default int search ( int from, int until, IntUnaryOperator compass ) { return ExpSearch.search (from,until, startIndex(from,until), compass); }
    @Override default int searchL( int from, int until, IntUnaryOperator compass ) { return ExpSearch.searchL(from,until, startIndex(from,until), compass); }
    @Override default int searchR( int from, int until, IntUnaryOperator compass ) { return ExpSearch.searchR(from,until, startIndex(from,until), compass); }


    @Override default int searchGap ( int from, int until, IntUnaryOperator compass ) { return ExpSearch.searchGap (from,until, startIndex(from,until), compass); }
    @Override default int searchGapL( int from, int until, IntUnaryOperator compass ) { return ExpSearch.searchGapL(from,until, startIndex(from,until), compass); }
    @Override default int searchGapR( int from, int until, IntUnaryOperator compass ) { return ExpSearch.searchGapR(from,until, startIndex(from,until), compass); }


    @Override default <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return ExpSearch.search (   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return ExpSearch.search (from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.search (   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.search (from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }

    @Override default <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return ExpSearch.searchR(   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return ExpSearch.searchR(from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.searchR(   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.searchR(from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }

    @Override default <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return ExpSearch.searchL(   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return ExpSearch.searchL(from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.searchL(   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.searchL(from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }


    @Override default <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return ExpSearch.searchGap (   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return ExpSearch.searchGap (from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.searchGap (   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.searchGap (from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }

    @Override default <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return ExpSearch.searchGapR(   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return ExpSearch.searchGapR(from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.searchGapR(   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.searchGapR(from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }

    @Override default <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return ExpSearch.searchGapL(   0,seq.length, startIndex(   0,seq.length), i -> key.compareTo(seq[i]) ); }
    @Override default <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return ExpSearch.searchGapL(from,until,      startIndex(from,until     ), i -> key.compareTo(seq[i]) ); }
    @Override default <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpSearch.searchGapL(   0,seq.length, startIndex(   0,seq.length), i -> cmp.compare(key,seq[i]) ); }
    @Override default <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpSearch.searchGapL(from,until,      startIndex(from,until     ), i -> cmp.compare(key,seq[i]) ); }
  }


  public static int search( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    final int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compass.applyAsInt(start)) )
      return start;

    if( c < 0 ) return ExpR2LSearch.search(from,start,       compass);
    else        return ExpL2RSearch.search(   1+start,until, compass);
  }

  public static int searchR( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compass.applyAsInt(start);

    if( c < 0 )
      return ExpR2LSearch.searchR(from,start,       compass);
    int  i = ExpL2RSearch.searchR(   1+start,until, compass);
    return c==0 && ~(start+1)==i ? ~i : i;
  }

  public static int searchL( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from <  0     ) throw new IllegalArgumentException();
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return ~from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compass.applyAsInt(start);

    if( c > 0 )
      return ExpL2RSearch.searchL(   1+start,until, compass);
    int  i = ExpR2LSearch.searchL(from,start,       compass);
    return c==0 && ~start==i ? ~i : i;
  }


  public static int searchGap( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    final int c;
         if( start <  from ) { c = +1; }
    else if( start >= until) { c = -1; }
    else if( 0 == (c = compass.applyAsInt(start)) )
      return start;

    if( c <  0 ) return ExpR2LSearch.searchGap(from,start,       compass);
    else         return ExpL2RSearch.searchGap(   1+start,until, compass);
  }

  public static int searchGapR( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException( String.format("from: %d; until: %d; start: %d;", from, until, start) );
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compass.applyAsInt(start);

    if( c < 0 ) return ExpR2LSearch.searchGapR(from,start,       compass);
    else        return ExpL2RSearch.searchGapR(   1+start,until, compass);
  }

  public static int searchGapL( int from, int until, int start, IntUnaryOperator compass )
  {
    if(  from >  until ) throw new IllegalArgumentException();
    if( start <  from-1) throw new IllegalArgumentException();
    if( start >  until ) throw new IllegalArgumentException();
    if(  from == until ) return from;

    int c = start <  from  ? +1
          : start >= until ? -1
          : compass.applyAsInt(start);

    if( c <= 0 ) return ExpR2LSearch.searchGapL(from,start,       compass);
    else         return ExpL2RSearch.searchGapL(   1+start,until, compass);
  }


  public static <T extends Comparable<? super T>> int search ( T[] seq,                      T key, int start                            ) { return search (   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key, int start                            ) { return search (from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int search ( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return search (   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int search ( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return search (from,until,      start, i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchR( T[] seq,                      T key, int start                            ) { return searchR(   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key, int start                            ) { return searchR(from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchR( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return searchR(   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchR( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return searchR(from,until,      start, i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchL( T[] seq,                      T key, int start                            ) { return searchL(   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key, int start                            ) { return searchL(from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchL( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return searchL(   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchL( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return searchL(from,until,      start, i -> cmp.compare(key,seq[i]) ); }


  public static <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key, int start                            ) { return searchGap (   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key, int start                            ) { return searchGap (from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGap ( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return searchGap (   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGap ( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return searchGap (from,until,      start, i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key, int start                            ) { return searchGapR(   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key, int start                            ) { return searchGapR(from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGapR( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return searchGapR(   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGapR( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return searchGapR(from,until,      start, i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key, int start                            ) { return searchGapL(   0,seq.length, start, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key, int start                            ) { return searchGapL(from,until,      start, i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGapL( T[] seq,                      T key, int start, Comparator<? super T> cmp ) { return searchGapL(   0,seq.length, start, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGapL( T[] seq, int from, int until, T key, int start, Comparator<? super T> cmp ) { return searchGapL(from,until,      start, i -> cmp.compare(key,seq[i]) ); }
}
