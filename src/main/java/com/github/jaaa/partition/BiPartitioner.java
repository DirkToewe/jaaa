package com.github.jaaa.partition;

import com.github.jaaa.compare.*;

import java.util.Comparator;
import static java.util.Comparator.naturalOrder;


public interface BiPartitioner
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

  default void biPartition( byte[] seq                                          ) { biPartition( seq, 0,seq.length, Byte::compare); }
  default void biPartition( byte[] seq, int from, int until                     ) { biPartition( seq,  from, until, Byte::compare); }
  default void biPartition( byte[] seq,                      ComparatorByte cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( byte[] seq, int from, int until, ComparatorByte cmp );

  default void biPartition( short[] seq                                           ) { biPartition( seq, 0,seq.length, Short::compare); }
  default void biPartition( short[] seq, int from, int until                      ) { biPartition( seq,  from, until, Short::compare); }
  default void biPartition( short[] seq,                      ComparatorShort cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( short[] seq, int from, int until, ComparatorShort cmp );

  default void biPartition( int[] seq                                         ) { biPartition( seq, 0,seq.length, Integer::compare); }
  default void biPartition( int[] seq, int from, int until                    ) { biPartition( seq,  from, until, Integer::compare); }
  default void biPartition( int[] seq,                      ComparatorInt cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( int[] seq, int from, int until, ComparatorInt cmp );

  default void biPartition( long[] seq                                          ) { biPartition( seq, 0,seq.length, Long::compare); }
  default void biPartition( long[] seq, int from, int until                     ) { biPartition( seq,  from, until, Long::compare); }
  default void biPartition( long[] seq,                      ComparatorLong cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( long[] seq, int from, int until, ComparatorLong cmp );

  default void biPartition( char[] seq                                          ) { biPartition( seq, 0,seq.length, Character::compare); }
  default void biPartition( char[] seq, int from, int until                     ) { biPartition( seq,  from, until, Character::compare); }
  default void biPartition( char[] seq,                      ComparatorChar cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( char[] seq, int from, int until, ComparatorChar cmp );

  default void biPartition( float[] seq                                           ) { biPartition( seq, 0,seq.length, Float::compare); }
  default void biPartition( float[] seq, int from, int until                      ) { biPartition( seq,  from, until, Float::compare); }
  default void biPartition( float[] seq,                      ComparatorFloat cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( float[] seq, int from, int until, ComparatorFloat cmp );

  default void biPartition( double[] seq                                            ) { biPartition( seq, 0,seq.length, Double::compare); }
  default void biPartition( double[] seq, int from, int until                       ) { biPartition( seq,  from, until, Double::compare); }
  default void biPartition( double[] seq,                      ComparatorDouble cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( double[] seq, int from, int until, ComparatorDouble cmp );

  default <T extends Comparable<? super T>> void biPartition( T[] seq                                                 ) { biPartition( seq, 0,seq.length, naturalOrder()); }
  default <T extends Comparable<? super T>> void biPartition( T[] seq, int from, int until                            ) { biPartition( seq,  from, until, naturalOrder()); }
  default <T>                               void biPartition( T[] seq,                      Comparator<? super T> cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          <T>                               void biPartition( T[] seq, int from, int until, Comparator<? super T> cmp );
}
