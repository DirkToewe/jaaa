package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.Swap;

import java.util.Comparator;

import static java.lang.System.arraycopy;
import static java.util.Comparator.naturalOrder;

public final class InsertionSortV2
{
  // STATIC FIELDS
  public static SorterInplace INSERTION_V2_SORTER = new SorterInplace()
  {
    @Override public boolean isStable () { return true; }
    @Override public boolean isInplace() { return true; }

    @Override public void sort(               int from, int until,       CompareSwapAccess acc ) { InsertionSortV2.sort(from,until,acc); }

    @Override public void sort(   byte[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Byte::compare); }
    @Override public void sort(   byte[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Byte::compare); }
    @Override public void sort(   byte[] seq,                      ComparatorByte   cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(  short[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Short::compare); }
    @Override public void sort(  short[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Short::compare); }
    @Override public void sort(  short[] seq,                      ComparatorShort  cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(    int[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Integer::compare); }
    @Override public void sort(    int[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Integer::compare); }
    @Override public void sort(    int[] seq,                      ComparatorInt    cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(   long[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Long::compare); }
    @Override public void sort(   long[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Long::compare); }
    @Override public void sort(   long[] seq,                      ComparatorLong   cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(   char[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Character::compare); }
    @Override public void sort(   char[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Character::compare); }
    @Override public void sort(   char[] seq,                      ComparatorChar   cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort(  float[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Float::compare); }
    @Override public void sort(  float[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Float::compare); }
    @Override public void sort(  float[] seq,                      ComparatorFloat  cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public void sort( double[] seq                                            ) { InsertionSortV2.sort(seq,    0,seq.length, Double::compare); }
    @Override public void sort( double[] seq, int from, int until                       ) { InsertionSortV2.sort(seq, from,until,      Double::compare); }
    @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { InsertionSortV2.sort(seq, from,until,      cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { InsertionSortV2.sort(seq,    0,seq.length, naturalOrder()); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { InsertionSortV2.sort(seq, from,until,      naturalOrder()); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { InsertionSortV2.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { InsertionSortV2.sort(seq, from,until,      cmp); }
  };

// STATIC CONSTRUCTOR

  // STATIC METHODS
  public static void sort( int from, int until, CompareSwapAccess acc )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return acc.compare(i,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( byte[] seq, int from, int until, ComparatorByte cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( short[] seq, int from, int until, ComparatorShort cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( int[] seq, int from, int until, ComparatorInt cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( long[] seq, int from, int until, ComparatorLong cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( char[] seq, int from, int until, ComparatorChar cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( float[] seq, int from, int until, ComparatorFloat cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp )
  {
    new InsertionSortV2Access() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionSortV2(from,until);
  }

  public static <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    if(    0 > from      ) throw new IndexOutOfBoundsException();
    if(until < from      ) throw new IndexOutOfBoundsException();
    if(until > seq.length) throw new IndexOutOfBoundsException();

    for( int i=from; ++i < until; )
    {
      var piv = seq[i];
      int lo = from;
      for( int hi = i-1,
         mid = hi;;
         mid = lo+hi >>> 1 )
      {
        int c = cmp.compare(piv, seq[mid]);
        if( c < 0 )        hi = -1 + mid;
        else               lo = +1 + mid;

        if( lo > hi ) break;
      }

      arraycopy(seq,lo, seq,lo+1, i-lo);
      seq[lo] = piv;
    }

//    new InsertionSortV2Access() {
//      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
//      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
//    }.insertionSortV2(from,until);
  }

  public static void sort(   byte[] seq                       ) { sort(seq,    0,seq.length, Byte::compare); }
  public static void sort(   byte[] seq, int from, int until  ) { sort(seq, from,until,      Byte::compare); }
  public static void sort(   byte[] seq, ComparatorByte   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(  short[] seq                       ) { sort(seq,    0,seq.length, Short::compare); }
  public static void sort(  short[] seq, int from, int until  ) { sort(seq, from,until,      Short::compare); }
  public static void sort(  short[] seq, ComparatorShort  cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(    int[] seq                       ) { sort(seq,    0,seq.length, Integer::compare); }
  public static void sort(    int[] seq, int from, int until  ) { sort(seq, from,until,      Integer::compare); }
  public static void sort(    int[] seq, ComparatorInt    cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(   long[] seq                       ) { sort(seq,    0,seq.length, Long::compare); }
  public static void sort(   long[] seq, int from, int until  ) { sort(seq, from,until,      Long::compare); }
  public static void sort(   long[] seq, ComparatorLong   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(   char[] seq                       ) { sort(seq,    0,seq.length, Character::compare); }
  public static void sort(   char[] seq, int from, int until  ) { sort(seq, from,until,      Character::compare); }
  public static void sort(   char[] seq, ComparatorChar   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(  float[] seq                       ) { sort(seq,    0,seq.length, Float::compare); }
  public static void sort(  float[] seq, int from, int until  ) { sort(seq, from,until,      Float::compare); }
  public static void sort(  float[] seq, ComparatorFloat  cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort( double[] seq                       ) { sort(seq,    0,seq.length, Double::compare); }
  public static void sort( double[] seq, int from, int until  ) { sort(seq, from,until,      Double::compare); }
  public static void sort( double[] seq, ComparatorDouble cmp ) { sort(seq,    0,seq.length, cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                            )  { sort(seq,    0,seq.length, naturalOrder()); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until       )  { sort(seq, from,until,      naturalOrder()); }
  public static <T>                               void sort( T[] seq, Comparator<? super T> cmp )  { sort(seq,    0,seq.length, cmp); }

  // FIELDS
// CONSTRUCTORS
  private InsertionSortV2() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }
// METHODS
}
