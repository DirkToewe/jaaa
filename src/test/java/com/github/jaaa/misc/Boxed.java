package com.github.jaaa.misc;

public final class Boxed
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

// FIELDS

// CONSTRUCTORS
  private Boxed() { throw new UnsupportedOperationException("Static class cannot be instantiated."); }

// METHODS
}
