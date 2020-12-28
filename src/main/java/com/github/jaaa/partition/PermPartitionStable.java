package com.github.jaaa.partition;

import com.github.jaaa.Swap;

import java.util.function.ToIntFunction;

public class PermPartitionStable
{
  public static <T> void partition( T[] arr, int nKeys, ToIntFunction<? super T> keyFn )
  {
    new PermPartitionStableAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public int  key( int i ) { return keyFn.applyAsInt(arr[i]); }
      @Override public int nKeys() { return nKeys; }
    }.permPartitionStable(0,arr.length);
  }

  public static void partition( int from, int until, PartitionAccess acc )
  {
    new PermPartitionStableAccess() {
      @Override public void swap( int i, int j ) { acc.swap(i,j); }
      @Override public int  key( int i ) { return acc.key(i); }
      @Override public int nKeys() { return acc.nKeys(); }
    }.permPartitionStable(from,until);
  }
}
