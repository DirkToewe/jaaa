package com.github.jaaa;

import static java.lang.Math.addExact;
import static java.lang.System.arraycopy;


public class Concat
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static byte[] concat( byte[]... arrays )
  {
    int len=0;
    for( byte[] arr: arrays )
      len = addExact(len, arr.length);

    byte[] result = new byte[len];

    int off=0;
    for( byte[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static short[] concat( short[]... arrays )
  {
    int len=0;
    for( short[] arr: arrays )
      len = addExact(len, arr.length);

    short[] result = new short[len];

    int off=0;
    for( short[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static int[] concat( int[]... arrays )
  {
    int len=0;
    for( int[] arr: arrays )
      len = addExact(len, arr.length);

    int[] result = new int[len];

    int off=0;
    for( int[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static long[] concat( long[]... arrays )
  {
    int len=0;
    for( long[] arr: arrays )
      len = addExact(len, arr.length);

    long[] result = new long[len];

    int off=0;
    for( long[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static char[] concat( char[]... arrays )
  {
    int len=0;
    for( char[] arr: arrays )
      len = addExact(len, arr.length);

    char[] result = new char[len];

    int off=0;
    for( char[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static float[] concat( float[]... arrays )
  {
    int len=0;
    for( float[] arr: arrays )
      len = addExact(len, arr.length);

    float[] result = new float[len];

    int off=0;
    for( float[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static double[] concat( double[]... arrays )
  {
    int len=0;
    for( double[] arr: arrays )
      len = addExact(len, arr.length);

    double[] result = new double[len];

    int off=0;
    for( double[] arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

// FIELDS

// CONSTRUCTORS
  private Concat() { throw new UnsupportedOperationException("Static class cannot can be instantiated."); }

// METHODS
}
