package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.Swap;

import java.util.Comparator;

public final class SelectionSort
{
  public static SorterInplace SELECTION_SORTER = new SorterInplace()
  {
    @Override public boolean isStable () { return false; }
    @Override public boolean isInplace() { return true;  }

    @Override public void sort( int from, int until, CompareSwapAccess acc )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
      }.selectionSort(from,until);
    }

    @Override public void sort( byte[] seq, int from, int until, ComparatorByte cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( short[] seq, int from, int until, ComparatorShort cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( int[] seq, int from, int until, ComparatorInt cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( long[] seq, int from, int until, ComparatorLong cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( char[] seq, int from, int until, ComparatorChar cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( float[] seq, int from, int until, ComparatorFloat cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }

    @Override public <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
    {
      new SelectionSortAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.selectionSort(from,until);
    }
  };

  public static void sort(               int from, int until,       CompareSwapAccess acc ) { SELECTION_SORTER.sort(from,until,acc); }

  public static void sort(   byte[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(   byte[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(   byte[] seq,                      ComparatorByte   cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort(  short[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(  short[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(  short[] seq,                      ComparatorShort  cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort(    int[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(    int[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(    int[] seq,                      ComparatorInt    cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort(   long[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(   long[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(   long[] seq,                      ComparatorLong   cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort(   char[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(   char[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(   char[] seq,                      ComparatorChar   cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort(  float[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort(  float[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort(  float[] seq,                      ComparatorFloat  cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static void sort( double[] seq                                            ) { SELECTION_SORTER.sort(seq); }
  public static void sort( double[] seq, int from, int until                       ) { SELECTION_SORTER.sort(seq,from,until); }
  public static void sort( double[] seq,                      ComparatorDouble cmp ) { SELECTION_SORTER.sort(seq,           cmp); }
  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { SELECTION_SORTER.sort(seq,from,until,cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { SELECTION_SORTER.sort(seq); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { SELECTION_SORTER.sort(seq,from,until); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { SELECTION_SORTER.sort(seq,           cmp); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { SELECTION_SORTER.sort(seq,from,until,cmp); }
}
