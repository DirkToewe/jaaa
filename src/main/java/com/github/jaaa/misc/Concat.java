package com.github.jaaa.misc;

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
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new byte[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static short[] concat( short[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new short[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static int[] concat( int[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new int[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static long[] concat( long[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new long[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static char[] concat( char[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new char[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static float[] concat( float[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new float[len];

    int off=0;
    for( var arr: arrays ) {
      arraycopy(arr,0, result,off, arr.length);
      off += arr.length;
    }

    return result;
  }

  public static double[] concat( double[]... arrays )
  {
    int len=0;
    for( var arr: arrays )
      len = addExact(len, arr.length);

    var result = new double[len];

    int off=0;
    for( var arr: arrays ) {
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
