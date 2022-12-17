package com.github.jaaa.sort;

import com.github.jaaa.compare.*;
import com.github.jaaa.copy.*;
import com.github.jaaa.merge.ExpMergeOffsetAccessor;
import com.github.jaaa.merge.TimMergePartAccessor;

import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;


// Implementation Details
// ----------------------
// Builds a tree of merge tasks, where the leaf do the actual computation of a chunk of the result.
// The branches coordinate/synchronize the work of the leaves and trigger merges when their children
// are already sorted. The actual merge computation however is done by the leaves individually.
//
// How can we compute an individual chunk of a larger merge? Well, we can use binary search to
// skip elements from the halves that are to be merged in O(log(n)) and then start computing only
// the merged chunk.

public class ParallelSkipMergeSort
{
// STATIC FIELDS
  public interface Accessor<T>
  {
    void parallelSkipMergeSort_sort( T arr, int arr0, int arr1, T buf, int buf0, int buf1 );
    int  parallelSkipMergeSort_mergeOffset( T ab, int a0, int aLen,
                                                  int b0, int bLen, int nSkip );
    void parallelSkipMergeSort_mergePart(
      T ab, int a0, int aLen,
            int b0, int bLen,
      T c,  int c0, int cLen
    );
  }

  public static class ParallelSkipMergeSorter implements Sorter
  {
  // STATIC FIELDS
    private interface Acc<T> extends ParallelSkipMergeSort.Accessor<T>,
                                             ExpMergeOffsetAccessor<T>,
                                               TimMergePartAccessor<T>,
                                                    TimSortAccessor<T>
    {
      @Override default void parallelSkipMergeSort_sort( T arr, int arr0, int arr1,
                                                         T buf, int buf0, int buf1 ) { timSort(arr,arr0,arr1, buf,buf0,buf1); }
      @Override default int parallelSkipMergeSort_mergeOffset( T ab, int a0, int aLen, int b0, int bLen, int nSkip ) {
        return expMergeOffset(
          ab,a0,aLen,
          ab,b0,bLen, nSkip
        );
      }
      @Override default void parallelSkipMergeSort_mergePart( T ab, int a0, int aLen, int b0, int bLen, T c, int c0, int cLen )
      {
        timMergePartL2R(
          ab,a0,aLen,
          ab,b0,bLen,
          c, c0,cLen
        );
      }
    }

    private static abstract class AccArrObj<T> implements Acc<         T[]>, TimSortAccessorArrObj<T>{}
    private             interface AccArrByte   extends    Acc<      byte[]>, RandomAccessorArrByte   {}
    private             interface AccArrShort  extends    Acc<     short[]>,  RandomAccessorArrShort {}
    private             interface AccArrInt    extends    Acc<       int[]>, RandomAccessorArrInt    {}
    private             interface AccArrLong   extends    Acc<      long[]>,  RandomAccessorArrLong  {}
    private             interface AccArrChar   extends    Acc<      char[]>, RandomAccessorArrChar   {}
    private             interface AccArrFloat  extends    Acc<     float[]>, RandomAccessorArrFloat  {}
    private             interface AccArrDouble extends    Acc<    double[]>, RandomAccessorArrDouble {}
    private             interface AccBufInt    extends    Acc<   IntBuffer>,  RandomAccessorBufInt   {}

  // STATIC CONSTRUCTOR

  // STATIC METHODS

  // FIELDS
    private final ForkJoinPool pool;

  // CONSTRUCTORS
    public ParallelSkipMergeSorter( ForkJoinPool _pool ) {
      pool = requireNonNull(_pool);
    }

  // METHODS
    @Override public boolean isStable()     { return true; }
    @Override public boolean isThreadSafe() { return true; }


    private <T> void sort( T arr, int from, int until, Acc<T> ctx )
    {
      if( from < 0     ) throw new IndexOutOfBoundsException();
      if( from > until ) throw new IndexOutOfBoundsException();
      requireNonNull(ctx);

      int len = until-from,
         nPar = pool.getParallelism();

      int  h0,          h = log2Ceil( max(1,len) ); {
      int  H0 = max(13, h-4-log2Ceil(nPar)); // <- spawn at least 4 tasks per cpu core for load balancing
      h0 = H0 - (h-H0)%2; // <- make sure there's an even number of tree levels, such that seq always contains final result
    }

      if( 1==nPar || h <= h0 )
        ctx.parallelSkipMergeSort_sort(arr,from,until, null,0,0);
      else {
        T buf = ctx.malloc(len);
        pool.invoke(
          new ParallelSkipMergeSortTask<>(h-h0, null, arr,from, buf,0, len, ctx)
        );
      }
    }


    @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc )
    {
      Acc<T> ctx = new Acc<T>() {
        @Override public T malloc(int len) { return acc.malloc(len); }
        @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
        @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
        @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
        @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
      };
      sort(seq,from,until, ctx);
    }


    @Override public <T> void sort( T[] arr, int from, int until, Comparator<? super T> cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();

      Class<?> elemType = arr.getClass().getComponentType();
      AccArrObj<T> ctx = new AccArrObj<T>() {
        @Override public T[] malloc( int len ) { return (T[]) Array.newInstance(elemType,len); }
        @Override public int compare( T[] a, int i, T[] b, int j ) { return cmp.compare(a[i], b[j]); }
      };

      sort(arr,from,until, ctx);
    }
    @Override public <T extends Comparable<? super T>> void sort( T[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();

      Class<?> elemType = arr.getClass().getComponentType();
      AccArrObj<T> ctx = new AccArrObj<T>() {
        @Override public T[] malloc( int len ) { return (T[]) Array.newInstance(elemType,len); }
        @Override public int compare( T[] a, int i, T[] b, int j ) { return a[i].compareTo(b[j]); }
      };

      sort(arr,from,until, ctx);
    }


    @Override public void sort( byte[] arr, int from, int until, ComparatorByte cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrByte           ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( byte[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrByte           ctx = (a,i, b,j) -> Byte.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( short[] arr, int from, int until, ComparatorShort cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrShort          ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( short[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrShort          ctx = (a,i, b,j) -> Short.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( int[] arr, int from, int until, ComparatorInt cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrInt            ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( int[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrInt            ctx = (a,i, b,j) -> Integer.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( long[] arr, int from, int until, ComparatorLong cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrLong           ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( long[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrLong           ctx = (a,i, b,j) -> Long.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( char[] arr, int from, int until, ComparatorChar cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrChar           ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( char[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrChar           ctx = (a,i, b,j) -> Character.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( float[] arr, int from, int until, ComparatorFloat cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrFloat          ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( float[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrFloat          ctx = (a,i, b,j) -> Float.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( double[] arr, int from, int until, ComparatorDouble cmp )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrDouble         ctx = (a,i, b,j) -> cmp.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }
    @Override public void sort( double[] arr, int from, int until )
    {
      if( arr.length < until ) throw new IndexOutOfBoundsException();
      AccArrDouble         ctx = (a,i, b,j) -> Double.compare(a[i], b[j]);
      sort(arr,from,until, ctx);
    }


    @Override public void sort( IntBuffer buf, int from, int until, ComparatorInt cmp )
    {
      if( buf.limit() < until ) throw new IndexOutOfBoundsException();
      AccBufInt            ctx = (a,i, b,j) -> cmp.compare(a.get(i), b.get(j));
      sort(buf,from,until, ctx);
    }
    @Override public void sort( IntBuffer buf, int from, int until )
    {
      if( buf.limit() < until ) throw new IndexOutOfBoundsException();
      AccBufInt            ctx = (a,i, b,j) -> Integer.compare(a.get(i), b.get(j));
      sort(buf,from,until, ctx);
    }
  }

  public static final Sorter PARALLEL_SKIP_MERGE_SORTER = new ParallelSkipMergeSorter( ForkJoinPool.commonPool() );

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc ) { PARALLEL_SKIP_MERGE_SORTER.sort(seq, from,until, acc); }

  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { PARALLEL_SKIP_MERGE_SORTER.sort(seq,    0,seq.length     ); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { PARALLEL_SKIP_MERGE_SORTER.sort(seq, from,until          ); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(seq,    0,seq.length, cmp); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(seq, from,until,      cmp); }

  public static void sort(      byte[] arr, ComparatorByte   cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(     short[] arr, ComparatorShort  cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(       int[] arr, ComparatorInt    cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(      long[] arr, ComparatorLong   cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(      char[] arr, ComparatorChar   cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(     float[] arr, ComparatorFloat  cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(    double[] arr, ComparatorDouble cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length, cmp); }
  public static void sort(   IntBuffer buf, ComparatorInt    cmp ) { PARALLEL_SKIP_MERGE_SORTER.sort(buf, buf.position(),buf.limit(), cmp); }

  public static void sort(      byte[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(     short[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(       int[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(      long[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(      char[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(     float[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(    double[] arr ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, 0,arr.length); }
  public static void sort(   IntBuffer buf ) { PARALLEL_SKIP_MERGE_SORTER.sort(buf, buf.position(),buf.limit()); }

  public static void sort(      byte[] arr, int from, int until, ComparatorByte   cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(     short[] arr, int from, int until, ComparatorShort  cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(       int[] arr, int from, int until, ComparatorInt    cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(      long[] arr, int from, int until, ComparatorLong   cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(      char[] arr, int from, int until, ComparatorChar   cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(     float[] arr, int from, int until, ComparatorFloat  cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(    double[] arr, int from, int until, ComparatorDouble cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until, cmp); }
  public static void sort(   IntBuffer buf, int from, int until, ComparatorInt    cmp) { PARALLEL_SKIP_MERGE_SORTER.sort(buf, from,until, cmp); }

  public static void sort(      byte[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(     short[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(       int[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(      long[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(      char[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(     float[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(    double[] arr, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(arr, from,until); }
  public static void sort(   IntBuffer buf, int from, int until ) { PARALLEL_SKIP_MERGE_SORTER.sort(buf, from,until); }

// FIELDS

// CONSTRUCTORS
  private ParallelSkipMergeSort() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
