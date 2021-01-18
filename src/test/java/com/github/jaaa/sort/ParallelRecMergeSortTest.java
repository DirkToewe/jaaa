package com.github.jaaa.sort;

import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;
import net.jqwik.api.Tuple;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.Tuple.Tuple2;
import static org.assertj.core.api.Assertions.assertThat;

public class ParallelRecMergeSortTest
{
  private static final int N_TRIES = 10_000,
                          MAX_SIZE = 64*1024;

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] sample )
  {
    Byte[]      backup = sample.clone();
    Arrays.sort(backup);
    ParallelRecMergeSort.sort(sample);

    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] sample )
  {
    Integer[]   backup = sample.clone();
    Arrays.sort(backup);
    ParallelRecMergeSort.sort(sample);

    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysComparableString( @ForAll @Size(min=0, max=MAX_SIZE) String[] sample )
  {
    String[]    backup = sample.clone();
    Arrays.sort(backup);
    ParallelRecMergeSort.sort(sample);

    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyArraysTupleBoolean( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample )
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, cmp);
    ParallelRecMergeSort.sort(input,     cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyTupleArraysTupleByte( @ForAll @Size(min=0, max=MAX_SIZE)  byte[] sample )
  {
    Tuple2<Byte,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>>    cmp = comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, cmp);
    ParallelRecMergeSort.sort(input,     cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyTupleArraysTupleInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] sample )
  {
    Tuple2<Integer,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, cmp);
    ParallelRecMergeSort.sort(input,     cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysWithRangeComparableByte(@ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> sample )
  {
    int     from = sample.getFrom(),
            until = sample.getUntil();
    Byte[] input = sample.getData(),
            backup = input.clone();

    Arrays.sort(backup, from,until);
    ParallelRecMergeSort.sort(input, from,until);

    assertThat(input).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysWithRangeComparableInteger(@ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> sample )
  {
    int        from = sample.getFrom(),
            until = sample.getUntil();
    Integer[] input = sample.getData(),
            backup = input.clone();

    Arrays.sort(backup, from,until);
    ParallelRecMergeSort.sort(input, from,until);

    assertThat(input).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsArraysComparableString(@ForAll WithRange<@Size(min=0, max=MAX_SIZE) String[]> sample )
  {
    int       from = sample.getFrom(),
            until = sample.getUntil();
    String[] input = sample.getData(),
            backup = input.clone();

    Arrays.sort(backup, from,until);
    ParallelRecMergeSort.sort(input, from,until);

    assertThat(input).isEqualTo(backup);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyArraysWithRangeTupleBoolean( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sampleWithRange )
  {
    int         from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    boolean[] sample = sampleWithRange.getData();

    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>>             cmp  =  comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, from,until, cmp);
    ParallelRecMergeSort.sort(input,     from,until, cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyArraysWithRangeTupleByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sampleWithRange )
  {
    int      from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    byte[] sample = sampleWithRange.getData();

    Tuple2<Byte,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>>                cmp  =  comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, from,until, cmp);
    ParallelRecMergeSort.sort(input, from,until, cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyArraysWithRangeTupleInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sampleWithRange )
  {
    int     from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    int[] sample = sampleWithRange.getData();

    Tuple2<Integer,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>>             cmp = comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, from,until, cmp);
    ParallelRecMergeSort.sort(input,     from,until, cmp);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void sortsStablyArraysWithRangeTupleString( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) String[]> sampleWithRange )
  {
    int        from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    String[] sample = sampleWithRange.getData();

    Tuple2<String,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<String,Integer>>              cmp = comparing(Tuple2::get1);
    cmp = cmp.thenComparing(Tuple2::get2);
    Arrays.sort(reference, from,until, cmp);
    ParallelRecMergeSort.sort(input,     from,until, cmp);

    assertThat(input).isEqualTo(reference);
  }
}
