package com.github.jaaa.merge;

import com.github.jaaa.*;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.misc.Boxing.unboxed;
import static com.github.jaaa.util.ZipWithIndex.zipWithIndex;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.assertj.core.api.Assertions.assertThat;


public interface MergeAccessTestTemplate extends MergeDataProviderTemplate
{
  // STATIC FIELDS
  int N_TRIES = 100_000,
     MAX_SIZE = 8192;

  boolean isStable();

  interface MergeAccess {
    void merge( int from, int mid, int until );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  MergeAccess createAccess( CompareSwapAccess srtAcc );

  @Property(tries=N_TRIES) default void mergesArr_limitedRanges(
    @ForAll("mergeSamples_limitedRanges") WithInsertIndex<WithRange<Byte[]>> sample, @ForAll Comparator<Byte> cmp
  ) { mergesArr(sample,cmp); }

  @Property(tries=N_TRIES) default void mergesStablyArr_limitedRanges(
    @ForAll("mergeSamples_limitedRanges") WithInsertIndex<WithRange<Byte[]>> sample, @ForAll Comparator<Byte> cmp
  ) { mergesStablyArr(sample,cmp); }

  @Property(tries=2_200_000) default void merges_exhaustive_small( @ForAll("mergeSamples_exhaustive_small") MergeInput<byte[]> sample ) { merges_exhaustive(sample); }
  @Property(tries=2_200_000) default void merges_exhaustive_len13( @ForAll("mergeSamples_exhaustive_len13") MergeInput<byte[]> sample ) { merges_exhaustive(sample); }
  @Property(tries=2_200_000) default void merges_exhaustive_len14( @ForAll("mergeSamples_exhaustive_len14") MergeInput<byte[]> sample ) { merges_exhaustive(sample); }
  @Property(tries=2_200_000) default void merges_exhaustive_len15( @ForAll("mergeSamples_exhaustive_len15") MergeInput<byte[]> sample ) { merges_exhaustive(sample); }
  @Property(tries=2_200_000) default void merges_exhaustive_len16( @ForAll("mergeSamples_exhaustive_len16") MergeInput<byte[]> sample ) { merges_exhaustive(sample); }
  private                            void merges_exhaustive      (                                          MergeInput<byte[]> sample ) {
    var tst = sample.array().clone();
    int mid = sample.mid(),
       from = sample.from(),
      until = sample.until();

    var ref = tst.clone();
    Arrays.sort(ref, from,until);

    var acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return Byte.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }

  @Property(tries=2_200_000) default void mergesStably_exhaustive_small( @ForAll("mergeSamples_exhaustive_small") MergeInput<byte[]> sample ) { mergesStabily_exhaustive(sample); }
  @Property(tries=2_200_000) default void mergesStably_exhaustive_len13( @ForAll("mergeSamples_exhaustive_len13") MergeInput<byte[]> sample ) { mergesStabily_exhaustive(sample); }
  @Property(tries=2_200_000) default void mergesStably_exhaustive_len14( @ForAll("mergeSamples_exhaustive_len14") MergeInput<byte[]> sample ) { mergesStabily_exhaustive(sample); }
  @Property(tries=2_200_000) default void mergesStably_exhaustive_len15( @ForAll("mergeSamples_exhaustive_len15") MergeInput<byte[]> sample ) { mergesStabily_exhaustive(sample); }
  @Property(tries=2_200_000) default void mergesStably_exhaustive_len16( @ForAll("mergeSamples_exhaustive_len16") MergeInput<byte[]> sample ) { mergesStabily_exhaustive(sample); }
  private                            void mergesStabily_exhaustive      (                                         MergeInput<byte[]> sample ) {
    var tst = sample.array().clone();
    int mid = sample.mid(),
       from = sample.from(),
      until = sample.until();

    for( int i=from; i < until; i++ )
      assertThat(tst[i]).isNotNegative();

    ComparatorByte cmp;
    if( isStable() )
    {
      for( int i=mid; i < until; i++ )
        tst[i] ^= -1;

      cmp = (x,y) -> {
        if( x < 0 ) x ^= -1;
        if( y < 0 ) y ^= -1;
        return Byte.compare(x,y);
      };
    }
    else cmp = Byte::compare;

    var ref = boxed(tst);
    Arrays.sort(ref, from,until, cmp::compare);

    var acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo( unboxed(ref) );
  }

  @Property(tries=N_TRIES)          default void mergesArrBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Boolean[]>> sample, @ForAll Comparator<  Boolean> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll Comparator<     Byte> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll Comparator<    Short> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll Comparator<  Integer> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll Comparator<     Long> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll Comparator<Character> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll Comparator<    Float> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll Comparator<   Double> cmp ) { mergesArr(sample,cmp); }
  private <T extends Comparable<? super T>> void mergesArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         Comparator<? super T> cmp )
  {
    var ref = sample.getData().getData().clone();
    var tst = ref.clone();
    int mid = sample.getIndex(),
       from = sample.getData().getFrom(),
      until = sample.getData().getUntil();

    Arrays.sort(ref,from,    until,cmp);
    Arrays.sort(tst,from,mid,      cmp);
    Arrays.sort(tst,     mid,until,cmp);

    var acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }


  @Property(tries=N_TRIES)          default void mergesStablyArrBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Boolean[]>> sample, @ForAll Comparator<  Boolean> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll Comparator<     Byte> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll Comparator<    Short> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll Comparator<  Integer> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll Comparator<     Long> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll Comparator<Character> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll Comparator<    Float> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          default void mergesStablyArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll Comparator<   Double> cmp ) { mergesStablyArr(sample,cmp); }
  private <T extends Comparable<? super T>> void mergesStablyArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         Comparator<? super T> comparator )
  {
    Comparator<Entry<T,Integer>> cmp =             comparingByKey(comparator);
    if( ! isStable() )     cmp = cmp.thenComparing(comparingByValue());

    var ref = zipWithIndex( sample.getData().getData() );
    var tst = ref.clone();

    int mid = sample.getIndex(),
       from = sample.getData().getFrom(),
      until = sample.getData().getUntil();

    Arrays.sort(ref,from,    until,cmp);
    Arrays.sort(tst,from,mid,      cmp);
    Arrays.sort(tst,     mid,until,cmp);

    var CMP = cmp;
    var acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }
}
