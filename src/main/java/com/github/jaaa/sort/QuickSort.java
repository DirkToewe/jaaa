package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.Swap;
import com.github.jaaa.util.RNG;

import java.util.Comparator;

public class QuickSort
{
  private static class QuickSorter implements SorterInplace
  {
    private final long seed;

    public QuickSorter( long _seed ) { seed =_seed; }

    @Override public boolean isStable () { return false; }
    @Override public boolean isInplace() { return true;  }

    @Override public void sort( int from, int until, CompareSwapAccess acc )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return acc.compare(i,j); }
        @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort(byte[] seq, int from, int until, ComparatorByte cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) {
          Swap.swap(seq,i,j);
        }
      }.quickSort(from,until);
    }

    @Override public void sort( short[] seq, int from, int until, ComparatorShort cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort( int[] seq, int from, int until, ComparatorInt cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort( long[] seq, int from, int until, ComparatorLong cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort( char[] seq, int from, int until, ComparatorChar cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort( float[] seq, int from, int until, ComparatorFloat cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }

    @Override public <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
    {
      new QuickSortAccess() {
        private final RNG rng = new RNG(seed);
        @Override public int randInt( int from, int until ) { return rng.nextInt(from,until); }
        @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
        @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      }.quickSort(from,until);
    }
  };
  public static SorterInplace QUICK_SORTER = new QuickSorter(1337);

  public static void sort(               int from, int until,       CompareSwapAccess acc ) { QUICK_SORTER.sort(from,until,acc); }

  public static void sort( byte[] seq                                              ) { QUICK_SORTER.sort(seq); }
  public static void sort( byte[] seq, int from, int until                         ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( byte[] seq,                      ComparatorByte     cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( byte[] seq, int from, int until, ComparatorByte     cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( short[] seq                                             ) { QUICK_SORTER.sort(seq); }
  public static void sort( short[] seq, int from, int until                        ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( short[] seq,                      ComparatorShort   cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( short[] seq, int from, int until, ComparatorShort   cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( int[] seq                                               ) { QUICK_SORTER.sort(seq); }
  public static void sort( int[] seq, int from, int until                          ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( int[] seq,                      ComparatorInt       cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( int[] seq, int from, int until, ComparatorInt       cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( long[] seq                                              ) { QUICK_SORTER.sort(seq); }
  public static void sort( long[] seq, int from, int until                         ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( long[] seq,                      ComparatorLong     cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( long[] seq, int from, int until, ComparatorLong     cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( char[] seq                                              ) { QUICK_SORTER.sort(seq); }
  public static void sort( char[] seq, int from, int until                         ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( char[] seq,                      ComparatorChar     cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( char[] seq, int from, int until, ComparatorChar     cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( float[] seq                                             ) { QUICK_SORTER.sort(seq); }
  public static void sort( float[] seq, int from, int until                        ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( float[] seq,                      ComparatorFloat   cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( float[] seq, int from, int until, ComparatorFloat   cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static void sort( double[] seq                                            ) { QUICK_SORTER.sort(seq); }
  public static void sort( double[] seq, int from, int until                       ) { QUICK_SORTER.sort(seq,from,until); }
  public static void sort( double[] seq,                      ComparatorDouble cmp ) { QUICK_SORTER.sort(seq,           cmp); }
  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { QUICK_SORTER.sort(seq,from,until,cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { QUICK_SORTER.sort(seq); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { QUICK_SORTER.sort(seq,from,until); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { QUICK_SORTER.sort(seq,           cmp); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { QUICK_SORTER.sort(seq,from,until,cmp); }
}
