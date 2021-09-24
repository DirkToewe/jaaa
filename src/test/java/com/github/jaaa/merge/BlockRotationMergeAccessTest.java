package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class BlockRotationMergeAccessTest extends MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class ExpMrgAcc implements BlockRotationMergeAccess, MergeAccess, BinaryMergeAccess
  {
    private final CompareSwapAccess acc;
    public ExpMrgAcc( CompareSwapAccess _acc ) { acc =_acc; }
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) { acc.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { blockRotationMerge(from,mid,until); }

    @Override public void blockRotationMerge_localMerge( int from, int mid, int until ) {
      binaryMerge(from,mid,until);
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

  // METHODS
  @Override protected boolean isStable() { return true; }
  @Override protected MergeAccess createAccess( CompareSwapAccess acc ) { return new ExpMrgAcc(acc); }
}
