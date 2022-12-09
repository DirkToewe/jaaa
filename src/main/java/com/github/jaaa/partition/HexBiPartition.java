package com.github.jaaa.partition;

import com.github.jaaa.fn.PredicateByte;
import com.github.jaaa.permute.Swap;

import java.util.function.Predicate;

public class HexBiPartition
{
  public static <T> void biPartition( T[] arr, Predicate<? super T> predicate )
  {
    new HexBiPartitionAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) {
        return predicate.test(arr[i]);
      }
    }.hexBiPartition(0,arr.length);
  }

  public static <T> void biPartition( byte[] arr, PredicateByte predicate )
  {
    new HexBiPartitionAccess() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) {
        return predicate.test(arr[i]);
      }
    }.hexBiPartition(0,arr.length);
  }
}
