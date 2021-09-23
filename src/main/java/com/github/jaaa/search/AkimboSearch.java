package com.github.jaaa.search;

import com.github.jaaa.ComparatorByte;
import com.github.jaaa.ComparatorInt;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;


public class AkimboSearch
{
  public static final Searcher AKIMBO_SEARCHER = new Searcher()
  {
    @Override public int search ( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.search (from,until, compass); }
    @Override public int searchL( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.searchL(from,until, compass); }
    @Override public int searchR( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.searchR(from,until, compass); }


    @Override public int searchGap ( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.searchGap (from,until, compass); }
    @Override public int searchGapL( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.searchGapL(from,until, compass); }
    @Override public int searchGapR( int from, int until, IntUnaryOperator compass ) { return AkimboSearch.searchGapR(from,until, compass); }


    @Override public <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return AkimboSearch.search (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return AkimboSearch.search (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.search (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.search (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return AkimboSearch.searchR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return AkimboSearch.searchR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.searchR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.searchR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return AkimboSearch.searchL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return AkimboSearch.searchL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.searchL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.searchL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return AkimboSearch.searchGap (   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return AkimboSearch.searchGap (from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGap (   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGap (from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return AkimboSearch.searchGapR(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return AkimboSearch.searchGapR(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGapR(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGapR(from,until,      i -> cmp.compare(key,seq[i]) ); }

    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return AkimboSearch.searchGapL(   0,seq.length, i -> key.compareTo(seq[i]) ); }
    @Override public <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return AkimboSearch.searchGapL(from,until,      i -> key.compareTo(seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGapL(   0,seq.length, i -> cmp.compare(key,seq[i]) ); }
    @Override public <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return AkimboSearch.searchGapL(from,until,      i -> cmp.compare(key,seq[i]) ); }


    @Override public int search ( byte[] seq,                      byte key                     ) { return AkimboSearch.search (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.search (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.search (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int search ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.search (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchR( byte[] seq,                      byte key                     ) { return AkimboSearch.searchR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.searchR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.searchR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.searchR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchL( byte[] seq,                      byte key                     ) { return AkimboSearch.searchL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.searchL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.searchL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.searchL(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGap ( byte[] seq,                      byte key                     ) { return AkimboSearch.searchGap (   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.searchGap (from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.searchGap (   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGap ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.searchGap (from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapR( byte[] seq,                      byte key                     ) { return AkimboSearch.searchGapR(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.searchGapR(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.searchGapR(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.searchGapR(from,until,      i ->  cmp.compare(key,seq[i]) ); }

    @Override public int searchGapL( byte[] seq,                      byte key                     ) { return AkimboSearch.searchGapL(   0,seq.length, i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key                     ) { return AkimboSearch.searchGapL(from,until,      i -> Byte.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq,                      byte key, ComparatorByte cmp ) { return AkimboSearch.searchGapL(   0,seq.length, i ->  cmp.compare(key,seq[i]) ); }
    @Override public int searchGapL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return AkimboSearch.searchGapL(from,until,      i ->  cmp.compare(key,seq[i]) ); }


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
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                        mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c == 0 )        return mid;

      // GALLOPING PHASE
      // ---------------
      if( c < 0 )
      {
        // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compass.applyAsInt(mid = from+step);
          if( c == 0 )        return mid;
          if( c <  0 )     { until = mid; break; }
                              from = mid+1;
        }
      }
      else
      {
        // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compass.applyAsInt(mid = until-step);
          if( c == 0 )        return mid;
          if( c >  0 )      { from = mid+1; break; }
                             until = mid;
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compass.applyAsInt(mid = from + until >>> 1);
        if( c == 0 )        return mid;
        if( c >  0 )        from = mid+1;
        else               until = mid;
      }
    }
    return ~from;
  }

  public static int searchL( int from, int until,                    IntUnaryOperator compass ) { return search(from,until, false, compass); }
  public static int searchR( int from, int until,                    IntUnaryOperator compass ) { return search(from,until, true,  compass); }
  public static int search ( int from, int until, boolean rightBias, IntUnaryOperator compass )
  {
    if(from < 0    ) throw new IllegalArgumentException();
    if(from ==until) return ~from;
    if(from > until) throw new IllegalArgumentException();

    int                      mid = from+until >>> 1,
      c = compass.applyAsInt(mid);

    boolean found = 0==c;

    // GALLOPING PHASE
    // ---------------
    int     bias = rightBias ? -1 : 0;
    if( c > bias )
    { // gallop right to left
                                    from = mid+1;
      for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compass.applyAsInt(mid = until-step); found |= 0==c;
        if( c > bias )    { from = mid+1; break; }
                           until = mid;
      }
    }
    else
    { // gallop left to right
                              until = mid;
      for( int step=0; step < until-from; step = 1 | step<<1 ) // <- make step have all bits set such that binary search is optimally efficient
      {
            c = compass.applyAsInt(mid = from+step); found |= 0==c;
        if( c > bias )      from = mid+1;
        else             { until = mid; break; }
      }
    }

    // BINARY SEARCH PHASE
    // -------------------
    while( from < until ) {
          c = compass.applyAsInt(mid = from+until >>> 1); found |= 0==c;
      if( c > bias ) from = mid+1;
      else          until = mid;
    }

    return found ? from : ~from;
  }

  public static int searchGap ( int from, int until, IntUnaryOperator compass )
  {
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                        mid = from+until >>> 1,
          c = compass.applyAsInt(mid);
      if( c == 0 )        return mid;

      // GALLOPING PHASE
      // ---------------
      if( c <  0 )
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compass.applyAsInt(mid = from+step);
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
              c = compass.applyAsInt(mid = until-step);
          if( c == 0 )        return mid;
          if( c >  0 )      { from = mid+1; break; }
                             until = mid;
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compass.applyAsInt(mid = from+until >>> 1);
        if( c == 0 )        return mid;
        if( c >  0 )        from = mid+1;
        else               until = mid;
      }
    }
    return from;
  }

  public static int searchGapL( int from, int until,                    IntUnaryOperator compass ) { return searchGap(from,until, false, compass); }
  public static int searchGapR( int from, int until,                    IntUnaryOperator compass ) { return searchGap(from,until, true,  compass); }
  public static int searchGap ( int from, int until, boolean rightBias, IntUnaryOperator compass )
  {
    if(from < 0    ) throw new IllegalArgumentException();
    if(from > until) throw new IllegalArgumentException();
    if(from < until)
    {
      int                      mid = from+until >>> 1,
        c = compass.applyAsInt(mid);

      // GALLOPING PHASE
      // ---------------
      int     bias = rightBias ? -1 : 0;
      if( c > bias )
      { // gallop right to left
                                      from = mid+1;
        for( int step=1; step < until-from; step<<=1 ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compass.applyAsInt(mid = until - step);
          if( c > bias )    { from = mid+1; break; }
                             until = mid;
        }
      }
      else
      { // gallop left to right
                                until = mid;
        for( int step=0; step < until-from; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
        {
              c = compass.applyAsInt(mid = from + step);
          if( c > bias )      from = mid+1;
          else             { until = mid; break; }
        }
      }

      // BINARY SEARCH PHASE
      // -------------------
      while( from < until ) {
            c = compass.applyAsInt(mid = from+until >>> 1);
        if( c > bias )      from = mid+1;
        else               until = mid;
      }
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
