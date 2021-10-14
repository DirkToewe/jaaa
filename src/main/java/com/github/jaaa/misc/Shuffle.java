package com.github.jaaa.misc;

import com.github.jaaa.Swap;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;

import static com.github.jaaa.misc.CheckArgsMisc.checkArgs_fromUntil;


public class Shuffle
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void shuffle( boolean[] arr                                                 ) { shuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void shuffle( boolean[] arr, int from, int until                            ) { shuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void shuffle( boolean[] arr,                      IntBinaryOperator randInt ) { shuffle(arr,    0,arr.length, randInt); }
  public static void shuffle( boolean[] arr, int from, int until, IntBinaryOperator randInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public IntBinaryOperator shuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

  public static void shuffle( byte[] arr                                                 ) { shuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void shuffle( byte[] arr, int from, int until                            ) { shuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void shuffle( byte[] arr,                      IntBinaryOperator randInt ) { shuffle(arr,    0,arr.length, randInt); }
  public static void shuffle( byte[] arr, int from, int until, IntBinaryOperator randInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public IntBinaryOperator shuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

  public static void shuffle( int[] arr                                                 ) { shuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void shuffle( int[] arr, int from, int until                            ) { shuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void shuffle( int[] arr,                      IntBinaryOperator randInt ) { shuffle(arr,    0,arr.length, randInt); }
  public static void shuffle( int[] arr, int from, int until, IntBinaryOperator randInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public IntBinaryOperator shuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

  public static int[] shuffled( int[] arr ) {
    var     result = arr.clone();
    shuffle(result);
    return  result;
  }
  public static int[] shuffled( int[] arr, IntBinaryOperator randInt ) {
    var     result = arr.clone();
    shuffle(result, randInt);
    return  result;
  }

  public static void shuffle( double[] arr                                                 ) { shuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static void shuffle( double[] arr, int from, int until                            ) { shuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static void shuffle( double[] arr,                      IntBinaryOperator randInt ) { shuffle(arr,    0,arr.length, randInt); }
  public static void shuffle( double[] arr, int from, int until, IntBinaryOperator randInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public IntBinaryOperator shuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

  public static <T> void shuffle( T[] arr                                                 ) { shuffle(arr,    0,arr.length, new SplittableRandom()::nextInt); }
  public static <T> void shuffle( T[] arr, int from, int until                            ) { shuffle(arr, from,until,      new SplittableRandom()::nextInt); }
  public static <T> void shuffle( T[] arr,                      IntBinaryOperator randInt ) { shuffle(arr,    0,arr.length, randInt); }
  public static <T> void shuffle( T[] arr, int from, int until, IntBinaryOperator randInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public IntBinaryOperator shuffle_newRNG() { return randInt; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

// FIELDS

// CONSTRUCTORS

// METHODS
}
