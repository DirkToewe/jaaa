package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.util.RNG;

import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

public class QuickSort
{
// STATIC FIELDS
  public static SorterInplace QUICK_SORTER = (QuickSorter) () -> new RNG(1337)::nextInt; // <- FIXME: use a less shitty seed

  private static abstract class QuickSortAcc implements QuickSortAccess {
    private final QuickSorter quickSorter;
    public QuickSortAcc( QuickSorter _quickSorter ) { quickSorter =_quickSorter; }
    @Override public IntBinaryOperator newRNG() { return quickSorter.newRNG(); }
  }

  private interface QuickSorter extends SorterInplace
  {
    @Override default boolean isStable() { return false; }

    IntBinaryOperator newRNG();

    @Override default <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return acc.compare(seq,i, seq,j); }
        @Override public void   swap( int i, int j ) {        acc.   swap(seq,i, seq,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( int from, int until, CompareSwapAccess acc )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( byte[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Byte.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.quickSort(from,until);
    }

    @Override default void sort( byte[] seq, int from, int until, ComparatorByte cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.quickSort(from,until);
    }

    @Override default void sort( short[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Short.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( short[] seq, int from, int until, ComparatorShort cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( int[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Integer.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( int[] seq, int from, int until, ComparatorInt cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( long[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Long.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( long[] seq, int from, int until, ComparatorLong cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( char[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Character.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( char[] seq, int from, int until, ComparatorChar cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( float[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Float.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( float[] seq, int from, int until, ComparatorFloat cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( double[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Double.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( double[] seq, int from, int until, ComparatorDouble cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( IntBuffer buf, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return Integer.compare( buf.get(i), buf.get(j) ); }
        @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
      }.quickSort(from,until);
    }

    @Override default void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( buf.get(i), buf.get(j) ); }
        @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
      }.quickSort(from,until);
    }

    @Override default <T extends Comparable<? super T>> void sort( T[] seq, int from, int until )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return seq[i].compareTo(seq[j]); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override default <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
    {
      new QuickSortAcc(this) {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static SorterInplace newQuickSorter( Supplier<IntBinaryOperator> rngFactory ) { return (QuickSorter) rngFactory::get; }

  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc ) { QUICK_SORTER.sort(seq,from,until,acc); }
  public static     void sort(        int from, int until, CompareSwapAccess                acc ) { QUICK_SORTER.sort(    from,until,acc); }

  public static void sort(      byte[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(      byte[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(      byte[] seq,                      ComparatorByte   cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(      byte[] seq, int from, int until, ComparatorByte   cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(     short[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(     short[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(     short[] seq,                      ComparatorShort  cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(     short[] seq, int from, int until, ComparatorShort  cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(       int[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(       int[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(       int[] seq,                      ComparatorInt    cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(       int[] seq, int from, int until, ComparatorInt    cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(      long[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(      long[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(      long[] seq,                      ComparatorLong   cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(      long[] seq, int from, int until, ComparatorLong   cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(      char[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(      char[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(      char[] seq,                      ComparatorChar   cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(      char[] seq, int from, int until, ComparatorChar   cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(     float[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(     float[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(     float[] seq,                      ComparatorFloat  cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(     float[] seq, int from, int until, ComparatorFloat  cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(    double[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort(    double[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort(    double[] seq,                      ComparatorDouble cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort(    double[] seq, int from, int until, ComparatorDouble cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort(   IntBuffer buf                                            ) { QUICK_SORTER.sort(buf); }
  public static void sort(   IntBuffer buf, int from, int until                       ) { QUICK_SORTER.sort(buf,from,until); }
  public static void sort(   IntBuffer buf,                      ComparatorInt    cmp ) { QUICK_SORTER.sort(buf,           cmp); }
  public static void sort(   IntBuffer buf, int from, int until, ComparatorInt    cmp ) { QUICK_SORTER.sort(buf,from,until,cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { QUICK_SORTER.sort(seq); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { QUICK_SORTER.sort(seq,from,until); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { QUICK_SORTER.sort(seq,           cmp); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { QUICK_SORTER.sort(seq,from,until,cmp); }
}
