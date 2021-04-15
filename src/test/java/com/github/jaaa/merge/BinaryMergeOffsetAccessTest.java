package com.github.jaaa.merge;

import com.github.jaaa.CompareAccess;
import static java.util.Objects.requireNonNull;

public class BinaryMergeOffsetAccessTest extends MergeOffsetAccessTestTemplate
{
  @Override public int maxArraySize() { return 32*1024; }

  private static class MrgSkpAcc implements MergeOffsetAccess, BinaryMergeOffsetAccess
  {
    private final CompareAccess acc;

    public MrgSkpAcc( CompareAccess _acc ) { acc = requireNonNull(_acc); }

    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public int mergeOffset(
      int a0, int aLen,
      int b0, int bLen, int nSkip
    ) {
      return binaryMergeOffset(a0,aLen, b0,bLen, nSkip);
    }
  }

  @Override protected MergeOffsetAccess createAccess( CompareAccess srtAcc ) { return new MrgSkpAcc(srtAcc); }
}
