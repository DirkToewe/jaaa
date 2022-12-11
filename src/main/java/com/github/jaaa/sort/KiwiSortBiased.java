package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.compare.*;
import com.github.jaaa.permute.Swap;

import java.nio.IntBuffer;
import java.util.Comparator;

import static java.lang.System.arraycopy;


public final class KiwiSortBiased
{
  // STATIC FIELDS
  public static SorterInPlace KIWI_BIASED_SORTER = new SorterInPlace()
  {
    @Override public boolean isStable() { return true; }

    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc ) { KiwiSortBiased.sort(seq,from,until,acc); }
    @Override public     void sort(        int from, int until, CompareSwapAccess        acc ) { KiwiSortBiased.sort(    from,until,acc); }

    @Override public void sort(   byte[] seq                                          ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(   byte[] seq, int from, int until                     ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(   byte[] seq,                      ComparatorByte cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   byte[] seq, int from, int until, ComparatorByte cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort(  short[] seq                                           ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(  short[] seq, int from, int until                      ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(  short[] seq,                      ComparatorShort cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  short[] seq, int from, int until, ComparatorShort cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort(    int[] seq                                         ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(    int[] seq, int from, int until                    ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(    int[] seq,                      ComparatorInt cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    int[] seq, int from, int until, ComparatorInt cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort(   long[] seq                                          ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(   long[] seq, int from, int until                     ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(   long[] seq,                      ComparatorLong cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   long[] seq, int from, int until, ComparatorLong cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort(   char[] seq                                          ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(   char[] seq, int from, int until                     ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(   char[] seq,                      ComparatorChar cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   char[] seq, int from, int until, ComparatorChar cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort(  float[] seq                                           ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort(  float[] seq, int from, int until                      ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort(  float[] seq,                      ComparatorFloat cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  float[] seq, int from, int until, ComparatorFloat cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort( double[] seq                                            ) { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public void sort( double[] seq, int from, int until                       ) { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { KiwiSortBiased.sort(seq, from,until,      cmp); }

    @Override public void sort( IntBuffer buf                                         ) { KiwiSortBiased.sort(buf, buf.position(),buf.limit()     ); }
    @Override public void sort( IntBuffer buf, int from, int until                    ) { KiwiSortBiased.sort(buf,           from,until           ); }
    @Override public void sort( IntBuffer buf,                      ComparatorInt cmp ) { KiwiSortBiased.sort(buf, buf.position(),buf.limit(), cmp); }
    @Override public void sort( IntBuffer buf, int from, int until, ComparatorInt cmp ) { KiwiSortBiased.sort(buf,           from,until,       cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { KiwiSortBiased.sort(seq,    0,seq.length     ); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { KiwiSortBiased.sort(seq, from,until          ); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { KiwiSortBiased.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { KiwiSortBiased.sort(seq, from,until,      cmp); }
  };

// STATIC CONSTRUCTOR

  // STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(seq,i, seq,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(seq,i, seq,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( int from, int until, CompareSwapAccess acc )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(i,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( byte[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Byte.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( byte[] seq, int from, int until, ComparatorByte cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( short[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Short.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( short[] seq, int from, int until, ComparatorShort cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( int[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Integer.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( int[] seq, int from, int until, ComparatorInt cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( long[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Long.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( long[] seq, int from, int until, ComparatorLong cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( char[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Character.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( char[] seq, int from, int until, ComparatorChar cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( float[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Float.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( float[] seq, int from, int until, ComparatorFloat cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( double[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Double.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return Double.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return seq[i].compareTo(seq[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.kiwiSortBiased(from,until);
  }

  public static <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    new KiwiSortBiasedAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
      @Override public void kiwiSortBiased_sortRun( int from, int until ) {
        for( int i=from; ++i < until; )
        {
          var piv = seq[i];
               int lo = from;
          for( int hi = i-1,
             mid = hi;;
             mid = lo+hi >>> 1
          ){
            int c = cmp.compare(piv, seq[mid]);
            if( c < 0 )             hi = mid;
            else                    lo = mid+1;
            if( lo >= hi ) break;
          }

          arraycopy(seq,lo, seq,lo+1, i-lo);
          seq[lo] = piv;
        }
      }
    }.kiwiSortBiased(from,until);
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
  private KiwiSortBiased() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }
// METHODS
}
