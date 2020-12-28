package com.github.jaaa.sort;

import java.util.Comparator;

import static java.lang.Math.subtractExact;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static java.lang.Math.min;

public class TimSortV1
{
  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { sort( seq, 0,seq.length, naturalOrder()); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { sort( seq,  from, until, naturalOrder()); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { sort( seq, 0,seq.length, cmp ); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    if(    0 > from      ) throw new IllegalArgumentException();
    if(until < from      ) throw new IllegalArgumentException();
    if(until > seq.length) throw new IllegalArgumentException();
    requireNonNull(cmp);

//    int len = subtractExact(until,from);
//    if( len <= 1 )
//      return;
//
//    int stack = 0;
//
//    final int                            N = 32;
//    for( int mid=from; mid < until; mid+=N )
//    {
//      int end = min(mid+N,until);
//      InsertionSortV2.sort(seq, mid,end, cmp);
//
//      ++stack;
//
//      for( int h=0; 0 == (1 & stack>>>h); h++ )
//      {
//        int l = end-N,
//            r = mid;
//        while( l < mid && r < end )
//      }
//    }
  }
}
