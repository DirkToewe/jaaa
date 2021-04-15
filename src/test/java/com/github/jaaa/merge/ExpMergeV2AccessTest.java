package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class ExpMergeV2AccessTest extends MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static class ExpMrgAcc implements ExpMergeV2Access, MergeAccess
  {
    private final CompareSwapAccess acc;
    public ExpMrgAcc( CompareSwapAccess _acc ) { acc =_acc; }
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) { acc.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { expMergeV2(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

  // METHODS
  @Override protected boolean isStable() { return true; }
  @Override protected MergeAccess createAccess( CompareSwapAccess acc ) { return new ExpMrgAcc(acc); }
}
