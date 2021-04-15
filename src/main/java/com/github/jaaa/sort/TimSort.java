package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.Comparator;

import static java.lang.System.arraycopy;


public final class TimSort
{
// STATIC FIELDS
  public static Sorter TIM_SORTER = new Sorter()
  {
    @Override public boolean isThreadSafe() { return true; }
    @Override public boolean isStable    () { return true; }

    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc ) { TimSort.sort(seq,from,until,acc); }

    @Override public void sort(      byte[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(      byte[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(      byte[] seq,                      ComparatorByte   cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(      byte[] seq, int from, int until, ComparatorByte   cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(     short[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(     short[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(     short[] seq,                      ComparatorShort  cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(     short[] seq, int from, int until, ComparatorShort  cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(       int[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(       int[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(       int[] seq,                      ComparatorInt    cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(       int[] seq, int from, int until, ComparatorInt    cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(      long[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(      long[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(      long[] seq,                      ComparatorLong   cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(      long[] seq, int from, int until, ComparatorLong   cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(      char[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(      char[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(      char[] seq,                      ComparatorChar   cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(      char[] seq, int from, int until, ComparatorChar   cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(     float[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(     float[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(     float[] seq,                      ComparatorFloat  cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(     float[] seq, int from, int until, ComparatorFloat  cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(    double[] seq                                            ) { TimSort.sort(seq,    0,seq.length     ); }
    @Override public void sort(    double[] seq, int from, int until                       ) { TimSort.sort(seq, from,until          ); }
    @Override public void sort(    double[] seq,                      ComparatorDouble cmp ) { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public void sort(    double[] seq, int from, int until, ComparatorDouble cmp ) { TimSort.sort(seq, from,until,      cmp); }

    @Override public void sort(   IntBuffer buf                                            ) { TimSort.sort(buf, buf.position(),buf.limit()     ); }
    @Override public void sort(   IntBuffer buf, int from, int until                       ) { TimSort.sort(buf,           from,until           ); }
    @Override public void sort(   IntBuffer buf,                      ComparatorInt    cmp ) { TimSort.sort(buf, buf.position(),buf.limit(), cmp); }
    @Override public void sort(   IntBuffer buf, int from, int until, ComparatorInt    cmp ) { TimSort.sort(buf,           from,until,       cmp); }

    @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { TimSort.sort(seq,    0,seq.length     ); }
    @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { TimSort.sort(seq, from,until          ); }
    @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { TimSort.sort(seq,    0,seq.length, cmp); }
    @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { TimSort.sort(seq, from,until,      cmp); }
  };

  private static abstract class AccArrObj<T> implements TimSortAccessorArrObj<T>
  {
    private final Class<?> elemType;
    AccArrObj( Class<?> elemType ) { this.elemType = elemType; }
    @Override public T[] malloc( int len ) { return (T[]) Array.newInstance(elemType, len); }
  }
  private interface AccArrByte   extends TimSortAccessor<   byte[]>, RandomAccessorArrByte  {}
  private interface AccArrShort  extends TimSortAccessor<  short[]>, RandomAccessorArrShort {}
  private interface AccArrInt    extends TimSortAccessor<    int[]>, RandomAccessorArrInt   {}
  private interface AccArrLong   extends TimSortAccessor<   long[]>, RandomAccessorArrLong  {}
  private interface AccArrChar   extends TimSortAccessor<   char[]>, RandomAccessorArrChar  {}
  private interface AccArrFloat  extends TimSortAccessor<  float[]>, RandomAccessorArrFloat {}
  private interface AccArrDouble extends TimSortAccessor< double[]>, RandomAccessorArrDouble{}
  private interface AccBufInt    extends TimSortAccessor<IntBuffer>, RandomAccessorBufInt   {}

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc )
  {
    new TimSortAccessor<T>() {
      @Override public T malloc( int len ) { return acc.malloc(len); }
      @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
    }.timSort(seq,from,until, null,0,0);
  }

  public static void sort(   byte[] seq, int from, int until )                       { ( (AccArrByte  ) (a,i, b,j) ->      Byte.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { ( (AccArrByte  ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(  short[] seq, int from, int until )                       { ( (AccArrShort ) (a,i, b,j) ->     Short.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { ( (AccArrShort ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(    int[] seq, int from, int until )                       { ( (AccArrInt   ) (a,i, b,j) ->   Integer.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { ( (AccArrInt   ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(   long[] seq, int from, int until )                       { ( (AccArrLong  ) (a,i, b,j) ->      Long.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { ( (AccArrLong  ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(   char[] seq, int from, int until )                       { ( (AccArrChar  ) (a,i, b,j) -> Character.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { ( (AccArrChar  ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(  float[] seq, int from, int until )                       { ( (AccArrFloat ) (a,i, b,j) ->     Float.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { ( (AccArrFloat ) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort( double[] seq, int from, int until )                       { ( (AccArrDouble) (a,i, b,j) ->    Double.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { ( (AccArrDouble) (a,i, b,j) ->       cmp.compare(a[i], b[j]) ).timSort(seq,from,until, null,0,0); }
  public static void sort(IntBuffer buf, int from, int until )                       { ( (AccBufInt   ) (a,i, b,j) ->   Integer.compare( a.get(i), b.get(j) ) ).timSort(buf,from,until, null,0,0); }
  public static void sort(IntBuffer buf, int from, int until, ComparatorInt    cmp ) { ( (AccBufInt   ) (a,i, b,j) ->       cmp.compare( a.get(i), b.get(j) ) ).timSort(buf,from,until, null,0,0); }

  public static <T extends Comparable<? super T>> void sort( T[] arr, int from, int until )
  {
    if( arr.length < until ) throw new IndexOutOfBoundsException();
    new AccArrObj<T>( arr.getClass().getComponentType() ) {
      @Override public int compare( T[] a, int i, T[] b, int j ) { return a[i].compareTo(b[j]); }
    }.timSort(arr,from,until, null,0,0);
  }

  public static <T> void sort( T[] arr, int from, int until, Comparator<? super T> cmp )
  {
    if( arr.length < until ) throw new IndexOutOfBoundsException();
    new AccArrObj<T>( arr.getClass().getComponentType() ) {
      @Override public int compare( T[] a, int i, T[] b, int j ) { return cmp.compare(a[i], b[j]); }
    }.timSort(arr,from,until, null,0,0);
  }

  public static void sort(      byte[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(      byte[] arr, ComparatorByte   cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(     short[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(     short[] arr, ComparatorShort  cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(       int[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(       int[] arr, ComparatorInt    cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(      long[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(      long[] arr, ComparatorLong   cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(      char[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(      char[] arr, ComparatorChar   cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(     float[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(     float[] arr, ComparatorFloat  cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(    double[] arr                       ) { sort(arr, 0,arr.length     ); }
  public static void sort(    double[] arr, ComparatorDouble cmp ) { sort(arr, 0,arr.length, cmp); }
  public static void sort(   IntBuffer buf                       ) { sort(buf, buf.position(),buf.limit()     ); }
  public static void sort(   IntBuffer buf, ComparatorInt    cmp ) { sort(buf, buf.position(),buf.limit(), cmp); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                            )  { sort(seq, 0,seq.length     ); }
  public static <T>                               void sort( T[] seq, Comparator<? super T> cmp )  { sort(seq, 0,seq.length, cmp); }

// FIELDS
// CONSTRUCTORS
  private TimSort() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
