package com.github.jaaa.partition;

import com.github.jaaa.*;

import java.util.Comparator;

public interface BiPartitioner
{
  public boolean isStable();
  public boolean isInplace();
  public default boolean isThreadSafe() { return false; }

  public default void biPartition( byte[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( byte[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( byte[] seq,                      ComparatorByte cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( byte[] seq, int from, int until, ComparatorByte cmp );

  public default void biPartition( short[] seq                                           ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( short[] seq, int from, int until                      ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( short[] seq,                      ComparatorShort cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( short[] seq, int from, int until, ComparatorShort cmp );

  public default void biPartition( int[] seq                                         ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( int[] seq, int from, int until                    ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( int[] seq,                      ComparatorInt cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( int[] seq, int from, int until, ComparatorInt cmp );

  public default void biPartition( long[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( long[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( long[] seq,                      ComparatorLong cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( long[] seq, int from, int until, ComparatorLong cmp );

  public default void biPartition( char[] seq                                          ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( char[] seq, int from, int until                     ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( char[] seq,                      ComparatorChar cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( char[] seq, int from, int until, ComparatorChar cmp );

  public default void biPartition( float[] seq                                           ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( float[] seq, int from, int until                      ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( float[] seq,                      ComparatorFloat cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( float[] seq, int from, int until, ComparatorFloat cmp );

  public default void biPartition( double[] seq                                            ) { biPartition( seq, 0,seq.length, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( double[] seq, int from, int until                       ) { biPartition( seq,  from, until, (x,y) -> (x < y) ? -1 : (x==y ? 0 : 1) ); }
  public default void biPartition( double[] seq,                      ComparatorDouble cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         void biPartition( double[] seq, int from, int until, ComparatorDouble cmp );

  public default <T extends Comparable<? super T>> void biPartition( T[] seq                                                 ) { biPartition( seq, 0,seq.length, (x,y) -> x.compareTo(y) ); }
  public default <T extends Comparable<? super T>> void biPartition( T[] seq, int from, int until                            ) { biPartition( seq,  from, until, (x,y) -> x.compareTo(y) ); }
  public default <T>                               void biPartition( T[] seq,                      Comparator<? super T> cmp ) { biPartition( seq, 0,seq.length, cmp ); }
  public         <T>                               void biPartition( T[] seq, int from, int until, Comparator<? super T> cmp );
}
