package com.github.jaaa.merge;

import com.github.jaaa.compare.*;

import static java.lang.Math.addExact;

import java.nio.IntBuffer;
import java.util.Comparator;


// I am fully aware that the dictionary definition of Merger is the process/state of things being merged, not something
// that merges something... but Mergerer just sounds annoying and is awful to type.

public interface Merger
{
  boolean isStable();

  <T> void merge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, CompareRandomAccessor<T> acc
  );

                               <T>  void merge( T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0, Comparator<? super T> cmp );
  <T extends Comparable<? super T>> void merge( T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0 );

  default byte[] merge( byte[] a,                    byte[] b                                                          ) { var c = new byte[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default byte[] merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen                                        ) { var c = new byte[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default byte[] merge( byte[] a,                    byte[] b,                                      ComparatorByte cmp ) { var c = new byte[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default byte[] merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,                    ComparatorByte cmp ) { var c = new byte[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void           merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0, ComparatorByte cmp );
  void           merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0 );

  default short[] merge( short[] a,                    short[] b                                                            ) { var c = new short[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default short[] merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen                                          ) { var c = new short[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default short[] merge( short[] a,                    short[] b,                                       ComparatorShort cmp ) { var c = new short[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default short[] merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,                     ComparatorShort cmp ) { var c = new short[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void            merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,  short[] c, int c0, ComparatorShort cmp );
  void            merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,  short[] c, int c0 );

  default int[] merge( int[] a,                    int[] b                                                        ) { var c = new int[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default int[] merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen                                      ) { var c = new int[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default int[] merge( int[] a,                    int[] b,                                     ComparatorInt cmp ) { var c = new int[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default int[] merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,                   ComparatorInt cmp ) { var c = new int[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void          merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,  int[] c, int c0, ComparatorInt cmp );
  void          merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,  int[] c, int c0 );

  default long[] merge( long[] a,                    long[] b                                                          ) { var c = new long[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default long[] merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen                                        ) { var c = new long[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default long[] merge( long[] a,                    long[] b,                                      ComparatorLong cmp ) { var c = new long[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default long[] merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,                    ComparatorLong cmp ) { var c = new long[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void           merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0, ComparatorLong cmp );
  void           merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0 );

  default char[] merge( char[] a,                    char[] b                                                          ) { var c = new char[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default char[] merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen                                        ) { var c = new char[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default char[] merge( char[] a,                    char[] b,                                      ComparatorChar cmp ) { var c = new char[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default char[] merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,                    ComparatorChar cmp ) { var c = new char[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void           merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0, ComparatorChar cmp );
  void           merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0 );

  default float[] merge( float[] a,                    float[] b                                                            ) { var c = new float[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default float[] merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen                                          ) { var c = new float[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default float[] merge( float[] a,                    float[] b,                                       ComparatorFloat cmp ) { var c = new float[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default float[] merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,                     ComparatorFloat cmp ) { var c = new float[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void            merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,  float[] c, int c0, ComparatorFloat cmp );
  void            merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,  float[] c, int c0 );

  default double[] merge( double[] a,                    double[] b                                                              ) { var c = new double[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0     ); return c; }
  default double[] merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen                                            ) { var c = new double[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default double[] merge( double[] a,                    double[] b,                                        ComparatorDouble cmp ) { var c = new double[addExact(a.length,b.length)]; merge(a,0,a.length, b,0,b.length, c,0, cmp); return c; }
  default double[] merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,                      ComparatorDouble cmp ) { var c = new double[addExact(aLen,    bLen    )]; merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  void             merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,  double[] c, int c0, ComparatorDouble cmp );
  void             merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,  double[] c, int c0 );

  void              merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,  IntBuffer c, int c0, ComparatorInt cmp );
  void              merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,  IntBuffer c, int c0 );
  default IntBuffer merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen                                          ) { var c = IntBuffer.allocate( addExact(aLen,    bLen    ) ); merge(a,a0,aLen,    b,b0,bLen,    c,0     ); return c; }
  default IntBuffer merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,                       ComparatorInt cmp ) { var c = IntBuffer.allocate( addExact(aLen,    bLen    ) ); merge(a,a0,aLen,    b,b0,bLen,    c,0, cmp); return c; }
  default IntBuffer merge( IntBuffer a,                    IntBuffer b )
  {
    int aPos = a.position(), aLim = a.limit(),
        bPos = b.position(), bLim = b.limit();
    var c = IntBuffer.allocate( addExact(aLim-aPos, bLim-bPos) );
    merge(
      a,aPos,aLim,
      b,bPos,bLim,
      c,0
    );
    return c;
  }
  default IntBuffer merge( IntBuffer a, IntBuffer b, ComparatorInt cmp )
  {
    int aPos = a.position(), aLim = a.limit(),
        bPos = b.position(), bLim = b.limit();
    var c = IntBuffer.allocate( addExact(aLim-aPos, bLim-bPos) );
    merge(
      a,aPos,aLim,
      b,bPos,bLim,
      c,0, cmp
    );
    return c;
  }
}
