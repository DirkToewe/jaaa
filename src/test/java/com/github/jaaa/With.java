package com.github.jaaa;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import static java.util.Collections.unmodifiableCollection;


public abstract class With<T> implements Cloneable
{
  private final T data;

  public With( T _data ) {
         if(_data instanceof  boolean[] ) data = (T) ( (boolean[]) _data ).clone();
    else if(_data instanceof     byte[] ) data = (T) ( (   byte[]) _data ).clone();
    else if(_data instanceof    short[] ) data = (T) ( (  short[]) _data ).clone();
    else if(_data instanceof      int[] ) data = (T) ( (    int[]) _data ).clone();
    else if(_data instanceof     long[] ) data = (T) ( (   long[]) _data ).clone();
    else if(_data instanceof     char[] ) data = (T) ( (   char[]) _data ).clone();
    else if(_data instanceof    float[] ) data = (T) ( (  float[]) _data ).clone();
    else if(_data instanceof   double[] ) data = (T) ( ( double[]) _data ).clone();
    else if(_data instanceof   Object[] ) data = (T) ( ( Object[]) _data ).clone();
    else if(_data instanceof Collection ) data = (T) unmodifiableCollection( (Collection<?>) _data );
    else                                  data = (T) ( (With<T>) _data ).clone();

    if( ! ( _data instanceof With
         || _data instanceof Collection
         || _data.getClass().isArray()) )
      throw new IllegalArgumentException();
  }

  protected String dataString() {
          if( data instanceof  boolean[] ) return Arrays.toString( (boolean[]) data );
     else if( data instanceof     byte[] ) return Arrays.toString( (   byte[]) data );
     else if( data instanceof    short[] ) return Arrays.toString( (  short[]) data );
     else if( data instanceof      int[] ) return Arrays.toString( (    int[]) data );
     else if( data instanceof     long[] ) return Arrays.toString( (   long[]) data );
     else if( data instanceof     char[] ) return Arrays.toString( (   char[]) data );
     else if( data instanceof    float[] ) return Arrays.toString( (  float[]) data );
     else if( data instanceof   double[] ) return Arrays.toString( ( double[]) data );
     else if( data instanceof   Object[] ) return Arrays.toString( ( Object[]) data );
     else
       return data.toString();
  }

  public T getData() { return data; }

  public int contentLength() {
    for( Object c = data;; )
           if( c instanceof With) c = ((With) c).getData();
      else if( c instanceof Collection ) return ((Collection<?>) c).size();
      else                               return Array.getLength(c);
  }

  @Override public abstract With<T> clone();
}
