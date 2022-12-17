package com.github.jaaa;

public final class Boxing
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static Boolean[] boxed( boolean[] input ) {
    int len = input.length;
    Boolean[] boxed = new Boolean[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Byte[] boxed( byte[] input ) {
    int len = input.length;
    Byte[] boxed = new Byte[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Short[] boxed( short[] input ) {
    int len = input.length;
    Short[] boxed = new Short[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Integer[] boxed( int[] input ) {
    int len = input.length;
    Integer[] boxed = new Integer[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Long[] boxed( long[] input ) {
    int len = input.length;
    Long[] boxed = new Long[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Character[] boxed( char[] input ) {
    int len = input.length;
    Character[] boxed = new Character[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Float[] boxed( float[] input ) {
    int len = input.length;
    Float[] boxed = new Float[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }

  public static Double[] boxed( double[] input ) {
    int len = input.length;
    Double[] boxed = new Double[len];
    for( int i=len; i-- > 0; )
      boxed[i] = input[i];
    return boxed;
  }



  public static boolean[] unboxed( Boolean[] input ) {
    int len = input.length;
    boolean[] unboxed = new boolean[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static byte[] unboxed( Byte[] input ) {
    int len = input.length;
    byte[] unboxed = new byte[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static short[] unboxed( Short[] input ) {
    int len = input.length;
    short[] unboxed = new short[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static int[] unboxed( Integer[] input ) {
    int len = input.length;
    int[] unboxed = new int[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static long[] unboxed( Long[] input ) {
    int len = input.length;
    long[] unboxed = new long[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static char[] unboxed( Character[] input ) {
    int len = input.length;
    char[] unboxed = new char[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static float[] unboxed( Float[] input ) {
    int len = input.length;
    float[] unboxed = new float[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

  public static double[] unboxed( Double[] input ) {
    int len = input.length;
    double[] unboxed = new double[len];
    for( int i=len; i-- > 0; )
      unboxed[i] = input[i];
    return unboxed;
  }

// FIELDS

// CONSTRUCTORS
  private Boxing() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
