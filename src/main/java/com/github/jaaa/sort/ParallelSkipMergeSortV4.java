package com.github.jaaa.sort;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import com.github.jaaa.merge.BinaryMergeOffsetAccessor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;

import static java.lang.Math.*;
import static java.util.Comparator.naturalOrder;
import static com.github.jaaa.util.IMath.log2Ceil;
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

public class ParallelSkipMergeSortV4
{
// STATIC FIELDS
  private static final class MergeSortContextArrayObj<T> implements Context<T[]>,
                                                  BinaryMergeOffsetAccessor<T[]>
  {
    private final Comparator<? super T> cmp;

    MergeSortContextArrayObj( Comparator<? super T> _cmp ) {
      cmp = requireNonNull(_cmp);
    }

    @Override public int         len( T[] buf ) { return buf.length; }
    @Override public void  copyRange( T[] a, int i, T[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    @Override public void  copy     ( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
    @Override public void       swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
    @Override public int     compare( T[] a, int i, T[] b, int j ) { return cmp.compare( a[i], b[j]); }
    @Override public void       sort( T[] arr, int arr0, T[] buf, int buf0, int len ) { ComparatorTimSort.sort(arr, arr0,arr0+len, cmp, buf,buf0,len); }
    @Override public int mergeOffset( T[] ab, int a0, int aLen, int b0, int bLen, int nSkip ) { return binaryMergeOffset(ab,a0,aLen, ab,b0,bLen, nSkip); }
  }

  static interface Context<T> extends CompareRandomAccessor<T>
  {
    public void sort( T arr, int arr0, T buf, int buf0, int len );
    public int mergeOffset( T ab, int a0, int aLen,
                                  int b0, int bLen, int nSkip );
    public default void mergePart(
      T ab, int a0, int aLen,
            int b0, int bLen,
      T c,  int c0, int cLen
    ) {
      assert aLen >= 0;
      assert bLen >= 0;
      assert cLen >= 0;
      assert cLen <= aLen + bLen;

      for( int i=0,j=0,k=0; k < cLen; k++ ) {
        int ij = j >= bLen || i < aLen && compare(ab,a0+i, ab,b0+j) <= 0
          ? a0 + i++
          : b0 + j++;
        copy(ab,ij, c,c0+k);
      }
    }
  }

  private static final class MergeSortTask<T> extends CountedCompleter<Void>
  {
    private long stack = 0;
    private final int          dstPos, dstLen;
    private int    src0, dst0, srcPos, srcLen, height;
    private Object src,  dst;
    private Context<? super T> ctx;

    public MergeSortTask(
      CountedCompleter<?> parent, int _height,
      Context<? super T> _ctx,
      T _src, int _src0,
      T _dst, int _dst0,
      long _stack, int pos, int len
    )
    {
      super(parent);
      assert 0 <= _height;
      assert 0 < len;

      ctx = requireNonNull(_ctx);

      src =_src; src0 =_src0;
      dst =_dst; dst0 =_dst0;
      srcPos = dstPos = pos;
      srcLen = dstLen = len;
      height =_height;
      stack = _stack;
    }

    @Override
    public void compute()
    {
      if( 0 == height )
      {
        T src = (T) this.src,
          dst = (T) this.dst;
        int src0 = this.src0,
            dst0 = this.dst0;

        if( srcLen == dstLen ) {
          assert srcPos == dstPos;
          ctx.sort(
            src,src0+srcPos,
            dst,dst0+srcPos,
                     srcLen
          );
        }
        else
        {
          int nSkip = dstPos - srcPos,
               lenL = srcLen >>> 1,  l0 = src0 + srcPos,
               lenR = srcLen - lenL, r0 = l0 + lenL;

          int l = ctx.mergeOffset(src,l0,lenL, r0,lenR, nSkip),
              r = nSkip - l;

          ctx.mergePart(
            src, l0+l, lenL-l,
                 r0+r, lenR-r,
            dst, dst0+dstPos, dstLen
          );

          // swap buffers to change merge direction next time
          this.src = dst; this.src0 = dst0;
          this.dst = src; this.dst0 = src0;
        }

        int oddBit = (int) (stack & 1); stack >>>= 1; // <- bit set if parent merge range is odd
        int rhsBit = (int) (stack & 1); stack >>>= 1; // <- bit set if current merge range is a right-hand side child
        // updated src range to next merge
        srcLen <<= 1;
        srcLen += oddBit * (1 - 2*rhsBit);
        srcPos -= rhsBit * (srcLen >>> 1);
      }
      else
      {
        int pend = 2;
        if( 0 < height ) {
          T src = (T) this.src,
            dst = (T) this.dst;

          assert srcLen == dstLen;
          assert srcPos == dstPos;

          long stack = dstLen&1L | this.stack<<2;
          int    len = dstLen >>> 1;

          this.src = new MergeSortTask<T>(this, height-1, ctx, src,src0, dst,dst0, stack,    dstPos,            len);
          this.dst = new MergeSortTask<T>(this, height-1, ctx, src,src0, dst,dst0, stack|2L, dstPos+len, dstLen-len);
          // first time around, we need child nodes to finish twice, once for sorting and once for merging
          if( 1 < height ) pend = 4;
        }
        setPendingCount(pend);
        ( (MergeSortTask<?>) dst ).fork();
        ( (MergeSortTask<?>) src ).compute();
      }

      tryComplete();
    }

    @Override
    public void onCompletion( CountedCompleter<?> caller ) {
      if( height >  0 ) {
          height = -1;
          compute(); // <- trigger merge on this tree level TODO: fork here maybe to prevent stack overflow?
      }
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

    var ctx = new MergeSortContextArrayObj(cmp);

    T[] buf = (T[]) Array.newInstance(seq.getClass().getComponentType(), len);

    new CountedCompleter<Void>() {
      @Override public void compute() {
      setPendingCount(2);
      new MergeSortTask<T[]>(this, h-h0, ctx, seq,from, buf,0, 0L, 0,len).compute();
      tryComplete();
      }
    }.invoke();
  }

// FIELDS

// CONSTRUCTORS
  private ParallelSkipMergeSortV4() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
