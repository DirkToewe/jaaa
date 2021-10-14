package com.github.jaaa.search;

import com.github.jaaa.ComparatorByte;
import com.github.jaaa.ComparatorInt;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;


public class ExpR2LSearch
{
  public static final Searcher EXP_R2L_SEARCHER = new Searcher()
  {
    @Override public int search ( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.search (from,until, compass); }
    @Override public int searchL( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.searchL(from,until, compass); }
    @Override public int searchR( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.searchR(from,until, compass); }


    @Override public int searchGap ( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.searchGap (from,until, compass); }
    @Override public int searchGapL( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.searchGapL(from,until, compass); }
    @Override public int searchGapR( int from, int until, IntUnaryOperator compass ) { return ExpR2LSearch.searchGapR(from,until, compass); }


    @Override public <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return ExpR2LSearch.search (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.search (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.search (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.search (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return ExpR2LSearch.searchR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.searchR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return ExpR2LSearch.searchL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.searchL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return ExpR2LSearch.searchGap (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.searchGap (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGap (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGap (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return ExpR2LSearch.searchGapR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.searchGapR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGapR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGapR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return ExpR2LSearch.searchGapL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return ExpR2LSearch.searchGapL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGapL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return ExpR2LSearch.searchGapL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public int search ( byte[] seq,                      byte key                     ) { return ExpR2LSearch.search (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.search (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.search (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.search (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchR( byte[] seq,                      byte key                     ) { return ExpR2LSearch.searchR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.searchR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchL( byte[] seq,                      byte key                     ) { return ExpR2LSearch.searchL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.searchL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchL(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGap ( byte[] seq,                      byte key                     ) { return ExpR2LSearch.searchGap (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.searchGap (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGap (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGap (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapR( byte[] seq,                      byte key                     ) { return ExpR2LSearch.searchGapR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.searchGapR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGapR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGapR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapL( byte[] seq,                      byte key                     ) { return ExpR2LSearch.searchGapL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key                     ) { return ExpR2LSearch.searchGapL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq,                      byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGapL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return ExpR2LSearch.searchGapL(from,until,      i ->  cmp.compare(key,seq[i]) ); }
  };


  public static int search( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    // GALLOPING PHASE
    for( int step=0; step <= until-from;  step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c == 0 )        return k;
      if( c >  0 ) { from = +1 + k; break; }
                    until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) {      int mid = from+until >>> 1,
               c = compass.applyAsInt(mid);
           if( c < 0 )   until = -1 + mid;
      else if( c > 0 )    from = +1 + mid;
      else                     return mid;
    }
    return ~from;
  }

  public static int searchR( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    boolean found = false;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c >= 0 ) { from = +1 + k; found = 0==c; break; }
                    until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c < 0 )   until = -1 + mid;
      else           from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  public static int searchL( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    boolean found = false;
    // GALLOPING PHASE
    for( int step=0; step <= until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c > 0 )  { from = +1 + k; break; }
      found |= 0==c;until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c <= 0 )  until = -1 + mid;
      else           from = +1 + mid;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  public static int searchGap( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    // GALLOPING PHASE
    for( int step=0; MIN_VALUE+step < until-from-MAX_VALUE;  step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c == 0 )        return k;
      if( c >  0 ) { from = +1 + k; break; }
                    until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) {      int mid = from + (until-from >>> 1),
               c = compass.applyAsInt(mid);
           if( c < 0 )   until = -1 + mid;
      else if( c > 0 )    from = +1 + mid;
      else                     return mid;
    }
    return from;
  }

  public static int searchGapR( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    // GALLOPING PHASE
    for( int step=0; MIN_VALUE+step < until-from-MAX_VALUE; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c >= 0 ) { from = +1 + k; break; }
                    until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from + (until-from >>> 1),
          c = compass.applyAsInt(mid);
      if( c < 0 )   until = -1 + mid;
      else           from = +1 + mid;
    }
    return from;
  }

  public static int searchGapL( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();
                         --until;
    // GALLOPING PHASE
    for( int step=0; MIN_VALUE+step < until-from-MAX_VALUE; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = until-step,
          c = compass.applyAsInt(k);
      if( c > 0 )  { from = +1 + k; break; }
                    until = -1 + k;
    }

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from + (until-from >>> 1),
          c = compass.applyAsInt(mid);
      if( c <= 0 )  until = -1 + mid;
      else           from = +1 + mid;
    }
    return from;
  }


  public static <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return search (   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return search (from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return search (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return search (from,until,      i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return searchR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return searchR(from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchR(from,until,      i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return searchL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return searchL(from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchL(from,until,      i -> cmp.compare(key,seq[i]) ); }


  public static <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return searchGap (   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return searchGap (from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGap (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGap (from,until,      i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return searchGapR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return searchGapR(from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapR(from,until,      i -> cmp.compare(key,seq[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return searchGapL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return searchGapL(from,until,      i -> key.compareTo(seq[i]) ); }
  public static <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
  public static <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapL(from,until,      i -> cmp.compare(key,seq[i]) ); }


  public static int search ( byte[] seq,                      byte key                     ) { return search (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int search ( byte[] seq, int from, int until, byte key                     ) { return search (from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int search ( byte[] seq,                      byte key, ComparatorByte cmp ) { return search (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int search ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return search (from,until,      i ->  cmp.compare(key,seq[i]) ); }

  public static int searchR( byte[] seq,                      byte key                     ) { return searchR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int searchR( byte[] seq, int from, int until, byte key                     ) { return searchR(from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int searchR( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int searchR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

  public static int searchL( byte[] seq,                      byte key                     ) { return searchL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int searchL( byte[] seq, int from, int until, byte key                     ) { return searchL(from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int searchL( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int searchL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchL(from,until,      i ->  cmp.compare(key,seq[i]) ); }

  public static int searchGap ( byte[] seq,                      byte key                     ) { return searchGap (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int searchGap ( byte[] seq, int from, int until, byte key                     ) { return searchGap (from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int searchGap ( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGap (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int searchGap ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGap (from,until,      i ->  cmp.compare(key,seq[i]) ); }

  public static int searchGapR( byte[] seq,                      byte key                     ) { return searchGapR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int searchGapR( byte[] seq, int from, int until, byte key                     ) { return searchGapR(from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int searchGapR( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGapR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int searchGapR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGapR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

  public static int searchGapL( byte[] seq,                      byte key                     ) { return searchGapL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
  public static int searchGapL( byte[] seq, int from, int until, byte key                     ) { return searchGapL(from,until,      i -> Byte.compare(key,seq[i]) ); }
  public static int searchGapL( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGapL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
  public static int searchGapL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGapL(from,until,      i ->  cmp.compare(key,seq[i]) ); }


  public static int search ( int[] seq,                      int key                    ) { return search (   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int search ( int[] seq, int from, int until, int key                    ) { return search (from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int search ( int[] seq,                      int key, ComparatorInt cmp ) { return search (   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int search ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return search (from,until,      i ->     cmp.compare(key,seq[i]) ); }

  public static int searchR( int[] seq,                      int key                    ) { return searchR(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int searchR( int[] seq, int from, int until, int key                    ) { return searchR(from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int searchR( int[] seq,                      int key, ComparatorInt cmp ) { return searchR(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int searchR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchR(from,until,      i ->     cmp.compare(key,seq[i]) ); }

  public static int searchL( int[] seq,                      int key                    ) { return searchL(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int searchL( int[] seq, int from, int until, int key                    ) { return searchL(from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int searchL( int[] seq,                      int key, ComparatorInt cmp ) { return searchL(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int searchL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchL(from,until,      i ->     cmp.compare(key,seq[i]) ); }

  public static int searchGap ( int[] seq,                      int key                    ) { return searchGap (   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int searchGap ( int[] seq, int from, int until, int key                    ) { return searchGap (from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int searchGap ( int[] seq,                      int key, ComparatorInt cmp ) { return searchGap (   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int searchGap ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGap (from,until,      i ->     cmp.compare(key,seq[i]) ); }

  public static int searchGapR( int[] seq,                      int key                    ) { return searchGapR(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int searchGapR( int[] seq, int from, int until, int key                    ) { return searchGapR(from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int searchGapR( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapR(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int searchGapR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapR(from,until,      i ->     cmp.compare(key,seq[i]) ); }

  public static int searchGapL( int[] seq,                      int key                    ) { return searchGapL(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
  public static int searchGapL( int[] seq, int from, int until, int key                    ) { return searchGapL(from,until,      i -> Integer.compare(key,seq[i]) ); }
  public static int searchGapL( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapL(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
  public static int searchGapL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapL(from,until,      i ->     cmp.compare(key,seq[i]) ); }
}
