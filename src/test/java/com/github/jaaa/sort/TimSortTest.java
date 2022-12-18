package com.github.jaaa.sort;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Positive;

import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import static java.util.stream.IntStream.rangeClosed;

import static com.github.jaaa.sort.TimSort.TIM_SORTER;
import static java.lang.Math.addExact;
import static java.lang.Math.min;
import static java.util.Spliterator.*;
import static java.util.stream.StreamSupport.intStream;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.jaaa.sort.BenchmarkTimSort_optimalRunLength.*;

public class TimSortTest
{
  @Example void timSort_minRunLength()
  {
    rangeClosed(0,Integer.MAX_VALUE).parallel().forEach( n -> {
      int                                                  runLen = minRunLenJDK(n);
      assertThat(            minRunLenJaaaV1(n)).isEqualTo(runLen);
      assertThat(            minRunLenJaaaV2(n)).isEqualTo(runLen);
      assertThat(            minRunLenJaaaV3(n)).isEqualTo(runLen);
      assertThat(TimSort.optimalRunLength(16,n)).isEqualTo(runLen);
    });
  }

  @Property( tries=Integer.MAX_VALUE )
  void timSort_stackLengths( @ForAll @Positive @IntRange(min=0, max=1_000_000) int MIN_RUN_LEN )
  {
    int[] arrLens1; {
      int sum = MIN_RUN_LEN;

      long a = 0,
           b = sum;

      IntStream.Builder builder = IntStream.builder();
      builder.add(0);
      builder.add(sum);

      for(;;) {
        long c = addExact(1+a,b);
        sum = (int) min( addExact(sum,c), Integer.MAX_VALUE );
        builder.add(sum);

        if( sum >= Integer.MAX_VALUE )
          break;

        a=b;
        b=c;
      }

      arrLens1 = builder.build().toArray();
    }

    final int[] arrLens2 = intStream(
      new Spliterators.AbstractIntSpliterator(Long.MAX_VALUE, DISTINCT|IMMUTABLE|NONNULL|ORDERED)
      {
        private int sum = 0;
        private long  a = 0,
                      b = MIN_RUN_LEN;
        @Override public boolean tryAdvance( IntConsumer action ) {
          if( sum >= Integer.MAX_VALUE )
            return false;
          sum = (int) min( Integer.MAX_VALUE, addExact(sum,a) );
          action.accept(sum);
          b = addExact(1+a, a=b);
          return true;
        }
      }, /*parallel=*/false
    ).toArray();

    assertThat(arrLens1).isEqualTo(arrLens2);
  }

  @Property( tries = 10_000_000 )
  void timSort_ensureCapacity( @ForAll @Positive int minCapacity, @ForAll @Positive int length ) {
    new Object() {
      private int ensureCapacityJDK( int minCapacity, int length )
      {
        // Compute smallest power of 2 > minCapacity
        int newSize = -1 >>> Integer.numberOfLeadingZeros(minCapacity);
        newSize++;

        if (newSize < 0) // Not bloody likely!
          newSize = length>>>1;
        else
          newSize = min(newSize, length >>> 1);

        return newSize;
      }

      private int ensureCapacityJaaa( int minCapacity, int length )
      {
        int bufLen = 1 << 31;
        if( bufLen>>>16 > minCapacity ) bufLen >>>= 16;
        if( bufLen>>> 8 > minCapacity ) bufLen >>>=  8;
        if( bufLen>>> 4 > minCapacity ) bufLen >>>=  4;
        if( bufLen>>> 2 > minCapacity ) bufLen >>>=  2;
        if( bufLen>>> 1 > minCapacity ) bufLen >>>=  1;
        assert Integer.bitCount(bufLen) == 1;
        assert Integer.compareUnsigned(bufLen,minCapacity) > 0;

        if( bufLen > length>>>1 || bufLen < 0 )
            bufLen = length>>>1;

        return bufLen;
      }

      {
        assertThat( ensureCapacityJaaa(minCapacity,length) ).isEqualTo( ensureCapacityJDK(minCapacity,length) );
      }
    };
  }

  private final Sorter sorter = new StaticMethodsSorter(TimSort.class) {
    @Override public boolean isStable    () { return TIM_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return TIM_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return sorter; }
  }
  @Group class SortTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return sorter; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SortTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements SorterTestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public Sorter sorter() { return TIM_SORTER; }
  }
  @Group class SorterTestMedium implements SorterTestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public Sorter sorter() { return TIM_SORTER; }
  }
  @PropertyDefaults( tries = 100 )
  @Group class SorterTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return TIM_SORTER; }
  }
}
