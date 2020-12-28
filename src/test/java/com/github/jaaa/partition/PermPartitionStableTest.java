package com.github.jaaa.partition;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;

import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class PermPartitionStableTest
{
  @Provide
  Arbitrary<Tuple2<int[], Integer>> shuffledKeys()
  {
    return Arbitraries.integers().between(1,     16)               .flatMap( nKeys ->
           Arbitraries.integers().between(0,nKeys-1).array(int[].class).map(  keys -> Tuple.of(keys,nKeys) ));
  }

  @Property( tries = 1_000_000 )
  void biPartitionsArrayObject2( @ForAll("shuffledKeys") Tuple2<int[],Integer> sample )
  {
    int[] keys = sample.get1();
    int  nKeys = sample.get2();

    Tuple2<Integer,Integer>[] input = range(0,keys.length).mapToObj(i -> Tuple.of(keys[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, comparing(Tuple2::get1));

    PermPartitionStable.partition(input, nKeys, Tuple2::get1);

    assertThat(input).isEqualTo(reference);
  }
}
