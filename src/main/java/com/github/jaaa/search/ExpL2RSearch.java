package com.github.jaaa.search;

import com.github.jaaa.compare.ComparatorByte;
import com.github.jaaa.compare.ComparatorInt;

import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.function.IntUnaryOperator;


/** Implementation of the left-to-right Exponential Search a.k.a. Galloping Search algorithm.
  * Exponential search is a specialized extension of the Binary Search algorithm. In the worst case,
  * left-to-right Exponential Search requires roughly twice as many comparisons as Binary Search.
  * If however the searched element is close to the left end (i.e. the beginning) of the searched range,
  * left-to-right Exponential Search is significantly faster than Binary Search. More specifically:
  * if the searched element is <code>n</code> elements away from the beginning, it takes
  * <code>O(log(n))</code> comparisons to find it.
  */
public class ExpL2RSearch
{
  public static final Searcher EXP_L2R_SEARCHER = new Searcher()
  {
    @Override public int search ( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.search (from,until, compass); }
    @Override public int searchL( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.searchL(from,until, compass); }
    @Override public int searchR( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.searchR(from,until, compass); }


    @Override public int searchGap ( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.searchGap (from,until, compass); }
    @Override public int searchGapL( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.searchGapL(from,until, compass); }
    @Override public int searchGapR( int from, int until, IntUnaryOperator compass ) { return ExpL2RSearch.searchGapR(from,until, compass); }


    @Override public <T extends Comparable<? super T>> int search ( T[] arr,                      T key                            ) { return ExpL2RSearch.search (   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int search ( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.search (from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int search ( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.search (   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int search ( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.search (from,until,      i -> cmp.compare(key,arr[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchR( T[] arr,                      T key                            ) { return ExpL2RSearch.searchR(   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchR( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.searchR(from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int searchR( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchR(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int searchR( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchR(from,until,      i -> cmp.compare(key,arr[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchL( T[] arr,                      T key                            ) { return ExpL2RSearch.searchL(   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchL( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.searchL(from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int searchL( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchL(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int searchL( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchL(from,until,      i -> cmp.compare(key,arr[i]) ); }


    @Override public <T extends Comparable<? super T>> int searchGap ( T[] arr,                      T key                            ) { return ExpL2RSearch.searchGap (   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGap ( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.searchGap (from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int searchGap ( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGap (   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int searchGap ( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGap (from,until,      i -> cmp.compare(key,arr[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapR( T[] arr,                      T key                            ) { return ExpL2RSearch.searchGapR(   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapR( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.searchGapR(from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int searchGapR( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGapR(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int searchGapR( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGapR(from,until,      i -> cmp.compare(key,arr[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapL( T[] arr,                      T key                            ) { return ExpL2RSearch.searchGapL(   0,arr.length, i -> key.compareTo(arr[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapL( T[] arr, int from, int until, T key                            ) { return ExpL2RSearch.searchGapL(from,until,      i -> key.compareTo(arr[i]) ); }
    @Override public <T>                               int searchGapL( T[] arr,                      T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGapL(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
    @Override public <T>                               int searchGapL( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return ExpL2RSearch.searchGapL(from,until,      i -> cmp.compare(key,arr[i]) ); }


    @Override public int search ( byte[] arr,                      byte key                     ) { return ExpL2RSearch.search (   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int search ( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.search (from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int search ( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.search (   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int search ( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.search (from,until,      i ->  cmp.compare(key,arr[i]) ); }

    @Override public int searchR( byte[] arr,                      byte key                     ) { return ExpL2RSearch.searchR(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchR( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.searchR(from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchR( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchR(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int searchR( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchR(from,until,      i ->  cmp.compare(key,arr[i]) ); }

    @Override public int searchL( byte[] arr,                      byte key                     ) { return ExpL2RSearch.searchL(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchL( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.searchL(from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchL( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchL(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int searchL( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchL(from,until,      i ->  cmp.compare(key,arr[i]) ); }

    @Override public int searchGap ( byte[] arr,                      byte key                     ) { return ExpL2RSearch.searchGap (   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGap ( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.searchGap (from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGap ( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGap (   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int searchGap ( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGap (from,until,      i ->  cmp.compare(key,arr[i]) ); }

    @Override public int searchGapR( byte[] arr,                      byte key                     ) { return ExpL2RSearch.searchGapR(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGapR( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.searchGapR(from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGapR( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGapR(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int searchGapR( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGapR(from,until,      i ->  cmp.compare(key,arr[i]) ); }

    @Override public int searchGapL( byte[] arr,                      byte key                     ) { return ExpL2RSearch.searchGapL(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGapL( byte[] arr, int from, int until, byte key                     ) { return ExpL2RSearch.searchGapL(from,until,      i -> Byte.compare(key,arr[i]) ); }
    @Override public int searchGapL( byte[] arr,                      byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGapL(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
    @Override public int searchGapL( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return ExpL2RSearch.searchGapL(from,until,      i ->  cmp.compare(key,arr[i]) ); }
  };


  public static int search( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = from+step,
          c = compass.applyAsInt(k);
      if( c == 0 )    return k;
      if( c <  0 ) { until = k; break; }
                      from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ) {      int mid = from+until >>> 1,
               c = compass.applyAsInt(mid);
           if( c < 0 )   until = -1 + mid;
      else if( c > 0 )    from =  1 + mid;
      else                     return mid;
    }

    return ~from;
  }

  public static int searchR( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = from+step,
          c = compass.applyAsInt(k);
      if( c < 0 ) { until = k; break; }
      found |= 0==c; from = k+1;

    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c < 0 )   until = -1 + mid;
      else           from =  1 + mid;
      found |= 0==c;
    }

    return found ? from : ~from;
  }

  public static int searchL( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    boolean found = false;

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
    {
      int                      k = from+step,
        c = compass.applyAsInt(k);
      if( c <= 0 ) { until = k; found = 0==c; break; }
                      from = k+1;
    }

    --until;

    // BINARY SEARCH PHASE
    while( from <= until ) { int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c <= 0 )  until = -1 + mid;
      else           from =  1 + mid;
      found |= 0==c;
    }

    return found ? from : ~from;
  }


  public static int searchGap( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = from+step,
          c = compass.applyAsInt(k);
      if( c == 0 )    return k;
      if( c <  0 ) { until = k; break; }
                      from = k+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {       int mid = from+until >>> 1,
               c = compass.applyAsInt(mid);
           if( c < 0 )        until = mid;
      else if( c > 0 )         from = mid+1;
      else                     return mid;
    }

    return from;
  }

  public static int searchGapR( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = from+step,
          c = compass.applyAsInt(k);
      if( c < 0 ) { until = k; break; }
                     from = k+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {  int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c < 0 )        until = mid;
      else                from = mid+1;
    }

    return from;
  }

  public static int searchGapL( int from, int until, IntUnaryOperator compass )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    // GALLOPING PHASE
    for( int step=0; step < until-from; step = 1 + 2*step )  // <- make step have all bits set such that binary search is optimally efficient
    {
      int                        k = from+step,
          c = compass.applyAsInt(k);
      if( c <= 0 ) { until = k; break; }
                      from = k+1;
    }

    // BINARY SEARCH PHASE
    while( from < until ) {  int mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c <= 0 )       until = mid;
      else                from = mid+1;
    }

    return from;
  }


  public static <T extends Comparable<? super T>> int search ( T[] arr,                      T key                            ) { return search (   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int search ( T[] arr, int from, int until, T key                            ) { return search (from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int search ( T[] arr,                      T key, Comparator<? super T> cmp ) { return search (   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int search ( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return search (from,until,      i -> cmp.compare(key,arr[i]) ); }

  public static <T extends Comparable<? super T>> int searchR( T[] arr,                      T key                            ) { return searchR(   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int searchR( T[] arr, int from, int until, T key                            ) { return searchR(from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int searchR( T[] arr,                      T key, Comparator<? super T> cmp ) { return searchR(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int searchR( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return searchR(from,until,      i -> cmp.compare(key,arr[i]) ); }

  public static <T extends Comparable<? super T>> int searchL( T[] arr,                      T key                            ) { return searchL(   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int searchL( T[] arr, int from, int until, T key                            ) { return searchL(from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int searchL( T[] arr,                      T key, Comparator<? super T> cmp ) { return searchL(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int searchL( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return searchL(from,until,      i -> cmp.compare(key,arr[i]) ); }


  public static <T extends Comparable<? super T>> int searchGap ( T[] arr,                      T key                            ) { return searchGap (   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int searchGap ( T[] arr, int from, int until, T key                            ) { return searchGap (from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int searchGap ( T[] arr,                      T key, Comparator<? super T> cmp ) { return searchGap (   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int searchGap ( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return searchGap (from,until,      i -> cmp.compare(key,arr[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapR( T[] arr,                      T key                            ) { return searchGapR(   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapR( T[] arr, int from, int until, T key                            ) { return searchGapR(from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int searchGapR( T[] arr,                      T key, Comparator<? super T> cmp ) { return searchGapR(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int searchGapR( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapR(from,until,      i -> cmp.compare(key,arr[i]) ); }

  public static <T extends Comparable<? super T>> int searchGapL( T[] arr,                      T key                            ) { return searchGapL(   0,arr.length, i -> key.compareTo(arr[i]) ); }
  public static <T extends Comparable<? super T>> int searchGapL( T[] arr, int from, int until, T key                            ) { return searchGapL(from,until,      i -> key.compareTo(arr[i]) ); }
  public static <T>                               int searchGapL( T[] arr,                      T key, Comparator<? super T> cmp ) { return searchGapL(   0,arr.length, i -> cmp.compare(key,arr[i]) ); }
  public static <T>                               int searchGapL( T[] arr, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapL(from,until,      i -> cmp.compare(key,arr[i]) ); }


  public static int search ( byte[] arr,                      byte key                     ) { return search (   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int search ( byte[] arr, int from, int until, byte key                     ) { return search (from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int search ( byte[] arr,                      byte key, ComparatorByte cmp ) { return search (   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int search ( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return search (from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int searchR( byte[] arr,                      byte key                     ) { return searchR(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int searchR( byte[] arr, int from, int until, byte key                     ) { return searchR(from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int searchR( byte[] arr,                      byte key, ComparatorByte cmp ) { return searchR(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int searchR( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return searchR(from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int searchL( byte[] arr,                      byte key                     ) { return searchL(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int searchL( byte[] arr, int from, int until, byte key                     ) { return searchL(from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int searchL( byte[] arr,                      byte key, ComparatorByte cmp ) { return searchL(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int searchL( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return searchL(from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int searchGap ( byte[] arr,                      byte key                     ) { return searchGap (   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int searchGap ( byte[] arr, int from, int until, byte key                     ) { return searchGap (from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int searchGap ( byte[] arr,                      byte key, ComparatorByte cmp ) { return searchGap (   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int searchGap ( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return searchGap (from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int searchGapR( byte[] arr,                      byte key                     ) { return searchGapR(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int searchGapR( byte[] arr, int from, int until, byte key                     ) { return searchGapR(from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int searchGapR( byte[] arr,                      byte key, ComparatorByte cmp ) { return searchGapR(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int searchGapR( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return searchGapR(from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int searchGapL( byte[] arr,                      byte key                     ) { return searchGapL(   0,arr.length, i -> Byte.compare(key,arr[i]) ); }
  public static int searchGapL( byte[] arr, int from, int until, byte key                     ) { return searchGapL(from,until,      i -> Byte.compare(key,arr[i]) ); }
  public static int searchGapL( byte[] arr,                      byte key, ComparatorByte cmp ) { return searchGapL(   0,arr.length, i ->  cmp.compare(key,arr[i]) ); }
  public static int searchGapL( byte[] arr, int from, int until, byte key, ComparatorByte cmp ) { return searchGapL(from,until,      i ->  cmp.compare(key,arr[i]) ); }

  public static int search ( int[] arr,                      int key                    ) { return search (   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int search ( int[] arr, int from, int until, int key                    ) { return search (from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int search ( int[] arr,                      int key, ComparatorInt cmp ) { return search (   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int search ( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return search (from,until,      i ->     cmp.compare(key,arr[i]) ); }

  public static int searchR( int[] arr,                      int key                    ) { return searchR(   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int searchR( int[] arr, int from, int until, int key                    ) { return searchR(from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int searchR( int[] arr,                      int key, ComparatorInt cmp ) { return searchR(   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int searchR( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return searchR(from,until,      i ->     cmp.compare(key,arr[i]) ); }

  public static int searchL( int[] arr,                      int key                    ) { return searchL(   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int searchL( int[] arr, int from, int until, int key                    ) { return searchL(from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int searchL( int[] arr,                      int key, ComparatorInt cmp ) { return searchL(   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int searchL( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return searchL(from,until,      i ->     cmp.compare(key,arr[i]) ); }

  public static int searchGap ( int[] arr,                      int key                    ) { return searchGap (   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int searchGap ( int[] arr, int from, int until, int key                    ) { return searchGap (from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int searchGap ( int[] arr,                      int key, ComparatorInt cmp ) { return searchGap (   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int searchGap ( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return searchGap (from,until,      i ->     cmp.compare(key,arr[i]) ); }

  public static int searchGapR( int[] arr,                      int key                    ) { return searchGapR(   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int searchGapR( int[] arr, int from, int until, int key                    ) { return searchGapR(from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int searchGapR( int[] arr,                      int key, ComparatorInt cmp ) { return searchGapR(   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int searchGapR( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return searchGapR(from,until,      i ->     cmp.compare(key,arr[i]) ); }

  public static int searchGapL( int[] arr,                      int key                    ) { return searchGapL(   0,arr.length, i -> Integer.compare(key,arr[i]) ); }
  public static int searchGapL( int[] arr, int from, int until, int key                    ) { return searchGapL(from,until,      i -> Integer.compare(key,arr[i]) ); }
  public static int searchGapL( int[] arr,                      int key, ComparatorInt cmp ) { return searchGapL(   0,arr.length, i ->     cmp.compare(key,arr[i]) ); }
  public static int searchGapL( int[] arr, int from, int until, int key, ComparatorInt cmp ) { return searchGapL(from,until,      i ->     cmp.compare(key,arr[i]) ); }


  public static int search ( IntBuffer buf,                      int key                    ) { return search ( buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int search ( IntBuffer buf, int from, int until, int key                    ) { return search (           from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int search ( IntBuffer buf,                      int key, ComparatorInt cmp ) { return search ( buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int search ( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return search (           from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  public static int searchR( IntBuffer buf,                      int key                    ) { return searchR( buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchR( IntBuffer buf, int from, int until, int key                    ) { return searchR(           from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchR( IntBuffer buf,                      int key, ComparatorInt cmp ) { return searchR( buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int searchR( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return searchR(           from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  public static int searchL( IntBuffer buf,                      int key                    ) { return searchL( buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchL( IntBuffer buf, int from, int until, int key                    ) { return searchL(           from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchL( IntBuffer buf,                      int key, ComparatorInt cmp ) { return searchL( buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int searchL( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return searchL(           from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  public static int searchGap ( IntBuffer buf,                      int key                    ) { return searchGap (buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGap ( IntBuffer buf, int from, int until, int key                    ) { return searchGap (          from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGap ( IntBuffer buf,                      int key, ComparatorInt cmp ) { return searchGap (buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int searchGap ( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return searchGap (          from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  public static int searchGapR( IntBuffer buf,                      int key                    ) { return searchGapR(buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGapR( IntBuffer buf, int from, int until, int key                    ) { return searchGapR(          from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGapR( IntBuffer buf,                      int key, ComparatorInt cmp ) { return searchGapR(buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int searchGapR( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return searchGapR(          from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  public static int searchGapL( IntBuffer buf,                      int key                    ) { return searchGapL(buf.position(),buf.limit(), i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGapL( IntBuffer buf, int from, int until, int key                    ) { return searchGapL(          from,until,       i -> Integer.compare(key,buf.get(i)) ); }
  public static int searchGapL( IntBuffer buf,                      int key, ComparatorInt cmp ) { return searchGapL(buf.position(),buf.limit(), i ->     cmp.compare(key,buf.get(i)) ); }
  public static int searchGapL( IntBuffer buf, int from, int until, int key, ComparatorInt cmp ) { return searchGapL(          from,until,       i ->     cmp.compare(key,buf.get(i)) ); }

  private ExpL2RSearch() {
    throw new UnsupportedOperationException("Static class cannot be instantiated.");
  }
}
