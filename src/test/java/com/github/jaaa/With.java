package com.github.jaaa;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static java.util.Collections.unmodifiableCollection;


public interface With<T>
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  static String toString( Object data ) {
    if( data instanceof boolean[] dat ) return Arrays.toString(dat);
    if( data instanceof    byte[] dat ) return Arrays.toString(dat);
    if( data instanceof   short[] dat ) return Arrays.toString(dat);
    if( data instanceof     int[] dat ) return Arrays.toString(dat);
    if( data instanceof    long[] dat ) return Arrays.toString(dat);
    if( data instanceof    char[] dat ) return Arrays.toString(dat);
    if( data instanceof   float[] dat ) return Arrays.toString(dat);
    if( data instanceof  double[] dat ) return Arrays.toString(dat);
    if( data instanceof  Object[] dat ) return Arrays.deepToString(dat);
    return data.toString();
  }
  @SuppressWarnings("unchecked")
  static <T> T clone( T data ) {
    if( data instanceof boolean[] dat ) return (T)  dat.clone();
    if( data instanceof    byte[] dat ) return (T)  dat.clone();
    if( data instanceof   short[] dat ) return (T)  dat.clone();
    if( data instanceof     int[] dat ) return (T)  dat.clone();
    if( data instanceof    long[] dat ) return (T)  dat.clone();
    if( data instanceof    char[] dat ) return (T)  dat.clone();
    if( data instanceof   float[] dat ) return (T)  dat.clone();
    if( data instanceof  double[] dat ) return (T)  dat.clone();
    if( data instanceof  Object[] dat ) return (T)  dat.clone();
    if( data instanceof     With with ) return (T) with.clone();
    throw new IllegalArgumentException();
  }
  static int contentLength( Object data ) {
    if( data instanceof With with ) return with.contentLength();
    if( data.getClass().isArray() ) return Array.getLength(data);
    throw new AssertionError();
  }

// FIELDS

// CONSTRUCTORS

// METHODS
  T getData();

  default int contentLength() {
    T data = getData();
    if( data instanceof      With with ) return with.contentLength();
    if( data.getClass().isArray()      ) return Array.getLength(data);
    if( data instanceof Collection col ) return col.size();
    throw new AssertionError();
  }

  <R> With<R> map( Function<? super T,? extends R> mapper );

  With<T> clone();
}
