package com.github.jaaa.misc;

import com.github.jaaa.Swap;

import java.util.Random;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static com.github.jaaa.misc.CheckArgsMisc.checkArgs_fromUntil;


public class Shuffle
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static void shuffle( byte[] arr                                                 ) { shuffle(arr,    0,arr.length, new Random()::nextInt); }
  public static void shuffle( byte[] arr, int from, int until                            ) { shuffle(arr, from,until,      new Random()::nextInt); }
  public static void shuffle( byte[] arr,                      IntUnaryOperator  nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( byte[] arr,                      IntBinaryOperator nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( byte[] arr, int from, int until, IntUnaryOperator  nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(until-from) + from; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }
  public static void shuffle( byte[] arr, int from, int until, IntBinaryOperator nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(from,until); }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

  public static void shuffle( int[] arr                                                 ) { shuffle(arr,    0,arr.length, new Random()::nextInt); }
  public static void shuffle( int[] arr, int from, int until                            ) { shuffle(arr, from,until,      new Random()::nextInt); }
  public static void shuffle( int[] arr,                      IntUnaryOperator  nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( int[] arr,                      IntBinaryOperator nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( int[] arr, int from, int until, IntUnaryOperator  nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(until-from) + from; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }
  public static void shuffle( int[] arr, int from, int until, IntBinaryOperator nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(from,until); }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }
  public static int[] shuffled( int[] arr ) {
    var     result = arr.clone();
    shuffle(result);
    return  result;
  }

  public static void shuffle( double[] arr                                                 ) { shuffle(arr,    0,arr.length, new Random()::nextInt); }
  public static void shuffle( double[] arr, int from, int until                            ) { shuffle(arr, from,until,      new Random()::nextInt); }
  public static void shuffle( double[] arr,                      IntUnaryOperator  nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( double[] arr,                      IntBinaryOperator nextInt ) { shuffle(arr,    0,arr.length, nextInt); }
  public static void shuffle( double[] arr, int from, int until, IntUnaryOperator  nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(until-from) + from; }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }
  public static void shuffle( double[] arr, int from, int until, IntBinaryOperator nextInt )
  {
    checkArgs_fromUntil(from,until, arr.length);
    new ShuffleAccess() {
      @Override public int randInt( int from, int until ) { return nextInt.applyAsInt(from,until); }
      @Override public void swap( int i, int j ) { Swap.swap(arr, i,j); }
    }.shuffle(from,until);
  }

// FIELDS

// CONSTRUCTORS

// METHODS
}
