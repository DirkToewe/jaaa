package com.github.jaaa.merge;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.Tuple.Tuple2;
import static org.assertj.core.api.Assertions.assertThat;

public class HwangLinMergeV1Test
{
  private static final int N_TRIES = 100_000;

  @Property( tries = N_TRIES )
  void mergesArraysObjComparatorBoolean( @ForAll Boolean[] A, @ForAll Boolean[] B, @ForAll boolean reversed )
  {
    Comparator<Boolean>  cmp = naturalOrder();
    if( reversed ) cmp = cmp.reversed();
    Arrays.sort(A, cmp);
    Arrays.sort(B, cmp);

    Boolean[] C = Stream.concat(
      stream(A),
      stream(B)
    ).sorted(cmp).toArray(Boolean[]::new);

    Boolean[] a = A.clone(),
              b = B.clone(),
              c = new Boolean[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }

  @Property( tries = N_TRIES )
  void mergesArraysObjComparatorByte( @ForAll Byte[] A, @ForAll Byte[] B, @ForAll boolean reversed )
  {
    Comparator<Byte>     cmp = naturalOrder();
    if( reversed ) cmp = cmp.reversed();
    Arrays.sort(A, cmp);
    Arrays.sort(B, cmp);

    Byte[] C = Stream.concat(
      stream(A),
      stream(B)
    ).sorted(cmp).toArray(Byte[]::new);

    Byte[] a = A.clone(),
           b = B.clone(),
           c = new Byte[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }

  @Property( tries = N_TRIES )
  void mergesArraysObjComparatorInteger( @ForAll Integer[] A, @ForAll Integer[] B, @ForAll boolean reversed )
  {
    Comparator<Integer>  cmp = naturalOrder();
    if( reversed ) cmp = cmp.reversed();
    Arrays.sort(A, cmp);
    Arrays.sort(B, cmp);

    Integer[] C = Stream.concat(
      stream(A),
      stream(B)
    ).sorted(cmp).toArray(Integer[]::new);

    Integer[] a = A.clone(),
              b = B.clone(),
              c = new Integer[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }

  @Property( tries = N_TRIES )
  void mergesStablyTupleComparatorBoolean( @ForAll boolean[] rawA, @ForAll boolean[] rawB, @ForAll boolean reversed )
  {
    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )
      cmp = cmp.reversed();

    Tuple2<Boolean,Integer>[]
      A = range(0,rawA.length).mapToObj( i -> Tuple.of(rawA[i],i            ) ).sorted(cmp).toArray(Tuple2[]::new),
      B = range(0,rawB.length).mapToObj( i -> Tuple.of(rawB[i],i+rawA.length) ).sorted(cmp).toArray(Tuple2[]::new),
      C =                                 Stream.concat( stream(A), stream(B) ).sorted(cmp).toArray(Tuple2[]::new),
      a = A.clone(),
      b = B.clone(),
      c = new Tuple2[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }

  @Property( tries = N_TRIES )
  void mergesStablyTupleComparatorByte( @ForAll byte[] rawA, @ForAll byte[] rawB, @ForAll boolean reversed )
  {
    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )
      cmp = cmp.reversed();

    Tuple2<Byte,Integer>[]
      A = range(0,rawA.length).mapToObj( i -> Tuple.of(rawA[i],i            ) ).sorted(cmp).toArray(Tuple2[]::new),
      B = range(0,rawB.length).mapToObj( i -> Tuple.of(rawB[i],i+rawA.length) ).sorted(cmp).toArray(Tuple2[]::new),
      C =                                 Stream.concat( stream(A), stream(B) ).sorted(cmp).toArray(Tuple2[]::new),
      a = A.clone(),
      b = B.clone(),
      c = new Tuple2[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }

  @Property( tries = N_TRIES )
  void mergesStablyTupleComparatorInteger( @ForAll int[] rawA, @ForAll int[] rawB, @ForAll boolean reversed )
  {
    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )
      cmp = cmp.reversed();

    Tuple2<Integer,Integer>[]
      A = range(0,rawA.length).mapToObj( i -> Tuple.of(rawA[i],i            ) ).sorted(cmp).toArray(Tuple2[]::new),
      B = range(0,rawB.length).mapToObj( i -> Tuple.of(rawB[i],i+rawA.length) ).sorted(cmp).toArray(Tuple2[]::new),
      C =                                 Stream.concat( stream(A), stream(B) ).sorted(cmp).toArray(Tuple2[]::new),
      a = A.clone(),
      b = B.clone(),
      c = new Tuple2[A.length + B.length];

    HwangLinMergeV1.merge(
      a,0,a.length,
      b,0,b.length,
      c,0, cmp
    );

    assertThat(a).isEqualTo(A);
    assertThat(b).isEqualTo(B);
    assertThat(c).isEqualTo(C);
  }
}
