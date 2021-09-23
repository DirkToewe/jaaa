package com.github.jaaa.search;

import com.github.jaaa.ComparatorByte;
import com.github.jaaa.ComparatorInt;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;


public class BinarySearch
{
  public static final Searcher BINARY_SEARCHER = new Searcher()
  {
    @Override public int search ( int from, int until, IntUnaryOperator compass ) { return BinarySearch.search (from,until, compass); }
    @Override public int searchL( int from, int until, IntUnaryOperator compass ) { return BinarySearch.searchL(from,until, compass); }
    @Override public int searchR( int from, int until, IntUnaryOperator compass ) { return BinarySearch.searchR(from,until, compass); }


    @Override public int searchGap ( int from, int until, IntUnaryOperator compass ) { return BinarySearch.searchGap (from,until, compass); }
    @Override public int searchGapL( int from, int until, IntUnaryOperator compass ) { return BinarySearch.searchGapL(from,until, compass); }
    @Override public int searchGapR( int from, int until, IntUnaryOperator compass ) { return BinarySearch.searchGapR(from,until, compass); }


    @Override public <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return BinarySearch.search (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return BinarySearch.search (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.search (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.search (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return BinarySearch.searchR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return BinarySearch.searchR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.searchR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.searchR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return BinarySearch.searchL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return BinarySearch.searchL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.searchL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.searchL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return BinarySearch.searchGap (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return BinarySearch.searchGap (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.searchGap (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.searchGap (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return BinarySearch.searchGapR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return BinarySearch.searchGapR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.searchGapR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.searchGapR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return BinarySearch.searchGapL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return BinarySearch.searchGapL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return BinarySearch.searchGapL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return BinarySearch.searchGapL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public int search ( byte[] seq,                      byte key                     ) { return BinarySearch.search (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.search (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.search (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.search (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchR( byte[] seq,                      byte key                     ) { return BinarySearch.searchR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.searchR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.searchR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.searchR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchL( byte[] seq,                      byte key                     ) { return BinarySearch.searchL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.searchL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.searchL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.searchL(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGap ( byte[] seq,                      byte key                     ) { return BinarySearch.searchGap (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.searchGap (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.searchGap (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.searchGap (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapR( byte[] seq,                      byte key                     ) { return BinarySearch.searchGapR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.searchGapR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.searchGapR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.searchGapR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapL( byte[] seq,                      byte key                     ) { return BinarySearch.searchGapL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key                     ) { return BinarySearch.searchGapL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq,                      byte key, ComparatorByte cmp ) { return BinarySearch.searchGapL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return BinarySearch.searchGapL(from,until,      i ->  cmp.compare(key,seq[i]) ); }


    @Override public int search ( int[] seq,                      int key                    ) { return search (   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int search ( int[] seq, int from, int until, int key                    ) { return search (from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int search ( int[] seq,                      int key, ComparatorInt cmp ) { return search (   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int search ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return search (from,until,      i ->     cmp.compare(key,seq[i]) ); }

    @Override public int searchR( int[] seq,                      int key                    ) { return searchR(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchR( int[] seq, int from, int until, int key                    ) { return searchR(from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchR( int[] seq,                      int key, ComparatorInt cmp ) { return searchR(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int searchR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchR(from,until,      i ->     cmp.compare(key,seq[i]) ); }

    @Override public int searchL( int[] seq,                      int key                    ) { return searchL(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchL( int[] seq, int from, int until, int key                    ) { return searchL(from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchL( int[] seq,                      int key, ComparatorInt cmp ) { return searchL(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int searchL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchL(from,until,      i ->     cmp.compare(key,seq[i]) ); }

    @Override public int searchGap ( int[] seq,                      int key                    ) { return searchGap (   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGap ( int[] seq, int from, int until, int key                    ) { return searchGap (from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGap ( int[] seq,                      int key, ComparatorInt cmp ) { return searchGap (   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int searchGap ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGap (from,until,      i ->     cmp.compare(key,seq[i]) ); }

    @Override public int searchGapR( int[] seq,                      int key                    ) { return searchGapR(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGapR( int[] seq, int from, int until, int key                    ) { return searchGapR(from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGapR( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapR(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int searchGapR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapR(from,until,      i ->     cmp.compare(key,seq[i]) ); }

    @Override public int searchGapL( int[] seq,                      int key                    ) { return searchGapL(   0,seq.length, i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGapL( int[] seq, int from, int until, int key                    ) { return searchGapL(from,until,      i -> Integer.compare(key,seq[i]) ); }
    @Override public int searchGapL( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapL(   0,seq.length, i ->     cmp.compare(key,seq[i]) ); }
    @Override public int searchGapL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapL(from,until,      i ->     cmp.compare(key,seq[i]) ); }
  };


  public static int search( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    while( from < until ) {       int mid = from+until >>> 1,
               c = compass.applyAsInt(mid);
           if( c < 0 )        until = mid;
      else if( c > 0 )         from = mid+1;
      else                     return mid;
    }
    return ~from;
  }

  public static int searchR( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    boolean found = false;
    while( from < until ) {  int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c < 0 )        until = mid;
      else                from = mid+1;
      found |= 0==c;
    }
    return found ? from : ~from;
  }

  public static int searchL( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0     ) throw new IllegalArgumentException();
    if( from > until ) throw new IllegalArgumentException();

    boolean found = false;
    while( from < until ) {  int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c <= 0 )       until = mid;
      else                from = mid+1;
      found |= 0==c;
    }
    return found ? from : ~from;
  }


  public static int searchGap ( int from, int until, IntUnaryOperator compass )
  {
    if( from > until ) throw new IllegalArgumentException();

    while( from < until ) {       int mid = from + (until-from >>> 1),
               c = compass.applyAsInt(mid);
           if( c < 0 )        until = mid;
      else if( c > 0 )         from = mid+1;
      else                     return mid;
    }
    return from;
  }

  public static int searchGapR( int from, int until, IntUnaryOperator compass )
  {
    if( from > until ) throw new IllegalArgumentException();

    while( from < until ) {  int mid = from + (until-from >>> 1),
          c = compass.applyAsInt(mid);
      if( c < 0 )   until =      mid;
      else           from =      mid+1;
    }
    return from;
  }

  public static int searchGapL( int from, int until, IntUnaryOperator compass )
  {
    if( from > until ) throw new IllegalArgumentException();

    while( from < until ) {  int mid = from + (until-from >>> 1),
          c = compass.applyAsInt(mid);
      if( c <= 0 )       until = mid;
      else                from = mid+1;
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
