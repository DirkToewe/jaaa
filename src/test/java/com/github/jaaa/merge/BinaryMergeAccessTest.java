package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;


public class BinaryMergeAccessTest implements MergeAccessTestTemplate
{
// STATIC FIELDS
  private static class BinMrgAcc implements BinaryMergeAccess, MergeAccess
  {
    private final CompareSwapAccess acc;
    public BinMrgAcc( CompareSwapAccess _acc ) { acc =_acc; }
    @Override public int compare( int i, int j ) { return acc.compare(i,j); }
    @Override public void   swap( int i, int j ) { acc.swap(i,j); }
    @Override public void  merge( int from, int mid, int until ) { binaryMerge(from,mid,until); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override public int maxArraySize() { return 10_000; }

  @Override public boolean isStable() { return true; }
  @Override public MergeAccess createAccess( CompareSwapAccess acc ) { return new BinMrgAcc(acc); }
}
