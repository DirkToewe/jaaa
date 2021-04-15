package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;

import java.nio.IntBuffer;
import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

public final class PermSort
{
  // STATIC FIELDS
  public static SorterInplace PERM_SORTER = new SorterInplace()
  {
    @Override public boolean isStable() { return false; }

    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc ) { PermSort.sort(seq,from,until,acc); }
    @Override public     void sort(        int from, int until, CompareSwapAccess        acc ) { PermSort.sort(    from,until,acc); }

    @Override public void sort(   byte[] seq                                            ) { PermSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(   byte[] seq, int from, int until                       ) { PermSort.sort(seq, from,until          ); }
    @Override public void sort(   byte[] seq,                      ComparatorByte   cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort(  short[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Short::compare); }
    @Override public void sort(  short[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Short::compare); }
    @Override public void sort(  short[] seq,                      ComparatorShort  cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort(    int[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Integer::compare); }
    @Override public void sort(    int[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Integer::compare); }
    @Override public void sort(    int[] seq,                      ComparatorInt    cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort(   long[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Long::compare); }
    @Override public void sort(   long[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Long::compare); }
    @Override public void sort(   long[] seq,                      ComparatorLong   cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort(   char[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Character::compare); }
    @Override public void sort(   char[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Character::compare); }
    @Override public void sort(   char[] seq,                      ComparatorChar   cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort(  float[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Float::compare); }
    @Override public void sort(  float[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Float::compare); }
    @Override public void sort(  float[] seq,                      ComparatorFloat  cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort( double[] seq                                            ) { PermSort.sort(seq,    0,seq.length, Double::compare); }
    @Override public void sort( double[] seq, int from, int until                       ) { PermSort.sort(seq, from,until,      Double::compare); }
    @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { PermSort.sort(seq, from,until,      cmp); }

    @Override public void sort( IntBuffer buf                                         ) { PermSort.sort(buf, buf.position(),buf.limit(), Double::compare); }
    @Override public void sort( IntBuffer buf, int from, int until                    ) { PermSort.sort(buf,           from,until,       Double::compare); }
    @Override public void sort( IntBuffer buf,                      ComparatorInt cmp ) { PermSort.sort(buf, buf.position(),buf.limit(), cmp); }
    @Override public void sort( IntBuffer buf, int from, int until, ComparatorInt cmp ) { PermSort.sort(buf,           from,until,       cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { PermSort.sort(seq,    0,seq.length, naturalOrder()); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { PermSort.sort(seq, from,until,      naturalOrder()); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { PermSort.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { PermSort.sort(seq, from,until,      cmp); }
  };

// STATIC CONSTRUCTOR

  // STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(seq,i, seq,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(seq,i, seq,j); }
    }.permSort(from,until);
  }

  public static void sort( int from, int until, CompareSwapAccess acc )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(i,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    }.permSort(from,until);
  }

  public static void sort( byte[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Byte.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( byte[] seq, int from, int until, ComparatorByte cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( short[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Short.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( short[] seq, int from, int until, ComparatorShort cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( int[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Integer.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( int[] seq, int from, int until, ComparatorInt cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( long[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Long.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( long[] seq, int from, int until, ComparatorLong cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( char[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Character.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( char[] seq, int from, int until, ComparatorChar cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( float[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Float.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( float[] seq, int from, int until, ComparatorFloat cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( double[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Double.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return Double.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.permSort(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.permSort(from,until);
  }

  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return seq[i].compareTo(seq[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    new PermSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.permSort(from,until);
  }

  public static void sort(   byte[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(   byte[] seq, ComparatorByte   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(  short[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(  short[] seq, ComparatorShort  cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(    int[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(    int[] seq, ComparatorInt    cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(   long[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(   long[] seq, ComparatorLong   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(   char[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(   char[] seq, ComparatorChar   cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort(  float[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort(  float[] seq, ComparatorFloat  cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort( double[] seq                       ) { sort(seq, 0,seq.length     ); }
  public static void sort( double[] seq, ComparatorDouble cmp ) { sort(seq, 0,seq.length, cmp); }

  public static void sort( IntBuffer buf                    ) { sort(buf, buf.position(),buf.limit()     ); }
  public static void sort( IntBuffer buf, ComparatorInt cmp ) { sort(buf, buf.position(),buf.limit(), cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                            )  { sort(seq, 0,seq.length     ); }
  public static <T>                               void sort( T[] seq, Comparator<? super T> cmp )  { sort(seq, 0,seq.length, cmp); }

  // FIELDS
// CONSTRUCTORS
  private PermSort() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }
// METHODS
}
