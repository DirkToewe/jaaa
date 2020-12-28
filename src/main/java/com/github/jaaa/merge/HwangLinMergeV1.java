package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;

import java.util.Comparator;

import static java.lang.System.arraycopy;

public class HwangLinMergeV1
{
  public static <T> void merge(
    T a, int i, int m,
    T b, int j, int n,
    T c, int k, CompareRandomAccessor<T> acc
  ) {
    new HwangLinMergeV1Accessor<T>() {
      @Override public int        len( T buf ) { return acc.len(buf); }
      @Override public void      copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
      @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    }.hwangLinMergeV1(a,i,m, b,j,n, c,k);
  }

  public static <T> void merge(
    T[] a, int i, int m,
    T[] b, int j, int n,
    T[] c, int k, Comparator<? super T> cmp
  ) {
    new HwangLinMergeV1Accessor<T[]>() {
      @Override public int        len( T[] buf ) { return buf.length; }
      @Override public void      copy( T[] a, int i, T[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copyRange( T[] a, int i, T[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
      @Override public int    compare( T[] a, int i, T[] b, int j ) { return cmp.compare(a[i],b[j]); }
    }.hwangLinMergeV1(a,i,m, b,j,n, c,k);
  }

//  public static <T> void merge(
//    T[] a, int i, int m,
//    T[] b, int j, int n,
//    T[] c, int k, Comparator<? super T> cmp
//  ) {
//    if( i < 0 ) throw new IllegalArgumentException();
//    if( j < 0 ) throw new IllegalArgumentException();
//    if( k < 0 ) throw new IllegalArgumentException();
//    if( m < 0 ) throw new IllegalArgumentException();
//    if( n < 0 ) throw new IllegalArgumentException();
//    if( i > a.length - m ) throw new IllegalArgumentException();
//    if( j > b.length - n ) throw new IllegalArgumentException();
//    if( k > c.length-m-n ) throw new IllegalArgumentException();
//    requireNonNull( a );
//    requireNonNull( b );
//    requireNonNull( c );
//    requireNonNull(cmp);
//
//    // source and destination may only be the same as long as there is no overlap
//    if( c==a && ! (i+m <= k || k+m+n <= i) ) throw new IllegalArgumentException();
//    if( c==b && ! (j+n <= k || k+m+n <= j) ) throw new IllegalArgumentException();
//
//    for( int comp = 1;; )
//    {
//      // make sure that `a` is always shorter
//      if( m > n ) {
//        T[] d=a; a=b; b=d;
//        int l=i; i=j; j=l;
//        int o=m; m=n; n=o; comp ^= 1;
//      }
//
//      if( m <= 0 )
//        break;
//
//      // Step by which to jump/look ahead. The step is carefully designed such
//      // that at most m*log(n) such steps are taken (every m steps without an
//      // element taken from a, about n/2 elements are taken from b).
//      int step = highestOneBit(n/m) - 1;
//
//      T piv = a[i];
//      if( cmp.compare(piv, b[j+step]) < comp )
//      {
//        // find pos. of a[0] via binary search
//        int lo = 0,
//            hi = step-1;
//        while( lo <= hi ) {    int mid = lo+hi >>> 1;
//          if( cmp.compare(piv, b[j+mid]) < comp ) hi = -1 + mid;
//          else                                    lo = +1 + mid;
//        }
//
//        // copy [...b[:lo], a[0]] -> c[:lo+1]
//        m -= 1;
//        n -= lo; arraycopy(b,j, c,k, lo);
//        j += lo;
//        k += lo; c[k++] = a[i++];
//      }
//      else {
//        // otherwise copy step from b to c and proceed
//           ++step;
//        n -= step; arraycopy(b,j, c,k, step);
//        j += step;
//        k += step;
//      }
//    }
//
//    arraycopy(b,j, c,k, n);
//  }
}
