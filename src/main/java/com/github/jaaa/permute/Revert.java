package com.github.jaaa.permute;

import com.github.jaaa.copy.RandomAccessor;

// TODO: refactor to "reverse"
public class Revert
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS
  public static <T> void revert( T arr, int from, int until, RandomAccessor<? super T> acc )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; from < --until; from++ )
      acc.swap(arr,from, arr,until);
  }

  public static <T> void revert( T[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      T tmp = arr[i];
              arr[i] = arr[j];
                       arr[j] = tmp;
    }
  }
  public static void revert( byte[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      byte tmp = arr[i];
                 arr[i] = arr[j];
                          arr[j] = tmp;
    }
  }
  public static void revert( short[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      short tmp = arr[i];
                  arr[i] = arr[j];
                           arr[j] = tmp;
    }
  }
  public static void revert( int[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      int tmp = arr[i];
                arr[i] = arr[j];
                         arr[j] = tmp;
    }
  }
  public static void revert( long[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      long tmp = arr[i];
                 arr[i] = arr[j];
                          arr[j] = tmp;
    }
  }
  public static void revert( char[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      char tmp = arr[i];
                 arr[i] = arr[j];
                          arr[j] = tmp;
    }
  }
  public static void revert( float[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      float tmp = arr[i];
                  arr[i] = arr[j];
                           arr[j] = tmp;
    }
  }
  public static void revert( double[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      double tmp = arr[i];
                   arr[i] = arr[j];
                            arr[j] = tmp;
    }
  }
  public static void revert( boolean[] arr ) {
    for( int i=0, j=arr.length; --j > i; i++ ) {
      boolean tmp = arr[i];
                    arr[i] = arr[j];
                             arr[j] = tmp;
    }
  }

  public static <T> void revert( T[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      T tmp = arr[from];
              arr[from] = arr[until];
                          arr[until] = tmp;
    }
  }
  public static void revert( byte[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      byte tmp = arr[from];
                arr[from] = arr[until];
                            arr[until] = tmp;
    }
  }
  public static void revert( short[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      short tmp = arr[from];
                  arr[from] = arr[until];
                              arr[until] = tmp;
    }
  }
  public static void revert( int[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      int tmp = arr[from];
                arr[from] = arr[until];
                            arr[until] = tmp;
    }
  }
  public static void revert( long[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      long tmp = arr[from];
                 arr[from] = arr[until];
                             arr[until] = tmp;
    }
  }
  public static void revert( char[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      char tmp = arr[from];
                 arr[from] = arr[until];
                             arr[until] = tmp;
    }
  }
  public static void revert( float[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      float tmp = arr[from];
                  arr[from] = arr[until];
                              arr[until] = tmp;
    }
  }
  public static void revert( double[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      double tmp = arr[from];
                   arr[from] = arr[until];
                               arr[until] = tmp;
    }
  }
  public static void revert( boolean[] arr, int from, int until )
  {
    if( from < 0 || from > until )
      throw new IllegalArgumentException();

    for( ; --until > from; from++ ) {
      boolean tmp = arr[from];
                    arr[from] = arr[until];
                                arr[until] = tmp;
    }
  }

// FIELDS

// CONSTRUCTORS
  private Revert() {}

// METHODS
}
