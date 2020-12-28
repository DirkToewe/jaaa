package com.github.jaaa;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

public abstract class With<T>
{
  private final T data;

  public With(T _data ) {
    data =_data;

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
      else if( c instanceof Collection   ) return ((Collection<?>) c).size();
      else                                 return Array.getLength(c);
  }
}
