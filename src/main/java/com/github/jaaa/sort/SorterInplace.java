package com.github.jaaa.sort;

import com.github.jaaa.*;

import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

public interface SorterInplace extends SorterOfArraysObject,
                                       SorterOfArraysInt
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

          void sort( int from, int until, CompareSwapAccess acc );

  default void sort( byte[] seq                                          ) { sort(seq,    0,seq.length, Byte::compare); }
  default void sort( byte[] seq, int from, int until                     ) { sort(seq, from,until,      Byte::compare); }
  default void sort( byte[] seq,                      ComparatorByte cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( byte[] seq, int from, int until, ComparatorByte cmp );

  default void sort( short[] seq                                           ) { sort(seq,    0,seq.length, Short::compare); }
  default void sort( short[] seq, int from, int until                      ) { sort(seq, from,until,      Short::compare); }
  default void sort( short[] seq,                      ComparatorShort cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( short[] seq, int from, int until, ComparatorShort cmp );

  default void sort( int[] seq                                         ) { sort(seq,    0,seq.length, Integer::compare); }
  default void sort( int[] seq, int from, int until                    ) { sort(seq, from,until,      Integer::compare); }
  default void sort( int[] seq,                      ComparatorInt cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( int[] seq, int from, int until, ComparatorInt cmp );

  default void sort( long[] seq                                          ) { sort(seq,    0,seq.length, Long::compare); }
  default void sort( long[] seq, int from, int until                     ) { sort(seq, from,until,      Long::compare); }
  default void sort( long[] seq,                      ComparatorLong cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( long[] seq, int from, int until, ComparatorLong cmp );

  default void sort( char[] seq                                          ) { sort(seq,    0,seq.length, Character::compare); }
  default void sort( char[] seq, int from, int until                     ) { sort(seq, from,until,      Character::compare); }
  default void sort( char[] seq,                      ComparatorChar cmp ) { sort(seq,    0,seq.length, cmp ); }
           void sort( char[] seq, int from, int until, ComparatorChar cmp );

  default void sort( float[] seq                                           ) { sort(seq,    0,seq.length, Float::compare); }
  default void sort( float[] seq, int from, int until                      ) { sort(seq, from,until,      Float::compare); }
  default void sort( float[] seq,                      ComparatorFloat cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( float[] seq, int from, int until, ComparatorFloat cmp );

  default void sort( double[] seq                                            ) { sort(seq,    0,seq.length, Double::compare); }
  default void sort( double[] seq, int from, int until                       ) { sort(seq, from,until,      Double::compare); }
  default void sort( double[] seq,                      ComparatorDouble cmp ) { sort(seq,    0,seq.length, cmp ); }
          void sort( double[] seq, int from, int until, ComparatorDouble cmp );

  default <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { sort(seq,    0,seq.length, naturalOrder() ); }
  default <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { sort(seq, from,until,      naturalOrder() ); }
  default <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { sort(seq,    0,seq.length, cmp ); }
          <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp );
}
