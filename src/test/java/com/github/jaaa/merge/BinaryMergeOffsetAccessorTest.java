package com.github.jaaa.merge;

import com.github.jaaa.CompareAccessor;
import static java.util.Objects.requireNonNull;

public class BinaryMergeOffsetAccessorTest extends MergeOffsetAccessorTestTemplate
{
  @Override public int maxArraySize() { return 32*1024; }

  private static class MrgSkpAcc<T> implements MergeOffsetAccessor<T>, BinaryMergeOffsetAccessor<T>
  {
    private final CompareAccessor<T> acc;

    public MrgSkpAcc( CompareAccessor<T> _acc ) { acc = requireNonNull(_acc); }

    @Override public int compare( T a, int i, T b, int j ) { return acc.compare(a,i, b,j); }
    @Override public int mergeOffset(
      T a, int a0, int aLen,
      T b, int b0, int bLen, int nSkip
    ) {
      return binaryMergeOffset(a,a0,aLen, b,b0,bLen, nSkip);
    }
  }

  @Override protected <T> MergeOffsetAccessor<T> createAccessor(CompareAccessor<T> srtAcc ) { return new MrgSkpAcc<>(srtAcc); }
}
