package com.github.jaaa.sort;

import java.util.Comparator;

import static java.util.Comparator.naturalOrder;

public interface SorterOfArraysObject
{
  public boolean isStable();
  public boolean isInplace();
  public default boolean isThreadSafe() { return false; }

  public default <T extends Comparable<? super T>> void sort( T[] seq                                                 ) { sort(seq,    0,seq.length, naturalOrder() ); }
  public default <T extends Comparable<? super T>> void sort( T[] seq, int from, int until                            ) { sort(seq, from,until,      naturalOrder() ); }
  public default <T>                               void sort( T[] seq,                      Comparator<? super T> cmp ) { sort(seq,    0,seq.length, cmp ); }
  public         <T>                               void sort( T[] seq, int from, int until, Comparator<? super T> cmp );
}
