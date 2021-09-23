package com.github.jaaa;

import java.util.Comparator;

public class ArgMax
{
// STATIC FIELDS

// STATIC CONSTRUCTORS

  // STATIC METHODS
  public static <C extends Comparable<? super C>> int argMaxL(      C[] arr ) { return ( (ArgMaxAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(boolean[] arr ) { return ( (ArgMaxAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(   byte[] arr ) { return ( (ArgMaxAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(  short[] arr ) { return ( (ArgMaxAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(    int[] arr ) { return ( (ArgMaxAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(   long[] arr ) { return ( (ArgMaxAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(   char[] arr ) { return ( (ArgMaxAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL(  float[] arr ) { return ( (ArgMaxAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static                                   int argMaxL( double[] arr ) { return ( (ArgMaxAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }

  public static <C extends Comparable<? super C>> int argMaxL(      C[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(boolean[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(   byte[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(  short[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(    int[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(   long[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(   char[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL(  float[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static                                   int argMaxL( double[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMaxL(from, until); }

  public static <T> int argMaxL(      T[] arr, Comparator<? super T> cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(boolean[] arr, ComparatorBoolean     cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(   byte[] arr, ComparatorByte        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(  short[] arr, ComparatorShort       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(    int[] arr, ComparatorInt         cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(   long[] arr, ComparatorLong        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(   char[] arr, ComparatorChar        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL(  float[] arr, ComparatorFloat       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }
  public static     int argMaxL( double[] arr, ComparatorDouble      cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(0,arr.length); }

  public static <T> int argMaxL(      T[] arr, int from, int until, Comparator<? super T> cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(boolean[] arr, int from, int until, ComparatorBoolean     cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(   byte[] arr, int from, int until, ComparatorByte        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(  short[] arr, int from, int until, ComparatorShort       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(    int[] arr, int from, int until, ComparatorInt         cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(   long[] arr, int from, int until, ComparatorLong        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(   char[] arr, int from, int until, ComparatorChar        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL(  float[] arr, int from, int until, ComparatorFloat       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }
  public static     int argMaxL( double[] arr, int from, int until, ComparatorDouble      cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxL(from, until); }


  public static <C extends Comparable<? super C>> int argMaxR(      C[] arr ) { return ( (ArgMaxAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(boolean[] arr ) { return ( (ArgMaxAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(   byte[] arr ) { return ( (ArgMaxAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(  short[] arr ) { return ( (ArgMaxAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(    int[] arr ) { return ( (ArgMaxAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(   long[] arr ) { return ( (ArgMaxAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(   char[] arr ) { return ( (ArgMaxAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR(  float[] arr ) { return ( (ArgMaxAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static                                   int argMaxR( double[] arr ) { return ( (ArgMaxAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }

  public static <C extends Comparable<? super C>> int argMaxR(      C[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(boolean[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(   byte[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(  short[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(    int[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(   long[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(   char[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR(  float[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static                                   int argMaxR( double[] arr, int from, int until ) { return ( (ArgMaxAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMaxR(from, until); }

  public static <T> int argMaxR(      T[] arr, Comparator<? super T> cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(boolean[] arr, ComparatorBoolean     cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(   byte[] arr, ComparatorByte        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(  short[] arr, ComparatorShort       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(    int[] arr, ComparatorInt         cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(   long[] arr, ComparatorLong        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(   char[] arr, ComparatorChar        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR(  float[] arr, ComparatorFloat       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }
  public static     int argMaxR( double[] arr, ComparatorDouble      cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(0,arr.length); }

  public static <T> int argMaxR(      T[] arr, int from, int until, Comparator<? super T> cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(boolean[] arr, int from, int until, ComparatorBoolean     cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(   byte[] arr, int from, int until, ComparatorByte        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(  short[] arr, int from, int until, ComparatorShort       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(    int[] arr, int from, int until, ComparatorInt         cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(   long[] arr, int from, int until, ComparatorLong        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(   char[] arr, int from, int until, ComparatorChar        cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR(  float[] arr, int from, int until, ComparatorFloat       cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }
  public static     int argMaxR( double[] arr, int from, int until, ComparatorDouble      cmp ) { return ( (ArgMaxAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMaxR(from, until); }

// FIELDS

// CONSTRUCTORS

  // METHODS
  private ArgMax() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
