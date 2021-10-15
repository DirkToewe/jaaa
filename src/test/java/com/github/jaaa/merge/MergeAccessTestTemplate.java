package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.misc.Concat.concat;
import static com.github.jaaa.util.ZipWithIndex.zipWithIndex;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static net.jqwik.api.Arbitraries.bytes;
import static net.jqwik.api.Combinators.combine;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( shrinking=ShrinkingMode.FULL )
public abstract class MergeAccessTestTemplate
{
  // STATIC FIELDS
  private static final int N_TRIES = 100_000,
                          MAX_SIZE = 8192;

  abstract protected boolean isStable();

  public interface MergeAccess
  {
    void merge( int from, int mid, int until );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected MergeAccess createAccess( CompareSwapAccess srtAcc );
//  abstract protected long comparisonLimit( int len, int i );

  @Provide Arbitrary<WithInsertIndex<WithRange<Byte[]>>> mergeSamples_limitedRanges(
    @ForAll  @IntRange(min=0, max=MAX_SIZE/2) int lenL,
    @ForAll  @IntRange(min=0, max=MAX_SIZE/2) int lenR,
    @ForAll byte rangeL,
    @ForAll byte rangeR
  ) {
    return combine(
      bytes().between(Byte.MIN_VALUE, rangeL).array(byte[].class).ofSize(lenL),
      bytes().between(Byte.MIN_VALUE, rangeR).array(byte[].class).ofSize(lenR)
    ).as( (l,r) ->
      new WithInsertIndex<>(lenL,
      new WithRange<>(0, lenL+lenR, boxed(concat(l,r)) ))
    ).withoutEdgeCases(); // <- FIXME: should not necessary
  }

  @Property(tries=N_TRIES) void mergesArr_limitedRanges(
    @ForAll("mergeSamples_limitedRanges") WithInsertIndex<WithRange<Byte[]>> sample, @ForAll Comparator<Byte> cmp
  ) { mergesArr(sample,cmp); }

  @Property(tries=N_TRIES) void mergesStablyArr_limitedRanges(
    @ForAll("mergeSamples_limitedRanges") WithInsertIndex<WithRange<Byte[]>> sample, @ForAll Comparator<Byte> cmp
  ) { mergesStablyArr(sample,cmp); }

  @Property(tries=N_TRIES)          void mergesArrBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Boolean[]>> sample, @ForAll Comparator<  Boolean> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll Comparator<     Byte> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll Comparator<    Short> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll Comparator<  Integer> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll Comparator<     Long> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll Comparator<Character> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll Comparator<    Float> cmp ) { mergesArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll Comparator<   Double> cmp ) { mergesArr(sample,cmp); }
  <T extends Comparable<? super T>> void mergesArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         Comparator<? super T> cmp )
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


  @Property(tries=N_TRIES)          void mergesStablyArrBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Boolean[]>> sample, @ForAll Comparator<  Boolean> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll Comparator<     Byte> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll Comparator<    Short> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll Comparator<  Integer> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll Comparator<     Long> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll Comparator<Character> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll Comparator<    Float> cmp ) { mergesStablyArr(sample,cmp); }
  @Property(tries=N_TRIES)          void mergesStablyArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll Comparator<   Double> cmp ) { mergesStablyArr(sample,cmp); }
  <T extends Comparable<? super T>> void mergesStablyArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         Comparator<? super T> comparator )
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
