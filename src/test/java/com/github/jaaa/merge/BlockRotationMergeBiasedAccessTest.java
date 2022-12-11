package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class BlockRotationMergeBiasedAccessTest implements MergeAccessTestTemplate
{
  private record ExpMrgAcc( CompareSwapAccess acc ) implements BlockRotationMergeBiasedAccess, MergeAccess, TimMergeAccess
  {
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { blockRotationMergeBiased(MIN_GALLOP, from,mid,until); }
    @Override public int blockRotationMergeBiased_localMerge( int bias, int from, int mid, int until ) { return timMergeBiased(bias, from, mid, until); }
  }

  @Override public int maxArraySize() { return 10_000; }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new ExpMrgAcc(acc); }
}
