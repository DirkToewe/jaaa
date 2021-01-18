package com.github.jaaa.sort.tiny;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.WithRange;
import com.github.jaaa.Swap;
import com.github.jaaa.sort.SorterInplace;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public interface SorterInplaceTest8Template extends SorterTest8Template
{
// STATIC FIELDS
  static final int MAX_SIZE = 8;

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override SorterInplace sorter();

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeBoolean8( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sample, @ForAll boolean reversed )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    boolean[] array = sample.getData();

    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeByte8( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    byte[] array = sample.getData();

    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )             cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeInt8( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] array = sample.getData();

    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithOffsetBoolean8(
    @ForAll @Size(min=0, max=MAX_SIZE) boolean[] array,
    @ForAll @IntRange( max = Integer.MAX_VALUE - MAX_SIZE ) int offset
  )
  {
    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithOffsetByte8(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] array,
    @ForAll @IntRange( max = Integer.MAX_VALUE - MAX_SIZE ) int offset
  )
  {
    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithOffsetInt8(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] array,
    @ForAll @IntRange( max = Integer.MAX_VALUE - MAX_SIZE ) int offset
  )
  {
    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }
}
