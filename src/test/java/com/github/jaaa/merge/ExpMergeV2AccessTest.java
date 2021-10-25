package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class ExpMergeV2AccessTest implements MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static record Acc( CompareSwapAccess acc ) implements ExpMergeV2Access, MergeAccess
  {
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) {        acc.   swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { expMergeV2(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

  // METHODS
  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new Acc(acc); }
}
