package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;

import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;


public class ParallelSkipMerge
{
// STATIC FIELDS
  interface Accessor<T>
  {
    int mergeOffset(
      T a, int a0, int aLen,
      T b, int b0, int bLen, int nSkip
    );
    void mergePart(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );
  }

  private static class AccAcc<T> implements ParallelSkipMerge.Accessor<T>,
                                             BinaryMergeOffsetAccessor<T>,
                                                 TapeMergePartAccessor<T>
  {
    // FIELDS
    private final CompareRandomAccessor<T> acc;

    // CONSTRUCTORS
    public AccAcc( CompareRandomAccessor<T> _acc ) {
      acc = requireNonNull(_acc);
    }

    // METHODS
    @Override public int        len( T buf ) { return acc.len(buf); }
    @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
    @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
    @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }

    @Override public int mergeOffset( T a, int a0, int aLen,  T b, int b0, int bLen,  int nSkip ) { return binaryMergeOffset(a,a0,aLen, b,b0,bLen, nSkip); }
    @Override public void  mergePart( T a, int a0, int aLen,  T b, int b0, int bLen,  T c, int c0, int cLen ) { tapeMergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen); }
  }

  public static class ParallelSkipMerger implements Merger
  {
  // STATIC FIELDS

  // STATIC CONSTRUCTOR

  // STATIC METHODS

  // FIELDS
    private final ForkJoinPool pool;

  // CONSTRUCTORS
    public ParallelSkipMerger( ForkJoinPool _pool ) {
      pool = requireNonNull(_pool);
    }

  // METHODS
    @Override public <T> void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, CompareRandomAccessor<T> acc
    ){
      int nPar = pool.getParallelism(),
            h  =              log2Ceil( max(1,aLen+bLen) ),
            h0 = max( 10, h-3-log2Ceil(nPar) ); // <- spwan roughly 4 tasks per cpu thread, but only if copied chunks size don't fall below 2^16

      var ctx = new AccAcc<T>(acc);

      if( nPar <= 1 || h0 >= h ) ctx.mergePart(a,a0,aLen, b,b0,bLen, c,c0,aLen+bLen);
      else pool.invoke(
        new ParallelSkipMergeTask<>(h-h0, null, a,a0,aLen, b,b0,bLen, c,c0,0,aLen+bLen, ctx)
      );
    }
  }

  public static final Merger PARALLEL_SKIP_MERGER = new ParallelSkipMerger( ForkJoinPool.commonPool() );

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS
  private ParallelSkipMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
