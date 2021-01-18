package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.util.Comparator;

public interface Sorter
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

  <T> void sort( T seq, int from, int until, CompareRandomAccessor<? super T> acc );

  <T extends Comparable<? super T>> void sort( T[] seq                                                 );
  <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            );
  <T>                               void sort( T[] seq,                      Comparator<? super T> cmp );
  <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp );

  void sort( byte[] seq                                          );
  void sort( byte[] seq, int from, int until                     );
  void sort( byte[] seq,                      ComparatorByte cmp );
  void sort( byte[] seq, int from, int until, ComparatorByte cmp );

  void sort( short[] seq                                           );
  void sort( short[] seq, int from, int until                      );
  void sort( short[] seq,                      ComparatorShort cmp );
  void sort( short[] seq, int from, int until, ComparatorShort cmp );

  void sort( int[] seq                                         );
  void sort( int[] seq, int from, int until                    );
  void sort( int[] seq,                      ComparatorInt cmp );
  void sort( int[] seq, int from, int until, ComparatorInt cmp );

  void sort( long[] seq                                          );
  void sort( long[] seq, int from, int until                     );
  void sort( long[] seq,                      ComparatorLong cmp );
  void sort( long[] seq, int from, int until, ComparatorLong cmp );

  void sort( char[] seq                                          );
  void sort( char[] seq, int from, int until                     );
  void sort( char[] seq,                      ComparatorChar cmp );
  void sort( char[] seq, int from, int until, ComparatorChar cmp );

  void sort( float[] seq                                           );
  void sort( float[] seq, int from, int until                      );
  void sort( float[] seq,                      ComparatorFloat cmp );
  void sort( float[] seq, int from, int until, ComparatorFloat cmp );

  void sort( double[] seq                                            );
  void sort( double[] seq, int from, int until                       );
  void sort( double[] seq,                      ComparatorDouble cmp );
  void sort( double[] seq, int from, int until, ComparatorDouble cmp );
}
