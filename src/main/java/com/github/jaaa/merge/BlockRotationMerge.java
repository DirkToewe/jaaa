package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;

import java.util.Comparator;

import static java.lang.System.arraycopy;


public final class BlockRotationMerge
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void merge(
    T a, int i, int m,
    T b, int j, int n,
    T c, int k, CompareRandomAccessor<T> acc
  ) {
    int mid = k+m;
    acc.copyRange(a,i, c,k,   m);
    acc.copyRange(b,j, c,mid, n);
    new BlockRotationMergeAccess() {
      @Override public void   swap( int i, int j ) {        acc.   swap(c,i, c,j); }
      @Override public int compare( int i, int j ) { return acc.compare(c,i, c,j); }
    }.blockRotationMerge(k, mid, mid+n);
  }

  public static <T> void merge(
    T[] a, int i, int m,
    T[] b, int j, int n,
    T[] c, int k, Comparator<? super T> cmp
  ) {
    int mid = k+m;
    arraycopy(a,i, c,k,   m);
    arraycopy(b,j, c,mid, n);
    new BlockRotationMergeAccess() {
      @Override public void   swap( int i, int j ) { Swap.swap(c,i, c,j); }
      @Override public int compare( int i, int j ) { return cmp.compare(c[i], c[j]); }
    }.blockRotationMerge(k, mid, mid+n);
  }

// FIELDS

// CONSTRUCTORS
  private BlockRotationMerge() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
