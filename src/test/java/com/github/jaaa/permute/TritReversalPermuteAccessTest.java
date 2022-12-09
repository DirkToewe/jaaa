package com.github.jaaa.permute;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.permute.Swap.swap;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000_000 )
public class TritReversalPermuteAccessTest
{
  @Property void test_tritReversal( @ForAll @IntRange(min=0,max=16) int exp ) {
    int len = (int) Math.pow(3,exp);

    var tst = range(0,len).toArray();
    TritReversalPermuteAccess acc = (i, j) -> swap(tst,i,j);
    acc.tritReversalPermute(0,len);

    var ref = range(0,len).map( x -> {
      int y = 0;
      for( int i=0; i++ < exp; ) {
        y *= 3;
        y += x % 3;
        x /= 3;
      }
      return y;
    }).toArray();

    assertThat(tst).isEqualTo(ref);
  }

  @Property void test_tritReversal_shifted( @ForAll @IntRange(min=0,max=8) int exp, @ForAll @IntRange(min=0, max =8192) int from ) {
    int len = (int) Math.pow(3,exp);

    var tst = range(0, from+len).toArray();
    TritReversalPermuteAccess acc = (i,j) -> {
      assertThat(i).isGreaterThanOrEqualTo(from);
      assertThat(j).isGreaterThanOrEqualTo(from);
      swap(tst,i,j);
    };
    acc.tritReversalPermute(from,from+len);

    var ref = range(0, from+len).map( x -> {
      if( x < from )
        return x;
      x -= from;
      int y = 0;
      for( int i=0; i++ < exp; ) {
        y *= 3;
        y += x % 3;
        x /= 3;
      }
      return y + from;
    }).toArray();

    assertThat(tst).isEqualTo(ref);
  }
}
