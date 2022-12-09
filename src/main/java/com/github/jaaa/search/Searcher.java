package com.github.jaaa.search;

import com.github.jaaa.compare.ComparatorByte;
import com.github.jaaa.compare.ComparatorInt;

import java.util.Comparator;
import java.util.function.IntUnaryOperator;

import static java.util.Comparator.naturalOrder;

public interface Searcher
{
  int search ( int from, int until, IntUnaryOperator compass );
  int searchL( int from, int until, IntUnaryOperator compass );
  int searchR( int from, int until, IntUnaryOperator compass );

  int searchGap ( int from, int until, IntUnaryOperator compass );
  int searchGapL( int from, int until, IntUnaryOperator compass );
  int searchGapR( int from, int until, IntUnaryOperator compass );

  default <T extends Comparable<? super T>> int search ( T[] seq,                      T key                            ) { return search ( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int search ( T[] seq, int from, int until, T key                            ) { return search ( seq, from,until,      key, naturalOrder()); }
  default <T>                               int search ( T[] seq,                      T key, Comparator<? super T> cmp ) { return search ( seq,    0,seq.length, key, cmp ); }
  default <T>                               int search ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return search (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default <T extends Comparable<? super T>> int searchR( T[] seq,                      T key                            ) { return searchR( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int searchR( T[] seq, int from, int until, T key                            ) { return searchR( seq, from,until,      key, naturalOrder()); }
  default <T>                               int searchR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchR( seq,    0,seq.length, key, cmp ); }
  default <T>                               int searchR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default <T extends Comparable<? super T>> int searchL( T[] seq,                      T key                            ) { return searchL( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int searchL( T[] seq, int from, int until, T key                            ) { return searchL( seq, from,until,      key, naturalOrder()); }
  default <T>                               int searchL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchL( seq,    0,seq.length, key, cmp ); }
  default <T>                               int searchL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchL(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default <T extends Comparable<? super T>> int searchGap ( T[] seq,                      T key                            ) { return searchGap ( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int searchGap ( T[] seq, int from, int until, T key                            ) { return searchGap ( seq, from,until,      key, naturalOrder()); }
  default <T>                               int searchGap ( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGap ( seq,    0,seq.length, key, cmp ); }
  default <T>                               int searchGap ( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGap (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default <T extends Comparable<? super T>> int searchGapR( T[] seq,                      T key                            ) { return searchGapR( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int searchGapR( T[] seq, int from, int until, T key                            ) { return searchGapR( seq, from,until,      key, naturalOrder()); }
  default <T>                               int searchGapR( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapR( seq,    0,seq.length, key, cmp ); }
  default <T>                               int searchGapR( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default <T extends Comparable<? super T>> int searchGapL( T[] seq,                      T key                            ) { return searchGapL( seq,    0,seq.length, key, naturalOrder()); }
  default <T extends Comparable<? super T>> int searchGapL( T[] seq, int from, int until, T key                            ) { return searchGapL( seq, from,until,      key, naturalOrder()); }
  default <T>                               int searchGapL( T[] seq,                      T key, Comparator<? super T> cmp ) { return searchGapL( seq,    0,seq.length, key, cmp ); }
  default <T>                               int searchGapL( T[] seq, int from, int until, T key, Comparator<? super T> cmp ) { return searchGapL(      from,until,      i -> cmp.compare(key,seq[i]) ); }


  default int search ( byte[] seq,                      byte key                     ) { return search ( seq,    0,seq.length, key, Integer::compare); }
  default int search ( byte[] seq, int from, int until, byte key                     ) { return search ( seq, from,until,      key, Integer::compare); }
  default int search ( byte[] seq,                      byte key, ComparatorByte cmp ) { return search ( seq,    0,seq.length, key, cmp ); }
  default int search ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return search (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchR( byte[] seq,                      byte key                     ) { return searchR( seq,    0,seq.length, key, Integer::compare); }
  default int searchR( byte[] seq, int from, int until, byte key                     ) { return searchR( seq, from,until,      key, Integer::compare); }
  default int searchR( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchR( seq,    0,seq.length, key, cmp ); }
  default int searchR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchL( byte[] seq,                      byte key                     ) { return searchL( seq,    0,seq.length, key, Integer::compare); }
  default int searchL( byte[] seq, int from, int until, byte key                     ) { return searchL( seq, from,until,      key, Integer::compare); }
  default int searchL( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchL( seq,    0,seq.length, key, cmp ); }
  default int searchL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchL(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGap ( byte[] seq,                      byte key                     ) { return searchGap ( seq,    0,seq.length, key, Integer::compare); }
  default int searchGap ( byte[] seq, int from, int until, byte key                     ) { return searchGap ( seq, from,until,      key, Integer::compare); }
  default int searchGap ( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGap ( seq,    0,seq.length, key, cmp ); }
  default int searchGap ( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGap (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGapR( byte[] seq,                      byte key                     ) { return searchGapR( seq,    0,seq.length, key, Integer::compare); }
  default int searchGapR( byte[] seq, int from, int until, byte key                     ) { return searchGapR( seq, from,until,      key, Integer::compare); }
  default int searchGapR( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGapR( seq,    0,seq.length, key, cmp ); }
  default int searchGapR( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGapR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGapL( byte[] seq,                      byte key                     ) { return searchGapL( seq,    0,seq.length, key, Integer::compare); }
  default int searchGapL( byte[] seq, int from, int until, byte key                     ) { return searchGapL( seq, from,until,      key, Integer::compare); }
  default int searchGapL( byte[] seq,                      byte key, ComparatorByte cmp ) { return searchGapL( seq,    0,seq.length, key, cmp ); }
  default int searchGapL( byte[] seq, int from, int until, byte key, ComparatorByte cmp ) { return searchGapL(      from,until, i -> cmp.compare(key,seq[i]) ); }


  default int search ( int[] seq,                      int key                    ) { return search ( seq,    0,seq.length, key, Integer::compare); }
  default int search ( int[] seq, int from, int until, int key                    ) { return search ( seq, from,until,      key, Integer::compare); }
  default int search ( int[] seq,                      int key, ComparatorInt cmp ) { return search ( seq,    0,seq.length, key, cmp ); }
  default int search ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return search (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchR( int[] seq,                      int key                    ) { return searchR( seq,    0,seq.length, key, Integer::compare); }
  default int searchR( int[] seq, int from, int until, int key                    ) { return searchR( seq, from,until,      key, Integer::compare); }
  default int searchR( int[] seq,                      int key, ComparatorInt cmp ) { return searchR( seq,    0,seq.length, key, cmp ); }
  default int searchR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchL( int[] seq,                      int key                    ) { return searchL( seq,    0,seq.length, key, Integer::compare); }
  default int searchL( int[] seq, int from, int until, int key                    ) { return searchL( seq, from,until,      key, Integer::compare); }
  default int searchL( int[] seq,                      int key, ComparatorInt cmp ) { return searchL( seq,    0,seq.length, key, cmp ); }
  default int searchL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchL(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGap ( int[] seq,                      int key                    ) { return searchGap ( seq,    0,seq.length, key, Integer::compare); }
  default int searchGap ( int[] seq, int from, int until, int key                    ) { return searchGap ( seq, from,until,      key, Integer::compare); }
  default int searchGap ( int[] seq,                      int key, ComparatorInt cmp ) { return searchGap ( seq,    0,seq.length, key, cmp ); }
  default int searchGap ( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGap (      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGapR( int[] seq,                      int key                    ) { return searchGapR( seq,    0,seq.length, key, Integer::compare); }
  default int searchGapR( int[] seq, int from, int until, int key                    ) { return searchGapR( seq, from,until,      key, Integer::compare); }
  default int searchGapR( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapR( seq,    0,seq.length, key, cmp ); }
  default int searchGapR( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapR(      from,until,      i -> cmp.compare(key,seq[i]) ); }

  default int searchGapL( int[] seq,                      int key                    ) { return searchGapL( seq,    0,seq.length, key, Integer::compare); }
  default int searchGapL( int[] seq, int from, int until, int key                    ) { return searchGapL( seq, from,until,      key, Integer::compare); }
  default int searchGapL( int[] seq,                      int key, ComparatorInt cmp ) { return searchGapL( seq,    0,seq.length, key, cmp ); }
  default int searchGapL( int[] seq, int from, int until, int key, ComparatorInt cmp ) { return searchGapL(      from,until,      i -> cmp.compare(key,seq[i]) ); }
}
