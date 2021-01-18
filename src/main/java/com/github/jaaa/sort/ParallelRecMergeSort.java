package com.github.jaaa.sort;

import com.github.jaaa.Swap;
import com.github.jaaa.merge.*;
import com.github.jaaa.search.BinarySearchAccessor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.lang.Math.subtractExact;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;

public class ParallelRecMergeSort
{
// STATIC FIELDS
  public static interface Accessor<T> extends ParallelRebelMerge.Accessor
  {
    void sort( T arr, int arr0, T buf, int buf0, int len );
  }

  private static final class AccessorArrayObj<T> implements ParallelRecMergeSortTask.Context <T[]>,
                                                            ParallelRebelMerge.Accessor<T[]>,
                                                                         BinarySearchAccessor<T[]>,
                                                                        TapeMergePartAccessor<T[]>
  {
  // FIELDS
    private final Comparator<? super T> cmp;

  // CONSTRUCTORS
    public AccessorArrayObj( Comparator<? super T> _cmp ) { cmp = requireNonNull(_cmp); }

  // METHODS
    @Override public int        len( T[] buf ) { return buf.length; }
    @Override public void      copy( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
    @Override public void      swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int    compare( T[] a, int i, T[] b, int j ) { return cmp.compare(a[i], b[j]);  }
    @Override public int searchGapL( T[] a, int a0, int aLen, T[] b, int key ) { return binarySearchGapL(a,a0,aLen, b,key); }
    @Override public int searchGapR( T[] a, int a0, int aLen, T[] b, int key ) { return binarySearchGapR(a,a0,aLen, b,key); }
    @Override public void mergePart( T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0, int cLen ) { tapeMergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen); }
    @Override public void      sort( T[] arr, int arr0, T[] buf, int buf0, int len ) { ComparatorTimSort.sort(arr, arr0,arr0+len, cmp, buf,buf0,len); }

    @Override public CountedCompleter<?> newMergeTask( int height, CountedCompleter<?> completer, T[] ab, int a0, int aLen, int b0, int bLen, T[] c, int c0 )
    {
      assert height >= 0;
      int granularity = aLen+bLen >>> height;
      return new ParallelRebelMergeTask<>(
        granularity, completer,
        ab, a0, a0+aLen,
        ab, b0, b0+bLen,
         c, c0, this
      );
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { sort( seq, 0,seq.length, naturalOrder()); }
  public static <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { sort( seq,  from, until, naturalOrder()); }
  public static <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { sort( seq, 0,seq.length, cmp ); }
  public static <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp ) {
    if(    0 > from      ) throw new IllegalArgumentException();
    if(until < from      ) throw new IllegalArgumentException();
    if(until > seq.length) throw new IllegalArgumentException();
    requireNonNull(cmp);

    int len = subtractExact(until,from),
       nPar = ForkJoinPool.getCommonPoolParallelism();

    int    h0,         h = log2Ceil( max(1,len) ); {
      int  H0 = max(8, h-2-log2Ceil(nPar)); // <- spawn at least 4 tasks per cpu core for load balancing
      h0 = H0 - (h-H0)%2; // <- make sure there's an even number of tree levels, such that seq always contains final result
    }

    if( 1==nPar || h <= h0 ) {
      Arrays.sort(seq, from,until, cmp);
      return;
    };

    var ctx = new AccessorArrayObj(cmp);

    T[] buf = (T[]) Array.newInstance(seq.getClass().getComponentType(), len);

    new ParallelRecMergeSortTask<T[]>(h-h0, null, seq,from, buf,0, len, ctx).invoke();
  }

// FIELDS

// CONSTRUCTORS
  private ParallelRecMergeSort() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS

}
