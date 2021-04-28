package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.Group;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.ShrinkingMode;

public class RecMergePartAccessorTest extends MergePartAccessorTestTemplate
{
  @Override public int maxArraySize() { return 32*1024; }

  private static final class BMPAccessor<T> implements MergePartAccessor<T>,
                                                    RecMergePartAccessor<T>
  {
    private final CompareRandomAccessor<T> acc;

    public BMPAccessor( CompareRandomAccessor<T> _acc ) { acc =_acc; }

    @Override public T malloc( int len ) { return acc.malloc(len); }
    @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public void   copy( T a, int i, T b, int j ) { acc.copy(a,i, b,j); }
    @Override public void   swap( T a, int i, T b, int j ) { acc.swap(a,i, b,j); }

    @Override public void mergePartL2R( T a, int a0, int aLen, T b, int b0, int bLen, T c, int c0, int cLen ) {
      recMergePartL2R(
        a,a0,aLen,
        b,b0,bLen,
        c,c0,cLen
      );
    }

    @Override public void mergePartR2L( T a, int a0, int aLen, T b, int b0, int bLen, T c, int c0, int cLen ) {
      recMergePartR2L(
        a,a0,aLen,
        b,b0,bLen,
        c,c0,cLen
      );
    }
  }

  @Override protected boolean isStable() { return true; }

  @Override protected <T> MergePartAccessor<T> createAccessor( CompareRandomAccessor<T> randAcc ) {
    return new BMPAccessor<>(randAcc);
  }
}
