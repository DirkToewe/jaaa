package com.github.jaaa.sort.tiny;

import com.github.jaaa.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.CompareSwapAccess;
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

public abstract class TinySorterTestTemplate
{
  // STATIC FIELDS
  static final int N_TRIES = 100_000,
                  MAX_SIZE = 8;

// STATIC CONSTRUCTOR

// STATIC METHODS

  // FIELDS
  private final SorterInplace sorter;

  // CONSTRUCTORS
  public TinySorterTestTemplate( SorterInplace _sorter )
  {
    sorter =_sorter;
  }

  // METHODS
  @Property( tries = N_TRIES )
  void sortsArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] seq )
  {
    byte[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysShort( @ForAll @Size(min=0, max=MAX_SIZE) short[] seq )
  {
    short[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] seq )
  {
    int[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysLong( @ForAll @Size(min=0, max=MAX_SIZE) long[] seq )
  {
    long[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysChar( @ForAll @Size(min=0, max=MAX_SIZE) char[] seq )
  {
    char[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysFloat( @ForAll @Size(min=0, max=MAX_SIZE) float[] seq )
  {
    float[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysDouble( @ForAll @Size(min=0, max=MAX_SIZE) double[] seq )
  {
    double[] backup = seq.clone();
    Arrays.sort(backup);
    sorter.sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsArraysComparableString( @ForAll @Size(min=0, max=MAX_SIZE) String[] sample )
  {
    String[] backup = sample.clone();
    Arrays.sort(backup);
    sorter.sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysBoolean( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample )
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter.sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] sample )
  {
    Tuple2<Byte,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter.sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] sample )
  {
    Tuple2<Integer,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter.sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }




  @Property( tries = N_TRIES )
  void sortsArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    byte[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeShort( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) short[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    short[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    int[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeLong( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) long[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    long[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeChar( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) char[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    char[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeFloat( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) float[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    float[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsArraysWithRangeDouble( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) double[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    double[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter.sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysWithRangeBoolean( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    boolean[] array = sample.getData();


    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);
    sorter.sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    byte[] array = sample.getData();

    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);
    sorter.sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyTupleArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    int[] array = sample.getData();


    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);
    sorter.sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void sortsStablyAccessWithRangeBoolean( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    boolean[] array = sample.getData();

    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter.sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyAccessWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    byte[] array = sample.getData();

    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter.sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyAccessWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    int[] array = sample.getData();

    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter.sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void sortsStablyAccessWithOffsetBoolean(
    @ForAll @Size(min=0, max=MAX_SIZE) boolean[] array,
    @ForAll @IntRange(
      max = Integer.MAX_VALUE - MAX_SIZE
    ) int offset
  )
  {
    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter.sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyAccessWithOffsetByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] array,
    @ForAll @IntRange(
      max = Integer.MAX_VALUE - MAX_SIZE
    ) int offset
  )
  {
    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter.sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void sortsStablyAccessWithOffsetInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] array,
    @ForAll @IntRange(
      max = Integer.MAX_VALUE - MAX_SIZE
    ) int offset
  )
  {
    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter.isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter.sort(offset,array.length+offset, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i-offset], input[j-offset]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }
}
