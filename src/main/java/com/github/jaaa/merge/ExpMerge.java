package com.github.jaaa.merge;

import com.github.jaaa.permute.Swap;
import com.github.jaaa.compare.CompareRandomAccessor;

import java.util.Comparator;

import static java.lang.System.arraycopy;

public class ExpMerge
{
  public static <T> void merge(
    T a, int i, int m,
    T b, int j, int n,
    T c, int k, CompareRandomAccessor<T> acc
  ) {
    new ExpMergeAccessor<T>() {
      @Override public T malloc( int len ) { return acc.malloc(len); }
      @Override public void      copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void      swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void copyRange( T a, int i, T b, int j, int len ) { acc.copyRange(a,i, b,j, len); }
      @Override public int    compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    }.expMerge(a,i,m, b,j,n, c,k);
  }

  public static <T> void merge(
    T[] a, int i, int m,
    T[] b, int j, int n,
    T[] c, int k, Comparator<? super T> cmp
  ) {
    new ExpMergeAccessor<T[]>() {
      @Override public T[] malloc( int len ) { throw new UnsupportedOperationException(); }
      @Override public void      copy( T[] a, int i, T[] b, int j ) { }
      @Override public void      swap( T[] a, int i, T[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copyRange( T[] a, int i, T[] b, int j, int len ) { arraycopy(a,i, b,j, len); }
      @Override public int    compare( T[] a, int i, T[] b, int j ) { return cmp.compare(a[i],b[j]); }
    }.expMerge(a,i,m, b,j,n, c,k);
  }
}
