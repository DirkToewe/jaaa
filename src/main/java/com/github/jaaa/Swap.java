package com.github.jaaa;

/** Contains utility methods for swapping pairs of elements in arrays.
 *
 *  @author Dirk Toewe
 */
public class Swap
{
  public static <T> void swap( T[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    T tmp = arr[i];
            arr[i] = arr[j];
                     arr[j] = tmp;
  }

  public static void swap( boolean[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    boolean tmp = arr[i];
                  arr[i] = arr[j];
                           arr[j] = tmp;
  }

  public static void swap( byte[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    byte tmp = arr[i];
               arr[i] = arr[j];
                        arr[j] = tmp;
  }

  public static void swap( short[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    short tmp = arr[i];
                arr[i] = arr[j];
                         arr[j] = tmp;
  }

  public static void swap( int[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    int tmp = arr[i];
              arr[i] = arr[j];
                       arr[j] = tmp;
  }

  public static void swap( long[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    long tmp = arr[i];
               arr[i] = arr[j];
                        arr[j] = tmp;
  }

  public static void swap( char[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    char tmp = arr[i];
               arr[i] = arr[j];
                        arr[j] = tmp;
  }

  public static void swap( float[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    float tmp = arr[i];
                arr[i] = arr[j];
                         arr[j] = tmp;
  }

  public static void swap( double[] arr, int i, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    double tmp = arr[i];
                 arr[i] = arr[j];
                          arr[j] = tmp;
  }



  public static <T> void swap( T[] a, int i, T[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    T tmp = a[i];
            a[i] = b[j];
                   b[j] = tmp;
  }

  public static void swap( boolean[] a, int i, boolean[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    boolean tmp = a[i];
                  a[i] = b[j];
                         b[j] = tmp;
  }

  public static void swap( byte[] a, int i, byte[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    byte tmp = a[i];
               a[i] = b[j];
                      b[j] = tmp;
  }

  public static void swap( short[] a, int i, short[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    short tmp = a[i];
                a[i] = b[j];
                       b[j] = tmp;
  }

  public static void swap( int[] a, int i, int[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    int tmp = a[i];
              a[i] = b[j];
                     b[j] = tmp;
  }

  public static void swap( long[] a, int i, long[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    long tmp = a[i];
               a[i] = b[j];
                      b[j] = tmp;
  }

  public static void swap( char[] a, int i, char[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    char tmp = a[i];
               a[i] = b[j];
                      b[j] = tmp;
  }

  public static void swap( float[] a, int i, float[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    float tmp = a[i];
                a[i] = b[j];
                       b[j] = tmp;
  }

  public static void swap( double[] a, int i, double[] b, int j ) throws IndexOutOfBoundsException, NullPointerException
  {
    double tmp = a[i];
                 a[i] = b[j];
                        b[j] = tmp;
  }
}
