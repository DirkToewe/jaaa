package com.github.jaaa.sort;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Math.*;
import static java.util.Comparator.naturalOrder;
import static com.github.jaaa.util.IMath.log2Ceil;
import static java.util.Objects.requireNonNull;

public class ParallelSkipMergeSortV3
{
  // Implementation Details
  // ----------------------
  // Builds a tree of merge tasks, where the leaf do the actual computation of a chunk of the result.
  // The branches coordinate/synchronize the work of the leaves and trigger merges when their children
  // are already sorted. The actual merge computation however is done by the leaves individually.
  //
  // How can we compute an individual chunk of a larger merge? Well, we can use binary search to
  // skip elements from the halves that are to be merged in O(log(n)) and then start computing the
  // merged chunk.
  private static final class MergeSortTask<T> extends CountedCompleter<Void>
  {
    private final int h0, h, off, pos, len;
    private       int run = 0;
    private final Comparator<? super T> cmp;
    private Object obj0 = null,
                   obj1 = null;

    public MergeSortTask( CountedCompleter<?> parent, int _h0, int _h, T[] arr, T[] buf, Comparator<? super T> _cmp, int _off, int _pos, int _len )
    {
      super(parent);
//      assert _h0  >= 0; assert arr != null;
//      assert _h   >= 0; assert buf != null;
//      assert _len >= 0;
//      assert _pos >= 0;
      obj0 = arr; off = _off; h0 = _h0;
      obj1 = buf; pos = _pos; h  = _h;
      cmp = _cmp; len = _len;
    }

    @Override
    public void compute()
    {
      if( pos < len )
      {
        if( h <= h0 )
        {
          int N = 1 << h0;

          if( 0 == run ) {
//            InsertionSortV2.sort( (T[]) obj0, off + pos, off + min(pos+N,len), cmp );
//            Arrays.sort( (T[]) obj0, off + pos, off + min(pos+N, len), cmp );
            int from = pos,
               until = min(pos+N, len);
            ComparatorTimSort.sort(
              (T[]) obj0, off+from, off+until, cmp,
              (T[]) obj1, from, until-from
            );
          }
          else
          {
            // determine source and destination
                           T[] A,              B;          int A0,       B0;
            if( run%2 == 1 ) { A = (T[]) obj0; B = (T[]) obj1; A0 = off; B0 = 0; }
            else             { B = (T[]) obj0; A = (T[]) obj1; B0 = off; A0 = 0; }

            // determine size and position of the merged sources
            int  L = N << run,
                 k = pos,    skip = k%L,
               off = A0 + (k-skip), lenL = min(A0+len-off, L/2),
               mid = off + lenL,    lenR = min(A0+len-mid, L/2),
               lo = max(skip-lenR, 0),
               hi = min(skip,lenL);

            // use binary search to skip beginning of merged sequence
            while( lo < hi ) {
              int l = lo+hi >>> 1,
                  r = skip - l;
                   if( 0 < r        && cmp.compare(A[off+l  ], A[mid+r-1]) <= 0 ) lo = l+1;
              else if(     r < lenR && cmp.compare(A[off+l-1], A[mid+r  ]) >  0 ) hi = l-1;
              else lo=hi=l;
            }

            int l = lo,
                r = skip-l,
                K = min(k+N, len);

            // merge
            for( ; k < K; k++ )
              B[B0+k] = r==lenR || l < lenL && cmp.compare(A[off+l],
                                                           A[mid+r]) <= 0
                ? A[off + l++]
                : A[mid + r++];
          }
        }
        else
        {
          int pend = 2;
          if( 0 == run ) {
            T[] arr = (T[]) obj0,
                buf = (T[]) obj1;
            obj0 = new MergeSortTask<>(this, h0,h-1, arr,buf, cmp, off,pos         ,len);
            obj1 = new MergeSortTask<>(this, h0,h-1, arr,buf, cmp, off,pos+(1<<h-1),len);
            if( 1+h0 < h ) pend = 4;
          }
          setPendingCount(pend);
          ( (MergeSortTask<?>) obj1 ).fork();
          ( (MergeSortTask<?>) obj0 ).compute();
        }
      }

      tryComplete();
    }

    @Override
    public void onCompletion( CountedCompleter<?> caller ) {
      if( 0 == run++ && h0 < h )
        compute(); // <- trigger merge on this tree level
    }
  }

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
      int  H0 = max(8, h-2-log2Ceil(nPar));
      h0 = H0 - (h-H0)%2; // <- make sure there's an even number of tree levels, such that seq always contains final result
    }

    if( 1==nPar || h <= h0 ) {
      Arrays.sort(seq, from,until, cmp);
      return;
    };

    T[] buf = (T[]) Array.newInstance(seq.getClass().getComponentType(), len);

    new CountedCompleter<Void>() {
      @Override public void compute() {
        setPendingCount(2);
        new MergeSortTask<T>(this, h0,h, seq,buf, cmp, from,0,len).compute();
        tryComplete();
      }
    }.invoke();
  }
}
