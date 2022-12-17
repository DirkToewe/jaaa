package com.github.jaaa.permute;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.permute.Swap.swap;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000_000 )
public class BitReversalPermuteAccessTest
{
  @Property void test_bitReversal( @ForAll @IntRange(min=0,max=25) int shift ) {
    int len = 1 << shift;

    int[] tst = range(0,len).toArray();
    BitReversalPermuteAccess acc = (i,j) -> swap(tst,i,j);
    acc.bitReversalPermute(0,len);

    int[] ref = range(0,len).map(x -> Integer.reverse(x) >>> 32-shift ).toArray();

    assertThat(tst).isEqualTo(ref);
  }

  @Property void test_bitReversal_shifted( @ForAll @IntRange(min=0,max=13) int shift, @ForAll @IntRange(min=0,max=1<<13) int from ) {
    int len = 1 << shift;

    int[] tst = range(0, from+len).toArray();
    BitReversalPermuteAccess acc = (i, j) -> {
      assertThat(i).isGreaterThanOrEqualTo(from);
      assertThat(j).isGreaterThanOrEqualTo(from);
      swap(tst,i,j);
    };
    acc.bitReversalPermute(from,from+len);

    int[] ref = range(0, from+len).map(x ->
      x < from ? x : from + (Integer.reverse(x-from) >>> 32-shift)
    ).toArray();

    assertThat(tst).isEqualTo(ref);
  }
}
