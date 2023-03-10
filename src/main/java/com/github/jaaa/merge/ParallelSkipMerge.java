package com.github.jaaa.merge;

import com.github.jaaa.compare.*;
import com.github.jaaa.copy.*;

import java.nio.IntBuffer;
import java.util.Comparator;
import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;


public class ParallelSkipMerge
{
// STATIC FIELDS
  public interface Accessor<T>
  {
    int  skipMerge_mergeOffset(
      T a, int a0, int aLen,
      T b, int b0, int bLen, int nSkip
    );
    void skipMerge_mergePart(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );
  }

  public static class ParallelSkipMerger implements Merger
  {
  // STATIC FIELDS
    private interface Acc<T> extends ParallelSkipMerge.Accessor<T>,
                                         ExpMergeOffsetAccessor<T>,
                                           ExpMergePartAccessor<T>
    {
      @Override default int  skipMerge_mergeOffset( T a, int a0, int aLen, T b, int b0, int bLen, int nSkip ) { return expMergeOffset(a,a0,aLen, b,b0,bLen, nSkip); }
      @Override default void skipMerge_mergePart  ( T a, int a0, int aLen, T b, int b0, int bLen, T c, int c0, int cLen ) { expMergePart_L2R(a,a0,aLen, b,b0,bLen, c,c0,cLen); }
    }

    private interface AccArrObj<T> extends Acc<         T[]>, RandomAccessorArrObj<T>{ @Override default T[] malloc(int len ) { throw new UnsupportedOperationException(); } }
    private interface AccArrByte   extends Acc<      byte[]>, RandomAccessorArrByte  {}
    private interface AccArrShort  extends Acc<     short[]>, RandomAccessorArrShort {}
    private interface AccArrInt    extends Acc<       int[]>, RandomAccessorArrInt   {}
    private interface AccArrLong   extends Acc<      long[]>, RandomAccessorArrLong  {}
    private interface AccArrChar   extends Acc<      char[]>, RandomAccessorArrChar  {}
    private interface AccArrFloat  extends Acc<     float[]>, RandomAccessorArrFloat {}
    private interface AccArrDouble extends Acc<    double[]>, RandomAccessorArrDouble{}
    private interface AccBufInt    extends Acc<   IntBuffer>, RandomAccessorBufInt   {}

  // STATIC CONSTRUCTOR

  // STATIC METHODS

  // FIELDS
    private final ForkJoinPool pool;

  // CONSTRUCTORS
    public ParallelSkipMerger( ForkJoinPool _pool ) {
      pool = requireNonNull(_pool);
    }

  // METHODS
    @Override public boolean isStable() { return true; }

    private <T> void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, Acc<T> acc
    ) {
      int nPar = pool.getParallelism(),
            h  =              log2Ceil( max(1,aLen+bLen) ),
            h0 = max( 13, h-4-log2Ceil(nPar) ); // <- spwan roughly 4 tasks per cpu thread, but only if copied chunks size don't fall below 2^16

      if( nPar <= 1 || h0 >= h ) acc.skipMerge_mergePart(a,a0,aLen, b,b0,bLen, c,c0,aLen+bLen);
      else pool.invoke(
        new ParallelSkipMergeTask<>(h-h0, null, a,a0,aLen, b,b0,bLen, c,c0,0,aLen+bLen, acc)
      );
    }

    @Override public <T> void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, CompareRandomAccessor<T> acc
    ) {
      Acc<T> ctx = new Acc<T>() {
        @Override public T malloc( int len ) { return acc.malloc(len); }
        @Override public  int   compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
        @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
        @Override public void copy     ( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
        @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
      };
      merge(
        a,a0,aLen,
        b,b0,bLen,
        c,c0, ctx
      );
    }

    @Override public                             <T> void merge(T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0, Comparator<? super T> cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrObj<T>) (u,i, v,j) -> cmp.compare(u[i],v[j]) ); }
    @Override public<T extends Comparable<? super T>>void merge(T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0                           ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrObj<T>) (u,i, v,j) ->   u[i].compareTo(v[j]) ); }

    @Override public void merge(  byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0, ComparatorByte cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrByte  ) (u, i, v, j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge(  byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrByte  ) (u,i, v,j) ->     Byte.compare(u[i],v[j]) ); }
    @Override public void merge( short[] a, int a0, int aLen, short[] b, int b0, int bLen, short[] c, int c0, ComparatorShort  cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrShort ) (u,i, v,j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge( short[] a, int a0, int aLen, short[] b, int b0, int bLen, short[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrShort ) (u,i, v,j) ->    Short.compare(u[i],v[j]) ); }
    @Override public void merge(   int[] a, int a0, int aLen,   int[] b, int b0, int bLen,   int[] c, int c0, ComparatorInt cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrInt   ) (u, i, v, j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge(   int[] a, int a0, int aLen,   int[] b, int b0, int bLen,   int[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrInt   ) (u,i, v,j) ->  Integer.compare(u[i],v[j]) ); }
    @Override public void merge(  long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0, ComparatorLong   cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrLong  ) (u,i, v,j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge(  long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrLong  ) (u,i, v,j) ->     Long.compare(u[i],v[j]) ); }
    @Override public void merge(  char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0, ComparatorChar cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrChar  ) (u, i, v, j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge(  char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrChar  ) (u,i, v,j) ->Character.compare(u[i],v[j]) ); }
    @Override public void merge( float[] a, int a0, int aLen, float[] b, int b0, int bLen, float[] c, int c0, ComparatorFloat cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrFloat ) (u, i, v, j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge( float[] a, int a0, int aLen, float[] b, int b0, int bLen, float[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrFloat ) (u,i, v,j) ->    Float.compare(u[i],v[j]) ); }
    @Override public void merge(double[] a, int a0, int aLen,double[] b, int b0, int bLen,double[] c, int c0, ComparatorDouble cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrDouble) (u, i, v, j) ->      cmp.compare(u[i],v[j]) ); }
    @Override public void merge(double[] a, int a0, int aLen,double[] b, int b0, int bLen,double[] c, int c0                      ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccArrDouble) (u,i, v,j) ->   Double.compare(u[i],v[j]) ); }

    @Override public void merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen,   IntBuffer c, int c0, ComparatorInt        cmp) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccBufInt) (u,i, v,j) ->      cmp.compare(u.get(i),v.get(j)) ); }
    @Override public void merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen,   IntBuffer c, int c0                          ) { merge(a,a0,aLen, b,b0,bLen, c,c0, (AccBufInt) (u,i, v,j) ->  Integer.compare(u.get(i),v.get(j)) ); }
  }

  public static final Merger PARALLEL_SKIP_MERGER = new ParallelSkipMerger( ForkJoinPool.commonPool() );

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void merge( T a, int a0, int aLen, T b, int b0, int bLen, T c, int c0, CompareRandomAccessor<T> acc ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, acc); }

  public static                             <T> void merge(T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0, Comparator<? super T>cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static<T extends Comparable<? super T>>void merge(T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0                          ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }

  public static void merge(      byte[] a, int a0, int aLen,      byte[] b, int b0, int bLen,      byte[] c, int c0, ComparatorByte   cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(      byte[] a, int a0, int aLen,      byte[] b, int b0, int bLen,      byte[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(     short[] a, int a0, int aLen,     short[] b, int b0, int bLen,     short[] c, int c0, ComparatorShort  cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(     short[] a, int a0, int aLen,     short[] b, int b0, int bLen,     short[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(       int[] a, int a0, int aLen,       int[] b, int b0, int bLen,       int[] c, int c0, ComparatorInt    cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(       int[] a, int a0, int aLen,       int[] b, int b0, int bLen,       int[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(      long[] a, int a0, int aLen,      long[] b, int b0, int bLen,      long[] c, int c0, ComparatorLong   cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(      long[] a, int a0, int aLen,      long[] b, int b0, int bLen,      long[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(      char[] a, int a0, int aLen,      char[] b, int b0, int bLen,      char[] c, int c0, ComparatorChar   cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(      char[] a, int a0, int aLen,      char[] b, int b0, int bLen,      char[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(     float[] a, int a0, int aLen,     float[] b, int b0, int bLen,     float[] c, int c0, ComparatorFloat  cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(     float[] a, int a0, int aLen,     float[] b, int b0, int bLen,     float[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(    double[] a, int a0, int aLen,    double[] b, int b0, int bLen,    double[] c, int c0, ComparatorDouble cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(    double[] a, int a0, int aLen,    double[] b, int b0, int bLen,    double[] c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }
  public static void merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen,   IntBuffer c, int c0, ComparatorInt    cmp) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  public static void merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen,   IntBuffer c, int c0                      ) { PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, c,c0     ); }

  public static       byte[] merge(      byte[] a, int a0, int aLen,      byte[] b, int b0, int bLen, ComparatorByte   cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static       byte[] merge(      byte[] a, int a0, int aLen,      byte[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static      short[] merge(     short[] a, int a0, int aLen,     short[] b, int b0, int bLen, ComparatorShort  cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static      short[] merge(     short[] a, int a0, int aLen,     short[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static        int[] merge(       int[] a, int a0, int aLen,       int[] b, int b0, int bLen, ComparatorInt    cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static        int[] merge(       int[] a, int a0, int aLen,       int[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static       long[] merge(      long[] a, int a0, int aLen,      long[] b, int b0, int bLen, ComparatorLong   cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static       long[] merge(      long[] a, int a0, int aLen,      long[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static       char[] merge(      char[] a, int a0, int aLen,      char[] b, int b0, int bLen, ComparatorChar   cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static       char[] merge(      char[] a, int a0, int aLen,      char[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static      float[] merge(     float[] a, int a0, int aLen,     float[] b, int b0, int bLen, ComparatorFloat  cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static      float[] merge(     float[] a, int a0, int aLen,     float[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static     double[] merge(    double[] a, int a0, int aLen,    double[] b, int b0, int bLen, ComparatorDouble cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static     double[] merge(    double[] a, int a0, int aLen,    double[] b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }
  public static    IntBuffer merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen, ComparatorInt    cmp) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen, cmp); }
  public static    IntBuffer merge(   IntBuffer a, int a0, int aLen,   IntBuffer b, int b0, int bLen                      ) { return PARALLEL_SKIP_MERGER.merge(a,a0,aLen, b,b0,bLen     ); }

  public static       byte[] merge(      byte[] a,    byte[] b, ComparatorByte   cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static       byte[] merge(      byte[] a,    byte[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static      short[] merge(     short[] a,   short[] b, ComparatorShort  cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static      short[] merge(     short[] a,   short[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static        int[] merge(       int[] a,     int[] b, ComparatorInt    cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static        int[] merge(       int[] a,     int[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static       long[] merge(      long[] a,    long[] b, ComparatorLong   cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static       long[] merge(      long[] a,    long[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static       char[] merge(      char[] a,    char[] b, ComparatorChar   cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static       char[] merge(      char[] a,    char[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static      float[] merge(     float[] a,   float[] b, ComparatorFloat  cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static      float[] merge(     float[] a,   float[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static     double[] merge(    double[] a,  double[] b, ComparatorDouble cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static     double[] merge(    double[] a,  double[] b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }
  public static    IntBuffer merge(   IntBuffer a, IntBuffer b, ComparatorInt    cmp) { return PARALLEL_SKIP_MERGER.merge(a,b,cmp); }
  public static    IntBuffer merge(   IntBuffer a, IntBuffer b                      ) { return PARALLEL_SKIP_MERGER.merge(a,b    ); }

// FIELDS

// CONSTRUCTORS
  private ParallelSkipMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
