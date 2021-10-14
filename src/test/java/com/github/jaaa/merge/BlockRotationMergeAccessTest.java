package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class BlockRotationMergeAccessTest extends MergeAccessTestTemplate
{
  private static record ExpMrgAcc( CompareSwapAccess acc ) implements BlockRotationMergeAccess, MergeAccess
  {
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }
  }

  @Override protected boolean isStable() { return true; }
  @Override protected MergeAccess createAccess( CompareSwapAccess acc ) { return new ExpMrgAcc(acc); }
}
