package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;

public class HwangLinMergeAccessorTest extends MergeAccessorTestTemplate
{
  @Override protected boolean isStable() { return true; }
  @Override protected boolean mergesInplaceL2R() { return true; }
  @Override protected boolean mergesInplaceR2L() { return true; }

  private static final class HLAccessor<T> implements HwangLinMergeAccessor<T>, MergeAccessor<T>
  {
    private final CompareRandomAccessor<T> acc;

    public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

    @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public int len( T buf ) { return acc.len(buf); }
    @Override public void copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
    @Override public void swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }

    @Override public void merge( T a, int i, int m, T b, int j, int n, T c, int k ) { hwangLinMergeV1(a,i,m, b,j,n, c,k); }
  }

  @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<T>(srtAcc); }



  @Group
  class L2R extends MergeAccessorTestTemplate
  {
    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return true; }
    @Override protected boolean mergesInplaceR2L() { return false; }

    private final class HLAccessor<T> implements HwangLinMergeAccessor<T>, MergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public int     len( T buf ) { return acc.len(buf); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { hwangLinMergeV1_L2R(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<T>(srtAcc); }
  }



  @Group
  class R2L extends MergeAccessorTestTemplate
  {
    @Override protected boolean isStable() { return true; }
    @Override protected boolean mergesInplaceL2R() { return false; }
    @Override protected boolean mergesInplaceR2L() { return true; }

    private final class HLAccessor<T> implements HwangLinMergeAccessor<T>, MergeAccessor<T>
    {
      private final CompareRandomAccessor<T> acc;

      public HLAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

      @Override public int     len( T buf ) { return acc.len(buf); }
      @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
      @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
      @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }
      @Override public void  merge( T a, int i, int m, T b, int j, int n, T c, int k ) { hwangLinMergeV1_R2L(a,i,m, b,j,n, c,k); }
    }

    @Override protected <T> MergeAccessor<T> createAccessor(CompareRandomAccessor<T> srtAcc) { return new HLAccessor<T>(srtAcc); }
  }
}
