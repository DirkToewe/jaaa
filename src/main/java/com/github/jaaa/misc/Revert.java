package com.github.jaaa.misc;

import static com.github.jaaa.Swap.swap;

public class Revert
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void revert(      T[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(   byte[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(  short[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(    int[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(   long[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(   char[] arr ) { revert(arr,0,arr.length); }
  public static     void revert(  float[] arr ) { revert(arr,0,arr.length); }
  public static     void revert( double[] arr ) { revert(arr,0,arr.length); }

  public static <T> void revert(      T[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(   byte[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(  short[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(    int[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(   long[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(   char[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert(  float[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }
  public static     void revert( double[] arr, int from, int until ) { ( (RevertAccess) (i, j) -> swap(arr,i,j) ).revert(from,until); }

// FIELDS

// CONSTRUCTORS
  private Revert() {}

// METHODS
}
