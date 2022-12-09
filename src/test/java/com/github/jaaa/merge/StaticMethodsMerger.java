package com.github.jaaa.merge;

import com.github.jaaa.compare.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.IntBuffer;
import java.util.Comparator;

import static java.lang.invoke.MethodType.methodType;


public abstract class StaticMethodsMerger implements Merger
{
// STATIC FIELDS
  private static final class ArrayMergeMethod<A,C>
  {
    private final MethodHandle
      mergeAIIAIIAIC,
      mergeAIIAIIAI,
      mergeAAC,
      mergeAA,
      mergeAIIAIIC,
      mergeAIIAII;

    public ArrayMergeMethod( Class<?> mergeClass, Class<A> arrType, Class<C> cmpType )
    {
      MethodHandles.Lookup lookup = MethodHandles.publicLookup();
      try {
        mergeAIIAIIAIC = lookup.findStatic( mergeClass, "merge", methodType(void.class, arrType, int.class, int.class, arrType, int.class, int.class, arrType, int.class, cmpType) );
        mergeAIIAIIAI  = lookup.findStatic( mergeClass, "merge", methodType(void.class, arrType, int.class, int.class, arrType, int.class, int.class, arrType, int.class         ) );
        mergeAAC       = lookup.findStatic( mergeClass, "merge", methodType(arrType,    arrType,                       arrType,                                           cmpType) );
        mergeAA        = lookup.findStatic( mergeClass, "merge", methodType(arrType,    arrType,                       arrType                                                   ) );
        mergeAIIAIIC   = lookup.findStatic( mergeClass, "merge", methodType(arrType,    arrType, int.class, int.class, arrType, int.class, int.class,                     cmpType) );
        mergeAIIAII    = lookup.findStatic( mergeClass, "merge", methodType(arrType,    arrType, int.class, int.class, arrType, int.class, int.class                             ) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new Error(err);
      }
    }

    public A merge( A a, A b )
    {
      try {
        return (A) mergeAA.invoke(a,b);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public A merge( A a, A b, C cmp )
    {
      try {
        return (A) mergeAAC.invoke(a,b, cmp);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public A merge( A a, int a0, int aLen, A b, int b0, int bLen )
    {
      try {
        return (A) mergeAIIAII.invoke(a,a0,aLen, b,b0,bLen);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public A merge( A a, int a0, int aLen, A b, int b0, int bLen, C cmp )
    {
      try {
        return (A) mergeAIIAIIC.invoke(a,a0,aLen, b,b0,bLen, cmp);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public void merge(
      A a, int a0, int aLen,
      A b, int b0, int bLen,
      A c, int c0
    )
    {
      try {
        mergeAIIAIIAI.invoke(
          a,a0,aLen,
          b,b0,bLen,
          c,c0
        );
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public void merge(
      A a, int a0, int aLen,
      A b, int b0, int bLen,
      A c, int c0, C cmp
    )
    {
      try {
        mergeAIIAIIAIC.invoke(
          a,a0,aLen,
          b,b0,bLen,
          c,c0, cmp
        );
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }
  }

// FIELDS
  private final ArrayMergeMethod<   byte[], ComparatorByte> arrayByte;
  private final ArrayMergeMethod<  short[], ComparatorShort > arrayShort;
  private final ArrayMergeMethod<    int[], ComparatorInt> arrayInt;
  private final ArrayMergeMethod<   long[], ComparatorLong  > arrayLong;
  private final ArrayMergeMethod<   char[], ComparatorChar> arrayChar;
  private final ArrayMergeMethod<  float[], ComparatorFloat> arrayFloat;
  private final ArrayMergeMethod< double[], ComparatorDouble> arrayDouble;
  private final ArrayMergeMethod<IntBuffer,ComparatorInt    > bufInt;
  private final MethodHandle accessor,
                             arrObjAIIAIIAIC,
                             arrObjAIIAIIAI;

  public StaticMethodsMerger( Class<?> mergeClass )
  {
    arrayByte   = new ArrayMergeMethod<>(mergeClass,   byte[].class, ComparatorByte  .class);
    arrayShort  = new ArrayMergeMethod<>(mergeClass,  short[].class, ComparatorShort .class);
    arrayInt    = new ArrayMergeMethod<>(mergeClass,    int[].class, ComparatorInt   .class);
    arrayLong   = new ArrayMergeMethod<>(mergeClass,   long[].class, ComparatorLong  .class);
    arrayChar   = new ArrayMergeMethod<>(mergeClass,   char[].class, ComparatorChar  .class);
    arrayFloat  = new ArrayMergeMethod<>(mergeClass,  float[].class, ComparatorFloat .class);
    arrayDouble = new ArrayMergeMethod<>(mergeClass, double[].class, ComparatorDouble.class);
    bufInt      = new ArrayMergeMethod<>(mergeClass,IntBuffer.class, ComparatorInt   .class);

    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      accessor        = lookup.findStatic( mergeClass, "merge", methodType(void.class,     Object  .class, int.class, int.class,       Object.class, int.class, int.class,       Object.class, int.class, CompareRandomAccessor.class) );
      arrObjAIIAIIAIC = lookup.findStatic( mergeClass, "merge", methodType(void.class,     Object[].class, int.class, int.class,     Object[].class, int.class, int.class,     Object[].class, int.class, Comparator           .class) );
      arrObjAIIAIIAI  = lookup.findStatic( mergeClass, "merge", methodType(void.class, Comparable[].class, int.class, int.class, Comparable[].class, int.class, int.class, Comparable[].class, int.class                             ) );
    }
    catch( NoSuchMethodException | IllegalAccessException err ) {
      throw new Error(err);
    }
  }

// CONSTRUCTORS

// METHODS
  @Override public <T> void merge(
    T a, int a0, int aLen,
    T b, int b0, int bLen,
    T c, int c0, CompareRandomAccessor<T> acc
  ) {
    try {
      accessor.invoke(
        a,a0,aLen,
        b,b0,bLen,
        c,c0, acc
      );
    }
    catch( RuntimeException|Error ex ) { throw ex; }
    catch( Throwable t ) { throw new Error(t); }
  }

  @Override public <T> void merge( T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0, Comparator<? super T> cmp )
  {
    try {
      arrObjAIIAIIAIC.invoke(
        a,a0,aLen,
        b,b0,bLen,
        c,c0, cmp
      );
    }
    catch( RuntimeException|Error ex ) { throw ex; }
    catch( Throwable t ) { throw new Error(t); }
  }

  @Override public <T extends Comparable<? super T>> void merge( T[] a, int a0, int aLen, T[] b, int b0, int bLen, T[] c, int c0 )
  {
    try {
      arrObjAIIAIIAIC.invoke(
        a,a0,aLen,
        b,b0,bLen,
        c,c0
      );
    }
    catch( RuntimeException|Error ex ) { throw ex; }
    catch( Throwable t ) { throw new Error(t); }
  }

  @Override public byte[] merge( byte[] a,                    byte[] b                                                          ) { return arrayByte.merge(a,b); }
  @Override public byte[] merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen                                        ) { return arrayByte.merge(a,a0,aLen, b,b0,bLen); }
  @Override public byte[] merge( byte[] a,                    byte[] b,                                      ComparatorByte cmp ) { return arrayByte.merge(a,b, cmp); }
  @Override public byte[] merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,                    ComparatorByte cmp ) { return arrayByte.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void   merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0, ComparatorByte cmp ) {        arrayByte.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void   merge( byte[] a, int a0, int aLen,  byte[] b, int b0, int bLen,  byte[] c, int c0                     ) {        arrayByte.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public short[] merge( short[] a,                    short[] b                                                            ) { return arrayShort.merge(a,b); }
  @Override public short[] merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen                                          ) { return arrayShort.merge(a,a0,aLen, b,b0,bLen); }
  @Override public short[] merge( short[] a,                    short[] b,                                       ComparatorShort cmp ) { return arrayShort.merge(a,b, cmp); }
  @Override public short[] merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,                     ComparatorShort cmp ) { return arrayShort.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void    merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,  short[] c, int c0, ComparatorShort cmp ) {        arrayShort.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void    merge( short[] a, int a0, int aLen,  short[] b, int b0, int bLen,  short[] c, int c0                      ) {        arrayShort.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public int[] merge( int[] a,                    int[] b                                                        ) { return arrayInt.merge(a,b); }
  @Override public int[] merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen                                      ) { return arrayInt.merge(a,a0,aLen, b,b0,bLen); }
  @Override public int[] merge( int[] a,                    int[] b,                                     ComparatorInt cmp ) { return arrayInt.merge(a,b, cmp); }
  @Override public int[] merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,                   ComparatorInt cmp ) { return arrayInt.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void  merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,  int[] c, int c0, ComparatorInt cmp ) {        arrayInt.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void  merge( int[] a, int a0, int aLen,  int[] b, int b0, int bLen,  int[] c, int c0                    ) {        arrayInt.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public long[] merge( long[] a,                    long[] b                                                          ) { return arrayLong.merge(a,b); }
  @Override public long[] merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen                                        ) { return arrayLong.merge(a,a0,aLen, b,b0,bLen); }
  @Override public long[] merge( long[] a,                    long[] b,                                      ComparatorLong cmp ) { return arrayLong.merge(a,b, cmp); }
  @Override public long[] merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,                    ComparatorLong cmp ) { return arrayLong.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void   merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0, ComparatorLong cmp ) {        arrayLong.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void   merge( long[] a, int a0, int aLen,  long[] b, int b0, int bLen,  long[] c, int c0                     ) {        arrayLong.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public char[] merge( char[] a,                    char[] b                                                          ) { return arrayChar.merge(a,b); }
  @Override public char[] merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen                                        ) { return arrayChar.merge(a,a0,aLen, b,b0,bLen); }
  @Override public char[] merge( char[] a,                    char[] b,                                      ComparatorChar cmp ) { return arrayChar.merge(a,b, cmp); }
  @Override public char[] merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,                    ComparatorChar cmp ) { return arrayChar.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void   merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0, ComparatorChar cmp ) {        arrayChar.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void   merge( char[] a, int a0, int aLen,  char[] b, int b0, int bLen,  char[] c, int c0                     ) {        arrayChar.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public float[] merge( float[] a,                    float[] b                                                            ) { return arrayFloat.merge(a,b); }
  @Override public float[] merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen                                          ) { return arrayFloat.merge(a,a0,aLen, b,b0,bLen); }
  @Override public float[] merge( float[] a,                    float[] b,                                       ComparatorFloat cmp ) { return arrayFloat.merge(a,b, cmp); }
  @Override public float[] merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,                     ComparatorFloat cmp ) { return arrayFloat.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void    merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,  float[] c, int c0, ComparatorFloat cmp ) {        arrayFloat.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void    merge( float[] a, int a0, int aLen,  float[] b, int b0, int bLen,  float[] c, int c0                      ) {        arrayFloat.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public double[] merge( double[] a,                    double[] b                                                              ) { return arrayDouble.merge(a,b); }
  @Override public double[] merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen                                            ) { return arrayDouble.merge(a,a0,aLen, b,b0,bLen); }
  @Override public double[] merge( double[] a,                    double[] b,                                        ComparatorDouble cmp ) { return arrayDouble.merge(a,b, cmp); }
  @Override public double[] merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,                      ComparatorDouble cmp ) { return arrayDouble.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void     merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,  double[] c, int c0, ComparatorDouble cmp ) {        arrayDouble.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void     merge( double[] a, int a0, int aLen,  double[] b, int b0, int bLen,  double[] c, int c0                       ) {        arrayDouble.merge(a,a0,aLen, b,b0,bLen, c,c0); }

  @Override public IntBuffer merge( IntBuffer a,                    IntBuffer b                                                            ) { return bufInt.merge(a,b); }
  @Override public IntBuffer merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen                                          ) { return bufInt.merge(a,a0,aLen, b,b0,bLen); }
  @Override public IntBuffer merge( IntBuffer a,                    IntBuffer b,                                         ComparatorInt cmp ) { return bufInt.merge(a,b, cmp); }
  @Override public IntBuffer merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,                       ComparatorInt cmp ) { return bufInt.merge(a,a0,aLen, b,b0,bLen, cmp); }
  @Override public void      merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,  IntBuffer c, int c0, ComparatorInt cmp ) {        bufInt.merge(a,a0,aLen, b,b0,bLen, c,c0, cmp); }
  @Override public void      merge( IntBuffer a, int a0, int aLen,  IntBuffer b, int b0, int bLen,  IntBuffer c, int c0                    ) {        bufInt.merge(a,a0,aLen, b,b0,bLen, c,c0); }
}
