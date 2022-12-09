package com.github.jaaa.permute;

import static com.github.jaaa.permute.Swap.swap;

public class Rotate
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

  // STATIC METHODS
  public static <T> void rotate(      T[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(boolean[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(   byte[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(  short[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(    int[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(   long[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(   char[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate(  float[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }
  public static     void rotate( double[] arr, int dist ) { rotate(arr, 0,arr.length, dist); }

  public static <T> void rotate(      T[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(boolean[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(   byte[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(  short[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(    int[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(   long[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(   char[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate(  float[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }
  public static     void rotate( double[] arr, int from, int until, int dist ) { ( (RotateAccess) (i, j) -> swap(arr,i,j) ).rotate(from,until,dist); }

// FIELDS

  // CONSTRUCTORS
  private Rotate() {}

// METHODS
}
