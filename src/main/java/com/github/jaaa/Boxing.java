package com.github.jaaa;

public final class Boxing
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static Boolean[] boxed( boolean[] input ) {
    int len = input.length;
    var boxed = new Boolean[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Byte[] boxed( byte[] input ) {
    int len = input.length;
    var boxed = new Byte[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Short[] boxed( short[] input ) {
    int len = input.length;
    var boxed = new Short[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Integer[] boxed( int[] input ) {
    int len = input.length;
    var boxed = new Integer[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Long[] boxed( long[] input ) {
    int len = input.length;
    var boxed = new Long[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Character[] boxed( char[] input ) {
    int len = input.length;
    var boxed = new Character[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Float[] boxed( float[] input ) {
    int len = input.length;
    var boxed = new Float[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Double[] boxed( double[] input ) {
    int len = input.length;
    var boxed = new Double[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }



  public static boolean[] unboxed( Boolean[] input ) {
    int len = input.length;
    var unboxed = new boolean[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static byte[] unboxed( Byte[] input ) {
    int len = input.length;
    var unboxed = new byte[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static short[] unboxed( Short[] input ) {
    int len = input.length;
    var unboxed = new short[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static int[] unboxed( Integer[] input ) {
    int len = input.length;
    var unboxed = new int[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static long[] unboxed( Long[] input ) {
    int len = input.length;
    var unboxed = new long[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static char[] unboxed( Character[] input ) {
    int len = input.length;
    var unboxed = new char[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static float[] unboxed( Float[] input ) {
    int len = input.length;
    var unboxed = new float[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static double[] unboxed( Double[] input ) {
    int len = input.length;
    var unboxed = new double[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

// FIELDS

// CONSTRUCTORS
  private Boxing() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
