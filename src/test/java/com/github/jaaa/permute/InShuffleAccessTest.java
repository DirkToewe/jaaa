package com.github.jaaa.permute;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.stream.IntStream;

import static com.github.jaaa.permute.Swap.swap;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.jqwik.api.Arbitraries.integers;
import static org.assertj.core.api.Assertions.assertThat;


@Group
public class InShuffleAccessTest
{
  @PropertyDefaults( tries=10_000 )
  @Group class Large  extends Template { @Override int maxSize() { return 1_000_000; } }
  @Group class Medium extends Template { @Override int maxSize() { return     10_00; } }
  @Group class Small  extends Template { @Override int maxSize() { return       100; } }
  @PropertyDefaults( tries=1_000_000 )
  static abstract class Template
  {
    abstract int maxSize();

    @Provide Arbitrary<Tuple2<Integer,Integer>> arbitraryRange() {
      return integers().between(0, maxSize()).tuple2().map(
        ij -> Tuple.of(
          min(ij.get1(), ij.get2()),
          max(ij.get1(), ij.get2())
        )
      );
    }

    @Property void test_inShuffle( @ForAll("arbitraryRange") Tuple2<Integer,Integer> sample ) {
      int from = sample.get1(),
         until = sample.get2();

      var tst = IntStream.range(0,until).toArray();
      InShuffleAccess acc = (i, j) -> swap(tst,i,j);
      acc.inShuffle(from,until);

      var ref = IntStream.range(0,until).map( i -> {
        if( i < from )
          return i;
        int n = until - from + 1 >>> 1;
        i -= from;
        i = switch(i&1) {
          case 0 ->  i>>>1;
          case 1 -> (i>>>1) + n;
          default -> throw new AssertionError();
        };
        return i + from;
      }).toArray();

      assertThat(tst).isEqualTo(ref);
    }
    @Property void test_unShuffle( @ForAll("arbitraryRange") Tuple2<Integer,Integer> sample ) {
      int from = sample.get1(),
         until = sample.get2();

      var tst = IntStream.range(0,until).toArray();
      InShuffleAccess acc = (i,j) -> swap(tst,i,j);
      acc.unShuffle(from,until);

      var ref = IntStream.range(0,until).map( i -> {
        if( i < from )
          return i;
        int n = until - from + 1 >>> 1;
        i -= from;
        i = i < n ? i<<1 : (i-n << 1) + 1;
        return i + from;
      }).toArray();

      assertThat(tst).isEqualTo(ref);
    }
  }
}
