package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.search.BinarySearchAccessor;

import java.util.concurrent.ForkJoinPool;

import static java.util.Objects.requireNonNull;


public class ParallelRebelMerge
{
// STATIC FIELDS
  public interface Accessor<T>
  {
    void      copy( T a, int i, T b, int j );
    int searchGapL( T a, int a0, int aLen, T b, int key );
    int searchGapR( T a, int a0, int aLen, T b, int key );
    void mergePart(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );
  }

  public static class ParallelRebelMerger implements Merger
  {
    // STATIC FIELDS
    private static class DefaultContext<T> implements ParallelRebelMerge.Accessor<T>,
                                                             BinarySearchAccessor<T>,
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
      @Override public int searchGapL( T a, int a0, int aLen, T b, int key ) { return binarySearchGapL(a,a0,aLen, b,key); }
      @Override public int searchGapR( T a, int a0, int aLen, T b, int key ) { return binarySearchGapR(a,a0,aLen, b,key); }
      @Override public void mergePart( T a, int a0, int aLen,  T b, int b0, int bLen,  T c, int c0, int cLen ) { tapeMergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,cLen); }
    }

    // STATIC CONSTRUCTOR

    // STATIC METHODS

    // FIELDS
    private final ForkJoinPool pool;

    // CONSTRUCTORS
    public ParallelRebelMerger(ForkJoinPool _pool ) {
      pool = requireNonNull(_pool);
    }

    // METHODS
    @Override public <T> void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, CompareRandomAccessor<T> acc
    ) {

      int nPar = pool.getParallelism(),
                      cLen = aLen + bLen,
        granularity = cLen / (8*nPar);

      var ctx = new DefaultContext<T>(acc);

      if( nPar <= 1 || granularity <= 1024 ) ctx.mergePart(a,a0,aLen, b,b0,bLen, c,c0,cLen);
      else pool.invoke(
        new ParallelRebelMergeTask<>(granularity, null, a,a0,a0+aLen, b,b0,b0+bLen, c,c0, ctx)
      );
    }
  }

  public static final Merger PARALLEL_REBEL_MERGER = new ParallelRebelMerger( ForkJoinPool.commonPool() );

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

  // CONSTRUCTORS
  private ParallelRebelMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
