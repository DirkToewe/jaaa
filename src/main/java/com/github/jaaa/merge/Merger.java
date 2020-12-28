package com.github.jaaa.merge;

import com.github.jaaa.*;
import com.github.jaaa.CompareRandomAccessor;

import java.util.Comparator;

public interface Merger
{
  public <T> void merge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, CompareRandomAccessor<T> acc
  );

//  public <T extends Comparable<? super T>> T[] merge( T[] a,               T[] b );
//  public <T extends Comparable<? super T>> T[] merge( T[] a, int i, int m, T[] b, int j, int n );
//  public                              <T>  T[] merge( T[] a,               T[] b,                             Comparator<? super T> cmp );
//  public                              <T>  T[] merge( T[] a, int i, int m, T[] b, int j, int n,               Comparator<? super T> cmp );
//  public                              <T>  T[] merge( T[] a, int i, int m, T[] b, int j, int n, T[] c, int k, Comparator<? super T> cmp );
//  public <T extends Comparable<? super T>> T[] merge( T[] a, int i, int m, T[] b, int j, int n, T[] c, int k );
//
//  public   byte[] merge(   byte[] a,                 byte[] b );
//  public   byte[] merge(   byte[] a, int i, int m,   byte[] b, int j, int n );
//  public   byte[] merge(   byte[] a,                 byte[] b,                                   ComparatorByte cmp );
//  public   byte[] merge(   byte[] a, int i, int m,   byte[] b, int j, int n,                     ComparatorByte cmp );
//  public   byte[] merge(   byte[] a, int i, int m,   byte[] b, int j, int n,    byte[] c, int k, ComparatorByte cmp );
//  public   byte[] merge(   byte[] a, int i, int m,   byte[] b, int j, int n,    byte[] c, int k );
//
//  public  short[] merge(  short[] a,                short[] b );
//  public  short[] merge(  short[] a, int i, int m,  short[] b, int j, int n );
//  public  short[] merge(  short[] a,                short[] b,                                   ComparatorShort cmp );
//  public  short[] merge(  short[] a, int i, int m,  short[] b, int j, int n,                     ComparatorShort cmp );
//  public  short[] merge(  short[] a, int i, int m,  short[] b, int j, int n,   short[] c, int k, ComparatorShort cmp );
//  public  short[] merge(  short[] a, int i, int m,  short[] b, int j, int n,   short[] c, int k );
//
//  public    int[] merge(    int[] a,                  int[] b );
//  public    int[] merge(    int[] a, int i, int m,    int[] b, int j, int n );
//  public    int[] merge(    int[] a,                  int[] b,                                  ComparatorInt cmp );
//  public    int[] merge(    int[] a, int i, int m,    int[] b, int j, int n,                    ComparatorInt cmp );
//  public    int[] merge(    int[] a, int i, int m,    int[] b, int j, int n,    int[] c, int k, ComparatorInt cmp );
//  public    int[] merge(    int[] a, int i, int m,    int[] b, int j, int n,    int[] c, int k );
//
//  public   long[] merge(   long[] a,                 long[] b );
//  public   long[] merge(   long[] a, int i, int m,   long[] b, int j, int n );
//  public   long[] merge(   long[] a,                 long[] b,                                  ComparatorInt cmp );
//  public   long[] merge(   long[] a, int i, int m,   long[] b, int j, int n,                    ComparatorInt cmp );
//  public   long[] merge(   long[] a, int i, int m,   long[] b, int j, int n,   long[] c, int k, ComparatorInt cmp );
//  public   long[] merge(   long[] a, int i, int m,   long[] b, int j, int n,   long[] c, int k );
//
//  public   char[] merge(   char[] a,                 char[] b );
//  public   char[] merge(   char[] a, int i, int m,   char[] b, int j, int n );
//  public   char[] merge(   char[] a,                 char[] b,                                  ComparatorChar cmp );
//  public   char[] merge(   char[] a, int i, int m,   char[] b, int j, int n,                    ComparatorChar cmp );
//  public   char[] merge(   char[] a, int i, int m,   char[] b, int j, int n,   char[] c, int k, ComparatorChar cmp );
//  public   char[] merge(   char[] a, int i, int m,   char[] b, int j, int n,   char[] c, int k );
//
//  public  float[] merge(  float[] a,                float[] b );
//  public  float[] merge(  float[] a, int i, int m,  float[] b, int j, int n );
//  public  float[] merge(  float[] a,                float[] b,                                  ComparatorFloat cmp );
//  public  float[] merge(  float[] a, int i, int m,  float[] b, int j, int n,                    ComparatorFloat cmp );
//  public  float[] merge(  float[] a, int i, int m,  float[] b, int j, int n,  float[] c, int k, ComparatorFloat cmp );
//  public  float[] merge(  float[] a, int i, int m,  float[] b, int j, int n,  float[] c, int k );
//
//  public double[] merge( double[] a,               double[] b );
//  public double[] merge( double[] a, int i, int m, double[] b, int j, int n );
//  public double[] merge( double[] a,               double[] b,                                  ComparatorChar cmp );
//  public double[] merge( double[] a, int i, int m, double[] b, int j, int n,                    ComparatorChar cmp );
//  public double[] merge( double[] a, int i, int m, double[] b, int j, int n, double[] c, int k, ComparatorChar cmp );
//  public double[] merge( double[] a, int i, int m, double[] b, int j, int n, double[] c, int k );
}
