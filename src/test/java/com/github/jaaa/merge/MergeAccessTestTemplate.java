package com.github.jaaa.merge;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.IntStream.range;
import static java.util.Comparator.comparing;


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

  @Property(tries=N_TRIES)          void mergeArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll boolean reversed ) { mergeArr(sample,reversed); }
  <T extends Comparable<? super T>> void mergeArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         boolean reversed )
  {
    Comparator<T> cmp = naturalOrder();
    if(reversed)  cmp = cmp.reversed();

    var ref = sample.getData().getData().clone();
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

  @Property(tries=N_TRIES)          void mergeStablyArrByte     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Byte[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrShort    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Short[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrInteger  ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)   Integer[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrLong     ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)      Long[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrCharacter( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE) Character[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrFloat    ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)     Float[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  @Property(tries=N_TRIES)          void mergeStablyArrDouble   ( @ForAll WithInsertIndex<WithRange<@Size(min=0, max=MAX_SIZE)    Double[]>> sample, @ForAll boolean reversed ) { mergeStablyArr(sample,reversed); }
  <T extends Comparable<? super T>> void mergeStablyArr         (         WithInsertIndex<WithRange<                                   T[]>> sample,         boolean reversed )
  {
    Comparator<Tuple2<T,Integer>> cmp =   comparing(Tuple2::get1);
    if( ! isStable() )      cmp = cmp.thenComparing(Tuple2::get2);
    if(   reversed   )      cmp = cmp.reversed();

    var raw = sample.getData().getData();
    Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
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
