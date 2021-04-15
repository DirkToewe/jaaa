package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;

public class ZenMergeAccessorTest extends MergeAccessorTestTemplate
{
  @Override public int maxArraySize() { return 32*1024; }

  @Override protected boolean isStable() { return true; }
  @Override protected boolean mergesInplaceL2R() { return true; }
  @Override protected boolean mergesInplaceR2L() { return true; }

  private static final class Acc<T> implements MergeAccessor<T>,
                                            ZenMergeAccessor<T>
  {
    private final CompareRandomAccessor<T> acc;

    public Acc(CompareRandomAccessor<T> _acc ) { acc =_acc; }

    @Override public T malloc(int len ) { return acc.malloc(len); }
    @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
    @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
    @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { zenMerge(a,i,m, b,j,n, c,k); }
  }

  @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new Acc(srtAcc); }



  @Group
  class L2R extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize() { return 32*1024; }

    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return true; }
    @Override protected boolean mergesInplaceR2L() { return false; }

    private final class Acc<T> implements MergeAccessor<T>,
                                       ZenMergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public Acc(CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public T malloc(int len ) { return acc.malloc(len); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { zenMergeL2R(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new Acc<T>(srtAcc); }
  }



  @Group
  class R2L extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize() { return 32*1024; }

    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return false; }
    @Override protected boolean mergesInplaceR2L() { return true; }

    private final class Acc<T> implements MergeAccessor<T>,
                                       ZenMergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public Acc(CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public T malloc(int len ) { return acc.malloc(len); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { zenMergeR2L(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new Acc<T>(srtAcc); }
  }
}