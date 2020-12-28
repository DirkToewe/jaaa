package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.Swap;

import java.util.Comparator;

// REFERENCES
// ----------
// .. [1] "STABLE IN SITU SORTING AND MINIMUM DATA MOVEMENT"
//         J. IAN MUNRO, VENKATESH RAMAN and JEFFREY S. SALOWE

public class PermSortStable
{
  public static SorterInplace PERM_STABLE_SORTER = new SorterInplace()
  {
    @Override public boolean isStable () { return true;  }
    @Override public boolean isInplace() { return false; }

    @Override public void sort( int from, int until, CompareSwapAccess acc )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
      }.permSortStable(from,until);
    }

    @Override public void sort(byte[] seq, int from, int until, ComparatorByte cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( short[] seq, int from, int until, ComparatorShort cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( int[] seq, int from, int until, ComparatorInt cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( long[] seq, int from, int until, ComparatorLong cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( char[] seq, int from, int until, ComparatorChar cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( float[] seq, int from, int until, ComparatorFloat cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }

    @Override public <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
    {
      new PermSortStableAccess() {
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.permSortStable(from,until);
    }
  };

  public static void sort(               int from, int until,       CompareSwapAccess acc ) { PERM_STABLE_SORTER.sort(from,until,acc); }

  public static void sort(   byte[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(   byte[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(   byte[] seq,                      ComparatorByte   cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort(  short[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(  short[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(  short[] seq,                      ComparatorShort  cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort(    int[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(    int[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(    int[] seq,                      ComparatorInt    cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort(   long[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(   long[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(   long[] seq,                      ComparatorLong   cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort(   char[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(   char[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(   char[] seq,                      ComparatorChar   cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort(  float[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort(  float[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort(  float[] seq,                      ComparatorFloat  cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static void sort( double[] seq                                            ) { PERM_STABLE_SORTER.sort(seq); }
  public static void sort( double[] seq, int from, int until                       ) { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static void sort( double[] seq,                      ComparatorDouble cmp ) { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { PERM_STABLE_SORTER.sort(seq); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { PERM_STABLE_SORTER.sort(seq,from,until); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { PERM_STABLE_SORTER.sort(seq,           cmp); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { PERM_STABLE_SORTER.sort(seq,from,until,cmp); }
}
