package com.github.jaaa.merge;

import com.github.jaaa.compare.CompareRandomAccessor;
import net.jqwik.api.Group;

public class ExpMergeV4AccessorTest extends MergeAccessorTestTemplate
{
  @Override public int maxArraySize() { return 32*1024; }

  @Override protected boolean isStable() { return true; }
  @Override protected boolean mergesInplaceL2R() { return true; }
  @Override protected boolean mergesInplaceR2L() { return true; }

  private static final class HLAccessor<T> implements ExpMergeV4Accessor<T>, MergeAccessor<T>
  {
    private final CompareRandomAccessor<T> acc;

    public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

    @Override public T malloc( int len ) { return acc.malloc(len); }
    @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
    @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
    @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { expMergeV4(a,i,m, b,j,n, c,k); }
  }

  @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<>(srtAcc); }



  @Group
  class L2R extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize() { return 32*1024; }

    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return true; }
    @Override protected boolean mergesInplaceR2L() { return false; }

    private final class HLAccessor<T> implements ExpMergeV4Accessor<T>, MergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public T malloc( int len ) { return acc.malloc(len); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { expMergeV4_L2R(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<>(srtAcc); }
  }



  @Group
  class R2L extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize() { return 32*1024; }

    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return false; }
    @Override protected boolean mergesInplaceR2L() { return true; }

    private final class HLAccessor<T> implements ExpMergeV4Accessor<T>, MergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public T malloc( int len ) { return acc.malloc(len); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { expMergeV4_R2L(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<>(srtAcc); }
  }
}
