package com.github.jaaa.merge;

import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.misc.Concat.concat;
import static net.jqwik.api.Arbitraries.bytes;
import static net.jqwik.api.Combinators.combine;


public interface MergeDataProviderTemplate
{
  int MAX_SIZE = 8192;

  @Provide default Arbitrary<WithInsertIndex<WithRange<Byte[]>>> mergeSamples_limitedRanges(
    @ForAll @IntRange(min=0, max=MAX_SIZE/2) int lenL,
    @ForAll @IntRange(min=0, max=MAX_SIZE/2) int lenR,
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

  @Provide default Arbitrary<MergeInput<byte[]>> mergeSamples_exhaustive_small() { return Arbitraries.of( new MergeInputsUpToLength(12) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeSamples_exhaustive_len13() { return Arbitraries.of( new MergeInputsOfLength(13) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeSamples_exhaustive_len14() { return Arbitraries.of( new MergeInputsOfLength(14) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeSamples_exhaustive_len15() { return Arbitraries.of( new MergeInputsOfLength(15) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeSamples_exhaustive_len16() { return Arbitraries.of( new MergeInputsOfLength(16) ); }
}
