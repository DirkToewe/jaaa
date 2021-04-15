package com.github.jaaa.partition;

import com.github.jaaa.*;

import java.util.Comparator;

public interface BiPartitioner
{
  boolean isStable();
  default boolean isThreadSafe() { return false; }

  default void biPartition( byte[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( byte[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( byte[] seq,                      ComparatorByte cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( byte[] seq, int from, int until, ComparatorByte cmp );

  default void biPartition( short[] seq                                           ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( short[] seq, int from, int until                      ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( short[] seq,                      ComparatorShort cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( short[] seq, int from, int until, ComparatorShort cmp );

  default void biPartition( int[] seq                                         ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( int[] seq, int from, int until                    ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( int[] seq,                      ComparatorInt cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( int[] seq, int from, int until, ComparatorInt cmp );

  default void biPartition( long[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( long[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( long[] seq,                      ComparatorLong cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( long[] seq, int from, int until, ComparatorLong cmp );

  default void biPartition( char[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( char[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( char[] seq,                      ComparatorChar cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( char[] seq, int from, int until, ComparatorChar cmp );

  default void biPartition( float[] seq                                           ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( float[] seq, int from, int until                      ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( float[] seq,                      ComparatorFloat cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( float[] seq, int from, int until, ComparatorFloat cmp );

  default void biPartition( double[] seq                                            ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( double[] seq, int from, int until                       ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  default void biPartition( double[] seq,                      ComparatorDouble cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          void biPartition( double[] seq, int from, int until, ComparatorDouble cmp );

  default <T extends Comparable<? super T>> void biPartition( T[] seq                                                 ) { biPartition( seq, 0,seq.length, (x,y) -> x.compareTo(y) ); }
  default <T extends Comparable<? super T>> void biPartition( T[] seq, int from, int until                            ) { biPartition( seq,  from, until, (x,y) -> x.compareTo(y) ); }
  default <T>                               void biPartition( T[] seq,                      Comparator<? super T> cmp ) { biPartition( seq, 0,seq.length, cmp ); }
          <T>                               void biPartition( T[] seq, int from, int until, Comparator<? super T> cmp );
}
