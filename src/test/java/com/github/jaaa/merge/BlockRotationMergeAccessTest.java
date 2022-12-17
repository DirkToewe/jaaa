package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class BlockRotationMergeAccessTest implements MergeAccessTestTemplate
{
  private static final class Acc implements BlockRotationMergeAccess, MergeAccess
  {
    private final CompareSwapAccess  acc;
    private  Acc( CompareSwapAccess _acc ) { acc = _acc; }
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  }

  @Override public int maxArraySize() { return 10_000; }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new Acc(acc); }
}
