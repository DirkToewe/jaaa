package com.github.jaaa.select;

import com.github.jaaa.*;
import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.compare.CompareRandomAccessorArrObj;
import com.github.jaaa.Boxing;
import com.github.jaaa.util.ZipWithIndex;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.util.ZipWithIndex.zipWithIndex;
import static java.lang.Math.min;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 100_000 )
public interface SelectAccessorTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS
  interface SelectAccessor<T> {
    void select( T arr, int from, int mid, int until );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  <T> SelectAccessor<T> createAccessor( CompareRandomAccessor<T> srtAcc );

  boolean isStable();
  default boolean sortsLHS( int from, int mid, int until ) { return false; }
  default boolean sortsRHS( int from, int mid, int until ) { return false; }

  @Property default void selectsBoolean( @ForAll("arraysWithInsertIndexBoolean") WithInsertIndex<boolean[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsByte   ( @ForAll("arraysWithInsertIndexByte"   ) WithInsertIndex<   byte[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsShort  ( @ForAll("arraysWithInsertIndexShort"  ) WithInsertIndex<  short[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsInt    ( @ForAll("arraysWithInsertIndexInt"    ) WithInsertIndex<    int[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsLong   ( @ForAll("arraysWithInsertIndexLong"   ) WithInsertIndex<   long[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsChar   ( @ForAll("arraysWithInsertIndexChar"   ) WithInsertIndex<   char[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsFloat  ( @ForAll("arraysWithInsertIndexFloat"  ) WithInsertIndex<  float[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }
  @Property default void selectsDouble ( @ForAll("arraysWithInsertIndexDouble" ) WithInsertIndex< double[]> sample ) { selects( sample.map( x -> new WithRange<>(0,x.length, boxed(x)) ) ); }

  @Property default void selectsRangeBoolean( @ForAll("arraysWithRangeWithInsertIndexBoolean") WithInsertIndex<WithRange<boolean[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeByte   ( @ForAll("arraysWithRangeWithInsertIndexByte"   ) WithInsertIndex<WithRange<   byte[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeShort  ( @ForAll("arraysWithRangeWithInsertIndexShort"  ) WithInsertIndex<WithRange<  short[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeInt    ( @ForAll("arraysWithRangeWithInsertIndexInt"    ) WithInsertIndex<WithRange<    int[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeLong   ( @ForAll("arraysWithRangeWithInsertIndexLong"   ) WithInsertIndex<WithRange<   long[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeChar   ( @ForAll("arraysWithRangeWithInsertIndexChar"   ) WithInsertIndex<WithRange<   char[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeFloat  ( @ForAll("arraysWithRangeWithInsertIndexFloat"  ) WithInsertIndex<WithRange<  float[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }
  @Property default void selectsRangeDouble ( @ForAll("arraysWithRangeWithInsertIndexDouble" ) WithInsertIndex<WithRange< double[]>> sample ) { selects( sample.map( x -> x.map(Boxing::boxed) ) ); }

  default <T extends Comparable<? super T>> void selects( WithInsertIndex<WithRange<T[]>> sample )
  {
    int mid = sample.getIndex(),
       from = sample.getData().getFrom(),
      until = sample.getData().getUntil();
    T[] tst = sample.getData().getData().clone();

    SelectAccessor<T[]> acc = createAccessor(new CompareRandomAccessorArrObj<T>() {
      @Override public T[] malloc( int len ) {
        return (T[]) new Comparable[len];
      }
      @Override public int compare( T[] a, int i, T[] b, int j ) {
        if( a == tst ) assertThat(i).isBetween(from,until-1);
        if( b == tst ) assertThat(j).isBetween(from,until-1);
        return a[i].compareTo(b[j]);
      }
    });

    acc.select(tst,from,mid,until);

    if( ! sortsLHS(from,mid,until) ) Arrays.sort(tst, from, mid);
    if( ! sortsRHS(from,mid,until) ) Arrays.sort(tst, min(1+mid,until), until);

    T[] ref = sample.getData().getData().clone();
    Arrays.sort(ref, from,until);

    assertThat(tst).isEqualTo(ref);
  }

  @Property default void selectsStablyBoolean( @ForAll("arraysWithInsertIndexBoolean") WithInsertIndex<boolean[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyByte   ( @ForAll("arraysWithInsertIndexByte"   ) WithInsertIndex<   byte[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyShort  ( @ForAll("arraysWithInsertIndexShort"  ) WithInsertIndex<  short[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyInt    ( @ForAll("arraysWithInsertIndexInt"    ) WithInsertIndex<    int[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyLong   ( @ForAll("arraysWithInsertIndexLong"   ) WithInsertIndex<   long[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyChar   ( @ForAll("arraysWithInsertIndexChar"   ) WithInsertIndex<   char[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyFloat  ( @ForAll("arraysWithInsertIndexFloat"  ) WithInsertIndex<  float[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }
  @Property default void selectsStablyDouble ( @ForAll("arraysWithInsertIndexDouble" ) WithInsertIndex< double[]> sample ) { selectsStably( sample.map( x -> new WithRange<>(0,x.length, zipWithIndex(x)) ) ); }

  @Property default void selectsStablyRangeBoolean( @ForAll("arraysWithRangeWithInsertIndexBoolean") WithInsertIndex<WithRange<boolean[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeByte   ( @ForAll("arraysWithRangeWithInsertIndexByte"   ) WithInsertIndex<WithRange<   byte[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeShort  ( @ForAll("arraysWithRangeWithInsertIndexShort"  ) WithInsertIndex<WithRange<  short[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeInt    ( @ForAll("arraysWithRangeWithInsertIndexInt"    ) WithInsertIndex<WithRange<    int[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeLong   ( @ForAll("arraysWithRangeWithInsertIndexLong"   ) WithInsertIndex<WithRange<   long[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeChar   ( @ForAll("arraysWithRangeWithInsertIndexChar"   ) WithInsertIndex<WithRange<   char[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeFloat  ( @ForAll("arraysWithRangeWithInsertIndexFloat"  ) WithInsertIndex<WithRange<  float[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }
  @Property default void selectsStablyRangeDouble ( @ForAll("arraysWithRangeWithInsertIndexDouble" ) WithInsertIndex<WithRange< double[]>> sample ) { selectsStably( sample.map( x -> x.map(ZipWithIndex::zipWithIndex) ) ); }

  default <T extends Comparable<? super T>> void selectsStably( WithInsertIndex<WithRange<Entry<T,Integer>[]>> sample )
  {
    int mid = sample.getIndex(),
       from = sample.getData().getFrom(),
      until = sample.getData().getUntil();
    Entry<T,Integer>[] tst = sample.getData().getData().clone();

    Comparator<Entry<T,Integer>> cmp = comparingByKey();
    if( ! isStable() )     cmp = cmp.thenComparing(comparingByValue());

    Comparator<Entry<T,Integer>> CMP = cmp;
    SelectAccessor<Entry<T,Integer>[]> acc = createAccessor(new CompareRandomAccessorArrObj<Entry<T,Integer>>() {
      @SuppressWarnings("unchecked")
      @Override public Entry<T,Integer>[] malloc( int len ) {
        return (Entry<T,Integer>[]) new Entry[len];
      }
      @Override public int compare( Entry<T,Integer>[] a, int i, Entry<T,Integer>[] b, int j ) {
        if( a == tst ) assertThat(i).isBetween(from,until-1);
        if( b == tst ) assertThat(j).isBetween(from,until-1);
        return CMP.compare(a[i],b[j]);
      }
    });

    acc.select(tst,from,mid,until);

    if( ! sortsLHS(from,mid,until) ) Arrays.sort(tst, from, mid, CMP);
    if( ! sortsRHS(from,mid,until) ) Arrays.sort(tst, min(1+mid,until), until, CMP);

    Entry<T,Integer>[] ref = sample.getData().getData().clone();
    Arrays.sort(ref, from,until, cmp);

    assertThat(tst).isEqualTo(ref);
  }
}
