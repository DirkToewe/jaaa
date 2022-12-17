package com.github.jaaa.permute;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.permute.Swap.swap;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000_000 )
public class DigitReversalPermuteAccessTest
{
  @Property void test_digitReversal( @ForAll @IntRange(min=2,max=8) int radix, @ForAll @IntRange(min=0,max=8) int exp ) {
    int len = (int) Math.pow(radix,exp);

    int[] tst = range(0,len).toArray();
    DigitReversalPermuteAccess acc = (i, j) -> swap(tst,i,j);
    acc.digitReversalPermute(radix, 0, len);

    int[] ref = range(0,len).map(x -> {
      int y = 0;
      for( int i=0; i++ < exp; ) {
        y *= radix;
        y += x % radix;
        x /= radix;
      }
      return y;
    }).toArray();

    assertThat(tst).isEqualTo(ref);
  }

  @Property void test_digitReversal( @ForAll @IntRange(min=2,max=6) int radix, @ForAll @IntRange(min=0,max=6) int exp, @ForAll @IntRange(min=0, max=1_000) int from ) {
    int len = (int) Math.pow(radix,exp);

    int[] tst = range(0, from+len).toArray();
    DigitReversalPermuteAccess acc = (i,j) -> {
      assertThat(i).isGreaterThanOrEqualTo(from);
      assertThat(j).isGreaterThanOrEqualTo(from);
      swap(tst,i,j);
    };
    acc.digitReversalPermute(radix, from, from+len);

    int[] ref = range(0, from+len).map(x -> {
      if( x < from )
        return x;
      x -= from;
      int y = 0;
      for( int i=0; i++ < exp; ) {
        y *= radix;
        y += x % radix;
        x /= radix;
      }
      return y + from;
    }).toArray();

    assertThat(tst).isEqualTo(ref);
  }
}
