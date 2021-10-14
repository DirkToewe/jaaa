package com.github.jaaa.select;

import com.github.jaaa.*;

import java.nio.IntBuffer;
import java.util.Comparator;

public interface Selector
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

  <T> void select( T arr, int from, int mid, int until, CompareRandomAccessor<T> acc );

  default <T extends Comparable<? super T>> void select( T[] arr,           int mid                                       ) { select(arr, 0,mid,arr.length); }
          <T extends Comparable<? super T>> void select( T[] arr, int from, int mid, int until                            );
  default <T>                               void select( T[] arr,           int mid,            Comparator<? super T> cmp ) { select(arr, 0,mid,arr.length, cmp); }
          <T>                               void select( T[] arr, int from, int mid, int until, Comparator<? super T> cmp );

  default void select( byte[] arr,           int mid                                ) { select(arr, 0,mid,arr.length); }
          void select( byte[] arr, int from, int mid, int until                     );
  default void select( byte[] arr,           int mid,            ComparatorByte cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( byte[] arr, int from, int mid, int until, ComparatorByte cmp );

  default void select( short[] arr,           int mid                                 ) { select(arr, 0,mid,arr.length); }
          void select( short[] arr, int from, int mid, int until                      );
  default void select( short[] arr,           int mid,            ComparatorShort cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( short[] arr, int from, int mid, int until, ComparatorShort cmp );

  default void select( int[] arr,           int mid                               ) { select(arr, 0,mid,arr.length); }
          void select( int[] arr, int from, int mid, int until                    );
  default void select( int[] arr,           int mid,            ComparatorInt cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( int[] arr, int from, int mid, int until, ComparatorInt cmp );

  default void select( long[] arr,           int mid                                ) { select(arr, 0,mid,arr.length); }
          void select( long[] arr, int from, int mid, int until                     );
  default void select( long[] arr,           int mid,            ComparatorLong cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( long[] arr, int from, int mid, int until, ComparatorLong cmp );

  default void select( char[] arr,           int mid                                ) { select(arr, 0,mid,arr.length); }
          void select( char[] arr, int from, int mid, int until                     );
  default void select( char[] arr,           int mid,            ComparatorChar cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( char[] arr, int from, int mid, int until, ComparatorChar cmp );

  default void select( float[] arr,           int mid                                 ) { select(arr, 0,mid,arr.length); }
          void select( float[] arr, int from, int mid, int until                      );
  default void select( float[] arr,           int mid,            ComparatorFloat cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( float[] arr, int from, int mid, int until, ComparatorFloat cmp );

  default void select( double[] arr,           int mid                                  ) { select(arr, 0,mid,arr.length); }
          void select( double[] arr, int from, int mid, int until                       );
  default void select( double[] arr,           int mid,            ComparatorDouble cmp ) { select(arr, 0,mid,arr.length, cmp); }
          void select( double[] arr, int from, int mid, int until, ComparatorDouble cmp );

  default void select( IntBuffer buf,           int mid                               ) { select(buf, buf.position(),mid,buf.limit()); }
          void select( IntBuffer buf, int from, int mid, int until                    );
  default void select( IntBuffer buf,           int mid,            ComparatorInt cmp ) { select(buf, buf.position(),mid,buf.limit(), cmp); }
          void select( IntBuffer buf, int from, int mid, int until, ComparatorInt cmp );
}
