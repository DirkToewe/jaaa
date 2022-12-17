package com.github.jaaa;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;


public interface With<T>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  static String toString( Object data ) {
    if( data instanceof boolean[] ) return Arrays.toString( (boolean[]) data );
    if( data instanceof    byte[] ) return Arrays.toString( (   byte[]) data );
    if( data instanceof   short[] ) return Arrays.toString( (  short[]) data );
    if( data instanceof     int[] ) return Arrays.toString( (    int[]) data );
    if( data instanceof    long[] ) return Arrays.toString( (   long[]) data );
    if( data instanceof    char[] ) return Arrays.toString( (   char[]) data );
    if( data instanceof   float[] ) return Arrays.toString( (  float[]) data );
    if( data instanceof  double[] ) return Arrays.toString( ( double[]) data );
    if( data instanceof  Object[] ) return Arrays.deepToString( (Object[]) data );
    return data.toString();
  }
  static void checkData( Object data ) {
    if(!( data instanceof boolean[]
       || data instanceof    byte[]
       || data instanceof   short[]
       || data instanceof     int[]
       || data instanceof    long[]
       || data instanceof    char[]
       || data instanceof   float[]
       || data instanceof  double[]
       || data instanceof  Object[]
       || data instanceof     With
     ))
      throw new IllegalArgumentException();
  }
  @SuppressWarnings("unchecked")
  static <T> T clone( T data ) {
    if( data instanceof boolean[] ) return (T)  ( (boolean[]) data ).clone();
    if( data instanceof    byte[] ) return (T)  ( (   byte[]) data ).clone();
    if( data instanceof   short[] ) return (T)  ( (  short[]) data ).clone();
    if( data instanceof     int[] ) return (T)  ( (    int[]) data ).clone();
    if( data instanceof    long[] ) return (T)  ( (   long[]) data ).clone();
    if( data instanceof    char[] ) return (T)  ( (   char[]) data ).clone();
    if( data instanceof   float[] ) return (T)  ( (  float[]) data ).clone();
    if( data instanceof  double[] ) return (T)  ( ( double[]) data ).clone();
    if( data instanceof  Object[] ) return (T)  ( ( Object[]) data ).clone();
    if( data instanceof      With ) return (T)  ( (  With<?>) data ).clone();
    throw new IllegalArgumentException();
  }
  static int contentLength( Object data ) {
    if( data instanceof With ) return ((With<?>) data).contentLength();
    if( data.getClass().isArray() ) return Array.getLength(data);
    throw new AssertionError();
  }

// FIELDS

// CONSTRUCTORS

// METHODS
  T getData();

  default int contentLength() {
    T data = getData();
    if( data instanceof       With ) return ((With<?>) data).contentLength();
    if( data.getClass().isArray()  ) return Array.getLength(data);
    if( data instanceof Collection ) return ((Collection<?>) data).size();
    throw new AssertionError();
  }

  <R> With<R> map( Function<? super T,? extends R> mapper );

  With<T> clone();
}
