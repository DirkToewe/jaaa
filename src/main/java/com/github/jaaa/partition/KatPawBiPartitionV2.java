package com.github.jaaa.partition;

import com.github.jaaa.*;
import com.github.jaaa.Swap;

import java.util.function.Predicate;

import static java.lang.reflect.Array.getLength;

public class KatPawBiPartitionV2
{
  private static void checkRange( Object arr, int from, int until )
  {
    if( from < 0               ) throw new IllegalArgumentException();
    if( from > until           ) throw new IllegalArgumentException();
    if( until > getLength(arr) ) throw new IllegalArgumentException();
  }

  public static <T> void biPartition( T[] arr, int from, int until, Predicate<? super T> predicate )
  {
    checkRange(arr, from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( int from, int until, PredicateSwapAccess arr )
  {
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { arr.swap(i,j); }
      @Override public boolean predicate( int i ) { return arr.predicate(i); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( byte[] arr, int from, int until, PredicateByte predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( short[] arr, int from, int until, PredicateShort predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( int[] arr, int from, int until, PredicateInt predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( long[] arr, int from, int until, PredicateLong predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( char[] arr, int from, int until, PredicateChar predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( float[] arr, int from, int until, PredicateFloat predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }
  public static void biPartition( double[] arr, int from, int until, PredicateDouble predicate )
  {
    checkRange(arr,from,until);
    new KatPawBiPartitionV2Access() {
      @Override public void swap( int i, int j ) { Swap.swap(arr,i,j); }
      @Override public boolean predicate( int i ) { return predicate.test(arr[i]); }
    }.katPawBiPartitionV2(from,until);
  }

  public static <T> void biPartition(      T[] arr, Predicate<? super T> predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(   byte[] arr, PredicateByte        predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(  short[] arr, PredicateShort       predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(    int[] arr, PredicateInt         predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(   long[] arr, PredicateLong        predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(   char[] arr, PredicateChar        predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition(  float[] arr, PredicateFloat       predicate ) { biPartition(arr, 0,arr.length, predicate); }
  public static     void biPartition( double[] arr, PredicateDouble      predicate ) { biPartition(arr, 0,arr.length, predicate); }
}
