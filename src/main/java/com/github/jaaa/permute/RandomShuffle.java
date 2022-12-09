package com.github.jaaa.permute;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;


public class RandomShuffle
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void randomShuffle( boolean[] arr                                                 ) { randomShuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void randomShuffle( boolean[] arr, int from, int until                            ) { randomShuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void randomShuffle( boolean[] arr,                      IntBinaryOperator randInt ) { randomShuffle(arr,    0,arr.length, randInt); }
  public static void randomShuffle( boolean[] arr, int from, int until, IntBinaryOperator randInt )
  {
    if( from < 0 || from > until || until > arr.length)
      throw new IllegalArgumentException();
    new RandomShuffleAccess() {
      @Override public IntBinaryOperator randomShuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.randomShuffle(from,until);
  }

  public static void randomShuffle( byte[] arr                                                 ) { randomShuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void randomShuffle( byte[] arr, int from, int until                            ) { randomShuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void randomShuffle( byte[] arr,                      IntBinaryOperator randInt ) { randomShuffle(arr,    0,arr.length, randInt); }
  public static void randomShuffle( byte[] arr, int from, int until, IntBinaryOperator randInt )
  {
    if( from < 0 || from > until || until > arr.length)
      throw new IllegalArgumentException();
    new RandomShuffleAccess() {
      @Override public IntBinaryOperator randomShuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.randomShuffle(from,until);
  }

  public static void randomShuffle( int[] arr                                                 ) { randomShuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void randomShuffle( int[] arr, int from, int until                            ) { randomShuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void randomShuffle( int[] arr,                      IntBinaryOperator randInt ) { randomShuffle(arr,    0,arr.length, randInt); }
  public static void randomShuffle( int[] arr, int from, int until, IntBinaryOperator randInt )
  {
    if( from < 0 || from > until || until > arr.length)
      throw new IllegalArgumentException();
    new RandomShuffleAccess() {
      @Override public IntBinaryOperator randomShuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.randomShuffle(from,until);
  }

  public static int[] shuffled( int[] arr ) {
    var           result = arr.clone();
    randomShuffle(result);
    return        result;
  }
  public static int[] shuffled( int[] arr, IntBinaryOperator randInt ) {
    var           result = arr.clone();
    randomShuffle(result, randInt);
    return        result;
  }

  public static void randomShuffle( double[] arr                                                 ) { randomShuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void randomShuffle( double[] arr, int from, int until                            ) { randomShuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void randomShuffle( double[] arr,                      IntBinaryOperator randInt ) { randomShuffle(arr,    0,arr.length, randInt); }
  public static void randomShuffle( double[] arr, int from, int until, IntBinaryOperator randInt )
  {
    if( from < 0 || from > until || until > arr.length)
      throw new IllegalArgumentException();
    new RandomShuffleAccess() {
      @Override public IntBinaryOperator randomShuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.randomShuffle(from,until);
  }

  public static <T> void randomShuffle( T[] arr                                                 ) { randomShuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static <T> void randomShuffle( T[] arr, int from, int until                            ) { randomShuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static <T> void randomShuffle( T[] arr,                      IntBinaryOperator randInt ) { randomShuffle(arr,    0,arr.length, randInt); }
  public static <T> void randomShuffle( T[] arr, int from, int until, IntBinaryOperator randInt )
  {
    if( from < 0 || from > until || until > arr.length)
      throw new IllegalArgumentException();
    new RandomShuffleAccess() {
      @Override public IntBinaryOperator randomShuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.randomShuffle(from,until);
  }

// FIELDS

// CONSTRUCTORS

// METHODS
}
