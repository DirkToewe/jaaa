package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.nio.IntBuffer;
import java.util.Comparator;

import static java.lang.System.arraycopy;

public final class InsertionExpSort
{
// STATIC FIELDS
  public static SorterInplace INSERTION_EXP_SORTER = new SorterInplace()
  {
    @Override public boolean isStable() { return true; }

    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc ) { InsertionExpSort.sort(seq,from,until,acc); }
    @Override public     void sort(        int from, int until, CompareSwapAccess        acc ) { InsertionExpSort.sort(    from,until,acc); }

    @Override public void sort(   byte[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(   byte[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(   byte[] seq,                      ComparatorByte   cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort(  short[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(  short[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(  short[] seq,                      ComparatorShort  cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort(    int[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(    int[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(    int[] seq,                      ComparatorInt    cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort(   long[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(   long[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(   long[] seq,                      ComparatorLong   cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort(   char[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(   char[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(   char[] seq,                      ComparatorChar   cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort(  float[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(  float[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort(  float[] seq,                      ComparatorFloat  cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort( double[] seq                                            ) { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public void sort( double[] seq, int from, int until                       ) { InsertionExpSort.sort(seq, from,until          ); }
    @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { InsertionExpSort.sort(seq, from,until,      cmp); }

    @Override public void sort( IntBuffer buf                                         ) { InsertionExpSort.sort(buf, buf.position(),buf.limit()     ); }
    @Override public void sort( IntBuffer buf, int from, int until                    ) { InsertionExpSort.sort(buf,           from,until           ); }
    @Override public void sort( IntBuffer buf,                      ComparatorInt cmp ) { InsertionExpSort.sort(buf, buf.position(),buf.limit(), cmp); }
    @Override public void sort( IntBuffer buf, int from, int until, ComparatorInt cmp ) { InsertionExpSort.sort(buf,           from,until,       cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { InsertionExpSort.sort(seq,    0,seq.length     ); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { InsertionExpSort.sort(seq, from,until          ); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { InsertionExpSort.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { InsertionExpSort.sort(seq, from,until,      cmp); }
  };

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(seq,i, seq,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(seq,i, seq,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( int from, int until, CompareSwapAccess acc )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return acc.compare(i,j); }
      @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( byte[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Byte.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( byte[] seq, int from, int until, ComparatorByte cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( short[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Short.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( short[] seq, int from, int until, ComparatorShort cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( int[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Integer.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( int[] seq, int from, int until, ComparatorInt cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( long[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Long.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( long[] seq, int from, int until, ComparatorLong cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( char[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Character.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( char[] seq, int from, int until, ComparatorChar cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( float[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Float.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( float[] seq, int from, int until, ComparatorFloat cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( double[] seq, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Double.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return Integer.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.insertionExpSort(from,until);
  }

  public static void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
  {
    new InsertionExpSortAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare( buf.get(i), buf.get(j) ); }
      @Override public void   swap( int i, int j ) { Swap.swap(buf,i,j); }
    }.insertionExpSort(from,until);
  }

  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until )
  {
    if(    0 > from      ) throw new IndexOutOfBoundsException();
    if(until < from      ) throw new IndexOutOfBoundsException();
    if(until > seq.length) throw new IndexOutOfBoundsException();

    for( int i=from; ++i < until; )
    {
      T piv = seq[i];
      int lo = from,
          hi = i-1;

      for( int step=0;; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int k = hi-step;
        if( k < lo ) break;
        int c = piv.compareTo(seq[k]);
        if( c >= 0 ) {  lo = +1 + k; break; }
        else            hi = -1 + k;
      }

      // binary search phase
      while( lo <= hi ) {       int mid = lo+hi >>> 1,
              c = piv.compareTo(seq[mid]);
        if( c < 0 )       hi = -1 + mid;
        else              lo = +1 + mid;
      }

      arraycopy(seq,lo, seq,lo+1, i-lo);
      seq[lo] = piv;
    }
  }

  public static <T> void sort( T[] seq, int from, int until, Comparator<? super T> cmp )
  {
    if(    0 > from      ) throw new IndexOutOfBoundsException();
    if(until < from      ) throw new IndexOutOfBoundsException();
    if(until > seq.length) throw new IndexOutOfBoundsException();

    for( int i=from; ++i < until; )
    {
      T piv = seq[i];
      int lo = from,
          hi = i-1;

      for( int step=0;; step = 1 + 2*step ) // <- make step have all bits set such that binary search is optimally efficient
      {
        int k = hi-step;
        if( k < lo ) break;
        int c = cmp.compare(piv,seq[k]);
        if( c >= 0 ) {    lo = +1 + k; break; }
        else              hi = -1 + k;
      }

      // binary search phase
      while( lo <= hi ) {        int mid = lo+hi >>> 1,
            c = cmp.compare(piv, seq[mid]);
        if( c < 0 )        hi = -1 + mid;
        else               lo = +1 + mid;
      }

      arraycopy(seq,lo, seq,lo+1, i-lo);
      seq[lo] = piv;
    }

//    new InsertionSortV3Access() {
//      @Override public int compare( int i, int j ) { return cmp.compare( seq[i], seq[j] ); }
//      @Override public void   swap( int i, int j ) { Swap.swap(seq,i,j); }
//    }.insertionSortV3(from,until);
  }

  public static void sort(   byte[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(   byte[] seq, ComparatorByte   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(  short[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(  short[] seq, ComparatorShort  cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(    int[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(    int[] seq, ComparatorInt    cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(   long[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(   long[] seq, ComparatorLong   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(   char[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(   char[] seq, ComparatorChar   cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort(  float[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort(  float[] seq, ComparatorFloat  cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort( double[] seq                       ) { sort(seq,    0,seq.length     ); }
  public static void sort( double[] seq, ComparatorDouble cmp ) { sort(seq,    0,seq.length, cmp); }

  public static void sort( IntBuffer buf                    ) { sort(buf, buf.position(),buf.limit()     ); }
  public static void sort( IntBuffer buf, ComparatorInt cmp ) { sort(buf, buf.position(),buf.limit(), cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                            )  { sort(seq,    0,seq.length     ); }
  public static <T>                               void sort( T[] seq, Comparator<? super T> cmp )  { sort(seq,    0,seq.length, cmp); }

// FIELDS

// CONSTRUCTORS
  private InsertionExpSort() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
