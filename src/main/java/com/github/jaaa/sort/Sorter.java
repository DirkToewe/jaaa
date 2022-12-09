package com.github.jaaa.sort;

import com.github.jaaa.compare.*;

import java.nio.IntBuffer;
import java.util.Comparator;

public interface Sorter
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

  <T> void sort( T arr, int from, int until, CompareRandomAccessor<T> acc );

  default <T extends Comparable<? super T>> void sort( T[] arr                                                 ) { sort(arr, 0,arr.length); }
          <T extends Comparable<? super T>> void sort( T[] arr, int from, int until                            );
  default <T>                               void sort( T[] arr,                      Comparator<? super T> cmp ) { sort(arr, 0,arr.length, cmp); }
          <T>                               void sort( T[] arr, int from, int until, Comparator<? super T> cmp );

  default void sort( byte[] arr                                          ) { sort(arr, 0,arr.length); }
          void sort( byte[] arr, int from, int until                     );
  default void sort( byte[] arr,                      ComparatorByte cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( byte[] arr, int from, int until, ComparatorByte cmp );

  default void sort( short[] arr                                           ) { sort(arr, 0,arr.length); }
          void sort( short[] arr, int from, int until                      );
  default void sort( short[] arr,                      ComparatorShort cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( short[] arr, int from, int until, ComparatorShort cmp );

  default void sort( int[] arr                                         ) { sort(arr, 0,arr.length); }
          void sort( int[] arr, int from, int until                    );
  default void sort( int[] arr,                      ComparatorInt cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( int[] arr, int from, int until, ComparatorInt cmp );

  default void sort( long[] arr                                          ) { sort(arr, 0,arr.length); }
          void sort( long[] arr, int from, int until                     );
  default void sort( long[] arr,                      ComparatorLong cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( long[] arr, int from, int until, ComparatorLong cmp );

  default void sort( char[] arr                                          ) { sort(arr, 0,arr.length); }
          void sort( char[] arr, int from, int until                     );
  default void sort( char[] arr,                      ComparatorChar cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( char[] arr, int from, int until, ComparatorChar cmp );

  default void sort( float[] arr                                           ) { sort(arr, 0,arr.length); }
          void sort( float[] arr, int from, int until                      );
  default void sort( float[] arr,                      ComparatorFloat cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( float[] arr, int from, int until, ComparatorFloat cmp );

  default void sort( double[] arr                                            ) { sort(arr, 0,arr.length); }
          void sort( double[] arr, int from, int until                       );
  default void sort( double[] arr,                      ComparatorDouble cmp ) { sort(arr, 0,arr.length, cmp); }
          void sort( double[] arr, int from, int until, ComparatorDouble cmp );

  default void sort( IntBuffer buf                                         ) { sort(buf, buf.position(),buf.limit()); }
          void sort( IntBuffer buf, int from, int until                    );
  default void sort( IntBuffer buf,                      ComparatorInt cmp ) { sort(buf, buf.position(),buf.limit(), cmp); }
          void sort( IntBuffer buf, int from, int until, ComparatorInt cmp );
}
