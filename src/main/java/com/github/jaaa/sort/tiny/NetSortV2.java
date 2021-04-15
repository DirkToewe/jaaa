package com.github.jaaa.sort.tiny;

import com.github.jaaa.*;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.sort.SorterInplace;

import java.nio.IntBuffer;
import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

public final class NetSortV2
{
  // STATIC FIELDS
  public static SorterInplace NET_V2_SORTER = new SorterInplace()
  {
    @Override public boolean isStable() { return true; }

    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc ) { NetSortV2.sort(seq,from,until,acc); }
    @Override public     void sort(        int from, int until, CompareSwapAccess        acc ) { NetSortV2.sort(    from,until,acc); }

    @Override public void sort(   byte[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(   byte[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(   byte[] seq,                      ComparatorByte   cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(  short[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(  short[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(  short[] seq,                      ComparatorShort  cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(    int[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(    int[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(    int[] seq,                      ComparatorInt    cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(   long[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(   long[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(   long[] seq,                      ComparatorLong   cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(   char[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(   char[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(   char[] seq,                      ComparatorChar   cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(  float[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort(  float[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort(  float[] seq,                      ComparatorFloat  cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort( double[] seq                                            ) { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public void sort( double[] seq, int from, int until                       ) { NetSortV2.sort(seq, from,until          ); }
    @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { NetSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(   IntBuffer buf                                            ) { NetSortV2.sort(buf, buf.position(),buf.limit()     ); }
    @Override public void sort(   IntBuffer buf, int from, int until                       ) { NetSortV2.sort(buf,           from,until           ); }
    @Override public void sort(   IntBuffer buf,                      ComparatorInt    cmp ) { NetSortV2.sort(buf, buf.position(),buf.limit(), cmp); }
    @Override public void sort(   IntBuffer buf, int from, int until, ComparatorInt    cmp ) { NetSortV2.sort(buf,           from,until,       cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { NetSortV2.sort(seq,    0,seq.length     ); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { NetSortV2.sort(seq, from,until          ); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { NetSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { NetSortV2.sort(seq, from,until,      cmp); }
  };

// STATIC CONSTRUCTOR

  // STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return acc.compare(seq,i, seq,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(seq,i, seq,j); }
    }.netSortV2(from,until);
  }

  public static void sort( int from, int until, CompareSwapAccess acc )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return acc.compare(i,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( byte[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Byte.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( byte[] seq, int from, int until, ComparatorByte cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( short[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Short.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( short[] seq, int from, int until, ComparatorShort cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( int[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Integer.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( int[] seq, int from, int until, ComparatorInt cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( long[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Long.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( long[] seq, int from, int until, ComparatorLong cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( char[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Character.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( char[] seq, int from, int until, ComparatorChar cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( float[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Float.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( float[] seq, int from, int until, ComparatorFloat cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( double[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Double.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return Integer.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.netSortV2(from,until);
  }

  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return seq[i].compareTo(seq[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    new NetSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.netSortV2(from,until);
  }

  public static void sort(      byte[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(      byte[] seq, ComparatorByte   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(     short[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(     short[] seq, ComparatorShort  cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(       int[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(       int[] seq, ComparatorInt    cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(      long[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(      long[] seq, ComparatorLong   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(      char[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(      char[] seq, ComparatorChar   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(     float[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(     float[] seq, ComparatorFloat  cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(    double[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(    double[] seq, ComparatorDouble cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(   IntBuffer buf                       ) { sort(buf, buf.position(),buf.limit()     ); }
  public static void sort(   IntBuffer buf, ComparatorInt    cmp ) { sort(buf, buf.position(),buf.limit(), cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                            )  { sort(seq, 0,seq.length     ); }
  public static <T>                               void sort( T[] seq, Comparator<? super T> cmp )  { sort(seq, 0,seq.length, cmp); }

  // FIELDS
// CONSTRUCTORS
  private NetSortV2() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }
// METHODS
}
