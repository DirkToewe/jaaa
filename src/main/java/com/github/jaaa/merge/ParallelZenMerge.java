package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;


public class ParallelZenMerge
{
// STATIC FIELDS
  public static class ParallelZenMerger implements Merger
  {
  // STATIC FIELDS
    private static class DefaultContext<T> implements ParallelZenMergeTask.Context<T>,
                                                           BinaryMergeOffsetAccessor<T>,
                                                               TapeMergePartAccessor<T>
    {
    // FIELDS
      private final CompareRandomAccessor<T> acc;

    // CONSTRUCTORS
      public DefaultContext( CompareRandomAccessor<T> _acc ) { acc = requireNonNull(_acc); }

    // METHODS
      @Override public int        len( T buf ) { return acc.len(buf); }
      @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }

      @Override public int mergeOffset( T a, int a0, int aLen,  T b, int b0, int bLen,  int nSkip ) { return binaryMergeOffset(a,a0,aLen, b,b0,bLen, nSkip); }
      @Override public void  mergePart( T a, int a0, int aLen,  T b, int b0, int bLen,  T c, int c0, int cLen ) { tapeMergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen); }
    }

  // STATIC CONSTRUCTOR

  // STATIC METHODS

  // FIELDS
    private final ForkJoinPool pool;

  // CONSTRUCTORS
    public ParallelZenMerger(ForkJoinPool _pool ) {
      pool = requireNonNull(_pool);
    }

  // METHODS
    @Override public <T> void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, CompareRandomAccessor<T> acc
    ) {
      int nPar = pool.getParallelism(),
            h  =              log2Ceil( max(1,aLen+bLen) ),
            h0 = max( 10, h-3-log2Ceil(nPar) ); // <- spwan roughly 4 tasks per cpu thread, but only if copied chunks size don't fall below 2^16

      var ctx = new DefaultContext<T>(acc);

      if( nPar <= 1 || h0 >= h ) ctx.mergePart(a,a0,aLen, b,b0,bLen, c,c0,aLen+bLen);
      else pool.invoke(
        new ParallelZenMergeTask<>(h-h0, null, a,a0,a0+aLen, b,b0,b0+bLen, c,c0, ctx)
      );
    }
  }

  public static final Merger PARALLEL_ZEN_MERGER = new ParallelZenMerger( ForkJoinPool.commonPool() );

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

  // CONSTRUCTORS
  private ParallelZenMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
