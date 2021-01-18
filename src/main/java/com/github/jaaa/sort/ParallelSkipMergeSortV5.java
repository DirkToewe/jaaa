package com.github.jaaa.sort;

import com.github.jaaa.Swap;
import com.github.jaaa.merge.BinaryMergeOffsetAccessor;
import com.github.jaaa.merge.TapeMergePartAccessor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.lang.Math.subtractExact;
import static java.util.Comparator.naturalOrder;
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

public class ParallelSkipMergeSortV5
{
// STATIC FIELDS
  public static interface Accessor<T>
  {
    public void sort( T arr, int arr0, T buf, int buf0, int len );
    public int mergeOffset( T ab, int a0, int aLen,
                                  int b0, int bLen, int nSkip );
    public void mergePart(
      T ab, int a0, int aLen,
            int b0, int bLen,
      T c,  int c0, int cLen
    );
  }

  private static final class AccessorArrayObj<T> implements Accessor<T[]>,
                                           BinaryMergeOffsetAccessor<T[]>,
                                               TapeMergePartAccessor<T[]>
  {
    private final Comparator<? super T> cmp;

    AccessorArrayObj(Comparator<? super T> _cmp ) { cmp = requireNonNull(_cmp); }

    @Override public int          len( T[] buf ) { return buf.length; }
    @Override public void   copyRange( T[] a, int i, T[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void   copy     ( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
    @Override public void        swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int      compare( T[] a, int i, T[] b, int j ) { return cmp.compare( a[i], b[j]); }
    @Override public void        sort( T[] arr, int arr0, T[] buf, int buf0, int len ) { ComparatorTimSort.sort(arr, arr0,arr0+len, cmp, buf,buf0,len); }
    @Override public int  mergeOffset( T[] ab, int a0, int aLen, int b0, int bLen, int nSkip ) {
      return binaryMergeOffset(
        ab,a0,aLen,
        ab,b0,bLen, nSkip
      );
    }
    @Override public void mergePart( T[] ab, int a0, int aLen, int b0, int bLen, T[] c, int c0, int cLen )
    {
      tapeMergePartL2R(
        ab,a0,aLen,
        ab,b0,bLen,
        c, c0,cLen
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

    new ParallelSkipMergeSortV5Task<T[]>(h-h0, null, seq,from, buf,0, len, ctx).invoke();
  }

// FIELDS

  // CONSTRUCTORS
  private ParallelSkipMergeSortV5() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
