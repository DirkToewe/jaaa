package com.github.jaaa.compare;

import java.util.Comparator;

public class ArgMin
{
// STATIC FIELDS

// STATIC CONSTRUCTORS

// STATIC METHODS
  public static <C extends Comparable<? super C>> int argMinL(      C[] arr ) { return ( (ArgMinAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(boolean[] arr ) { return ( (ArgMinAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(   byte[] arr ) { return ( (ArgMinAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(  short[] arr ) { return ( (ArgMinAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(    int[] arr ) { return ( (ArgMinAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(   long[] arr ) { return ( (ArgMinAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(   char[] arr ) { return ( (ArgMinAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL(  float[] arr ) { return ( (ArgMinAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static                                   int argMinL( double[] arr ) { return ( (ArgMinAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }

  public static <C extends Comparable<? super C>> int argMinL(      C[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(boolean[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(   byte[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(  short[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(    int[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(   long[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(   char[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL(  float[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static                                   int argMinL( double[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMinL(from, until); }

  public static <T> int argMinL(      T[] arr, Comparator<? super T> cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(boolean[] arr, ComparatorBoolean     cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(   byte[] arr, ComparatorByte        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(  short[] arr, ComparatorShort       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(    int[] arr, ComparatorInt         cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(   long[] arr, ComparatorLong        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(   char[] arr, ComparatorChar        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL(  float[] arr, ComparatorFloat       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }
  public static     int argMinL( double[] arr, ComparatorDouble      cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(0,arr.length); }

  public static <T> int argMinL(      T[] arr, int from, int until, Comparator<? super T> cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(boolean[] arr, int from, int until, ComparatorBoolean     cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(   byte[] arr, int from, int until, ComparatorByte        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(  short[] arr, int from, int until, ComparatorShort       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(    int[] arr, int from, int until, ComparatorInt         cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(   long[] arr, int from, int until, ComparatorLong        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(   char[] arr, int from, int until, ComparatorChar        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL(  float[] arr, int from, int until, ComparatorFloat       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }
  public static     int argMinL( double[] arr, int from, int until, ComparatorDouble      cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinL(from, until); }


  public static <C extends Comparable<? super C>> int argMinR(      C[] arr ) { return ( (ArgMinAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(boolean[] arr ) { return ( (ArgMinAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(   byte[] arr ) { return ( (ArgMinAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(  short[] arr ) { return ( (ArgMinAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(    int[] arr ) { return ( (ArgMinAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(   long[] arr ) { return ( (ArgMinAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(   char[] arr ) { return ( (ArgMinAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR(  float[] arr ) { return ( (ArgMinAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static                                   int argMinR( double[] arr ) { return ( (ArgMinAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }

  public static <C extends Comparable<? super C>> int argMinR(      C[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->         arr[i].compareTo(arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(boolean[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->   Boolean.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(   byte[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->      Byte.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(  short[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->     Short.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(    int[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->   Integer.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(   long[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->      Long.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(   char[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) -> Character.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR(  float[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->     Float.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static                                   int argMinR( double[] arr, int from, int until ) { return ( (ArgMinAccess) (i,j) ->    Double.compare(arr[i],arr[j]) ).argMinR(from, until); }

  public static <T> int argMinR(      T[] arr, Comparator<? super T> cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(boolean[] arr, ComparatorBoolean     cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(   byte[] arr, ComparatorByte        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(  short[] arr, ComparatorShort       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(    int[] arr, ComparatorInt         cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(   long[] arr, ComparatorLong        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(   char[] arr, ComparatorChar        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR(  float[] arr, ComparatorFloat       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }
  public static     int argMinR( double[] arr, ComparatorDouble      cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(0,arr.length); }

  public static <T> int argMinR(      T[] arr, int from, int until, Comparator<? super T> cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(boolean[] arr, int from, int until, ComparatorBoolean     cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(   byte[] arr, int from, int until, ComparatorByte        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(  short[] arr, int from, int until, ComparatorShort       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(    int[] arr, int from, int until, ComparatorInt         cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(   long[] arr, int from, int until, ComparatorLong        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(   char[] arr, int from, int until, ComparatorChar        cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR(  float[] arr, int from, int until, ComparatorFloat       cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }
  public static     int argMinR( double[] arr, int from, int until, ComparatorDouble      cmp ) { return ( (ArgMinAccess) (i,j) -> cmp.compare(arr[i],arr[j]) ).argMinR(from, until); }

// FIELDS

// CONSTRUCTORS

// METHODS
  private ArgMin() {
    throw new UnsupportedOperationException("Cannot instantiate static class.");
  }
}
