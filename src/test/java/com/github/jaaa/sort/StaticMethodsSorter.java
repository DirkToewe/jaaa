package com.github.jaaa.sort;

import com.github.jaaa.compare.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.IntBuffer;
import java.util.Comparator;

import static java.lang.invoke.MethodType.methodType;

/** A {@link SorterInPlace} which relays all sort calls to the static sort methods of a class.
 *  Used to test static sort methods the same way as any other {@link SorterInPlace}.
 */
public abstract class StaticMethodsSorter implements Sorter
{
  private static final class ArraySortMethod<A,C>
  {
    private final MethodHandle
      methodA,
      methodAII,
      methodAC,
      methodAIIC;

    public ArraySortMethod( Class<?> sortClass, Class<A> arrType, Class<C> comparatorType )
    {
      MethodHandles.Lookup lookup = MethodHandles.publicLookup();
      Class<?> cmpType = arrType == Object[].class ? Comparable[].class : arrType;
      try {
        methodA    = lookup.findStatic( sortClass, "sort", methodType(void.class, cmpType) );
        methodAII  = lookup.findStatic( sortClass, "sort", methodType(void.class, cmpType, int.class, int.class) );
        methodAC   = lookup.findStatic( sortClass, "sort", methodType(void.class, arrType                      , comparatorType) );
        methodAIIC = lookup.findStatic( sortClass, "sort", methodType(void.class, arrType, int.class, int.class, comparatorType) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new Error(err);
      }
    }

    public void sort( A array )
    {
      try {
        methodA.invoke(array);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public void sort( A array, int from, int until )
    {
      try {
        methodAII.invoke(array,from,until);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public void sort( A array, C comparator )
    {
      try {
        methodAC.invoke(array,comparator);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }

    public void sort( A array, int from, int until, C comparator )
    {
      try {
        methodAIIC.invoke(array,from,until,comparator);
      }
      catch( RuntimeException|Error ex ) { throw ex; }
      catch( Throwable t ) { throw new Error(t); }
    }
  }

  private final ArraySortMethod<   byte[], ComparatorByte  > arrayByte;
  private final ArraySortMethod<  short[], ComparatorShort > arrayShort;
  private final ArraySortMethod<    int[], ComparatorInt   > arrayInt;
  private final ArraySortMethod<   long[], ComparatorLong  > arrayLong;
  private final ArraySortMethod<   char[], ComparatorChar  > arrayChar;
  private final ArraySortMethod<  float[], ComparatorFloat > arrayFloat;
  private final ArraySortMethod< double[], ComparatorDouble> arrayDouble;
  private final ArraySortMethod< Object[], Comparator      > arrayObject;
  private final ArraySortMethod<IntBuffer, ComparatorInt   > bufInt;
  private final MethodHandle                               accessor;

  public StaticMethodsSorter( Class<?> sortClass )
  {
    arrayByte   = new ArraySortMethod<>(sortClass,   byte[].class, ComparatorByte  .class);
    arrayShort  = new ArraySortMethod<>(sortClass,  short[].class, ComparatorShort .class);
    arrayInt    = new ArraySortMethod<>(sortClass,    int[].class, ComparatorInt   .class);
    arrayLong   = new ArraySortMethod<>(sortClass,   long[].class, ComparatorLong  .class);
    arrayChar   = new ArraySortMethod<>(sortClass,   char[].class, ComparatorChar  .class);
    arrayFloat  = new ArraySortMethod<>(sortClass,  float[].class, ComparatorFloat .class);
    arrayDouble = new ArraySortMethod<>(sortClass, double[].class, ComparatorDouble.class);
    arrayObject = new ArraySortMethod<>(sortClass, Object[].class, Comparator      .class);
    bufInt      = new ArraySortMethod<>(sortClass,IntBuffer.class, ComparatorInt   .class);

    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    try {
      accessor = lookup.findStatic( sortClass, "sort", methodType(void.class, Object.class, int.class, int.class, CompareRandomAccessor.class) );
    }
    catch( NoSuchMethodException | IllegalAccessException err ) {
      throw new Error(err);
    }
  }

  @Override public <T> void sort( T seq, int from, int until, CompareRandomAccessor<T> acc )
  {
    try {
      accessor.invoke(seq,from,until,acc);
    }
    catch( RuntimeException|Error ex ) { throw ex; }
    catch( Throwable t ) { throw new Error(t); }
  }

  @Override public void sort(   byte[] seq                                            ) { arrayByte.sort(seq); }
  @Override public void sort(   byte[] seq, int from, int until                       ) { arrayByte.sort(seq,from,until); }
  @Override public void sort(   byte[] seq,                      ComparatorByte   cmp ) { arrayByte.sort(seq           ,cmp); }
  @Override public void sort(   byte[] seq, int from, int until, ComparatorByte   cmp ) { arrayByte.sort(seq,from,until,cmp); }

  @Override public void sort(  short[] seq                                            ) { arrayShort.sort(seq); }
  @Override public void sort(  short[] seq, int from, int until                       ) { arrayShort.sort(seq,from,until); }
  @Override public void sort(  short[] seq,                      ComparatorShort  cmp ) { arrayShort.sort(seq           ,cmp); }
  @Override public void sort(  short[] seq, int from, int until, ComparatorShort  cmp ) { arrayShort.sort(seq,from,until,cmp); }

  @Override public void sort(    int[] seq                                            ) { arrayInt.sort(seq); }
  @Override public void sort(    int[] seq, int from, int until                       ) { arrayInt.sort(seq,from,until); }
  @Override public void sort(    int[] seq,                      ComparatorInt    cmp ) { arrayInt.sort(seq           ,cmp); }
  @Override public void sort(    int[] seq, int from, int until, ComparatorInt    cmp ) { arrayInt.sort(seq,from,until,cmp); }

  @Override public void sort(   long[] seq                                            ) { arrayLong.sort(seq); }
  @Override public void sort(   long[] seq, int from, int until                       ) { arrayLong.sort(seq,from,until); }
  @Override public void sort(   long[] seq,                      ComparatorLong   cmp ) { arrayLong.sort(seq           ,cmp); }
  @Override public void sort(   long[] seq, int from, int until, ComparatorLong   cmp ) { arrayLong.sort(seq,from,until,cmp); }

  @Override public void sort(   char[] seq                                            ) { arrayChar.sort(seq); }
  @Override public void sort(   char[] seq, int from, int until                       ) { arrayChar.sort(seq,from,until); }
  @Override public void sort(   char[] seq,                      ComparatorChar   cmp ) { arrayChar.sort(seq           ,cmp); }
  @Override public void sort(   char[] seq, int from, int until, ComparatorChar   cmp ) { arrayChar.sort(seq,from,until,cmp); }

  @Override public void sort(  float[] seq                                            ) { arrayFloat.sort(seq); }
  @Override public void sort(  float[] seq, int from, int until                       ) { arrayFloat.sort(seq,from,until); }
  @Override public void sort(  float[] seq,                      ComparatorFloat  cmp ) { arrayFloat.sort(seq           ,cmp); }
  @Override public void sort(  float[] seq, int from, int until, ComparatorFloat  cmp ) { arrayFloat.sort(seq,from,until,cmp); }

  @Override public void sort( double[] seq                                            ) { arrayDouble.sort(seq); }
  @Override public void sort( double[] seq, int from, int until                       ) { arrayDouble.sort(seq,from,until); }
  @Override public void sort( double[] seq,                      ComparatorDouble cmp ) { arrayDouble.sort(seq           ,cmp); }
  @Override public void sort( double[] seq, int from, int until, ComparatorDouble cmp ) { arrayDouble.sort(seq,from,until,cmp); }

  @Override public void sort( IntBuffer buf                                         ) { bufInt.sort(buf); }
  @Override public void sort( IntBuffer buf, int from, int until                    ) { bufInt.sort(buf,from,until); }
  @Override public void sort( IntBuffer buf,                      ComparatorInt cmp ) { bufInt.sort(buf           ,cmp); }
  @Override public void sort( IntBuffer buf, int from, int until, ComparatorInt cmp ) { bufInt.sort(buf,from,until,cmp); }

  @Override public <T extends Comparable<? super T>> void sort( T[] seq                                                 )  { arrayObject.sort(seq); }
  @Override public <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            )  { arrayObject.sort(seq,from,until); }
  @Override public <T>                               void sort( T[] seq,                      Comparator<? super T> cmp )  { arrayObject.sort(seq           ,cmp); }
  @Override public <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp )  { arrayObject.sort(seq,from,until,cmp); }
}
