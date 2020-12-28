package com.github.jaaa.partition;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;

import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class HexBiPartitionTest
{
  private static final int N_TRIES = Integer.MAX_VALUE;

  @Property( tries = N_TRIES )
  void biPartitionsArrayObject1( @ForAll @Size(max=16) Boolean[] input )
  {
    Boolean[]   reference = input.clone();
    Arrays.sort(reference);

    HexBiPartition.biPartition(input, x -> x);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void biPartitionsArrayObject2( @ForAll @Size(max=16) Boolean[] sample )
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, comparing(Tuple2::get1));
    HexBiPartition.biPartition(input,Tuple2::get1);

    assertThat(input).isEqualTo(reference);
  }
}
