package com.github.jaaa.misc;

import com.github.jaaa.RandomAccessor;
import com.github.jaaa.Swap;

import java.util.concurrent.ForkJoinPool;

import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class ParallelCopy
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void copyRange( T src, int srcPos, T dst, int dstPos, int len, RandomAccessor<? super T> acc )
  {
    int nPar = ForkJoinPool.getCommonPoolParallelism(),
          h  =              log2Ceil( max(1,len) ),
          h0 = max( 18, h-1-log2Ceil(nPar) ); // <- spwan roughly 4 tasks per cpu thread, but only if copied chunks size don't fall below 2^16

    if( 1==nPar || h <= h0 )
      acc.copyRange(src,srcPos, dst,dstPos, len);
    else
      new ParallelCopyTask<>(h-h0, null, src,srcPos, dst,dstPos, len, acc).invoke();
  }



  public static <T> void arraycopy( T[] src, int srcPos, T[] dst, int dstPos, int length )
  {
    if( srcPos < 0 ) throw new IllegalArgumentException("srcPos must not be negative.");
    if( dstPos < 0 ) throw new IllegalArgumentException("srcPos must not be negative.");
    if( length < 0 ) throw new IllegalArgumentException("length must not be negative.");
    if( srcPos > src.length - length ) throw new IllegalArgumentException("Last src index out of bounds.");
    if( dstPos > dst.length - length ) throw new IllegalArgumentException("Last dst index out of bounds.");

    if( src == dst && abs(srcPos-dstPos) < length )
    {
      throw new IllegalArgumentException("Overlap not yet supported.");
    }
    else
      copyRange(src,srcPos, dst,dstPos, length, new RandomAccessor<T[]>() {
        @Override public int        len( T[] buf ) { return buf.length; }
        @Override public void      swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
        @Override public void copyRange( T[] a, int i, T[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
        @Override public void copy     ( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
      });
  }

// FIELDS

// CONSTRUCTORS
  private ParallelCopy() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
