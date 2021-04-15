package com.github.jaaa.sort;

import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Positive;

import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static com.github.jaaa.sort.TimSort.TIM_SORTER;
import static java.lang.Math.addExact;
import static java.lang.Math.min;
import static java.util.Spliterator.*;
import static java.util.Spliterator.ORDERED;
import static java.util.stream.StreamSupport.intStream;
import static org.assertj.core.api.Assertions.assertThat;
import static java.lang.Integer.numberOfLeadingZeros;


public class TimSortTest
{
  @Property( tries = 1_000_000 )
//  @Property( tries = Integer.MAX_VALUE )
  void timSort_minRunLength( @ForAll @Positive int n ) {
    new Object() {
      private static final int MIN_MERGE = 32;

      private int minRunLenJDK( int n ) {
        assert n >= 0;
        int r = 0; // Becomes 1 if any 1 bits are shifted off
        while (n >= MIN_MERGE) {
          r |= (n & 1);
          n >>= 1;
        }
        return n + r;
      }

      private int minRunLenJaaaV1( int n )
      {
        if( n < MIN_MERGE )
          return n;
        int s = 27 - numberOfLeadingZeros(n),
            l = n>>>s;
        if( l<<s != n )
          ++l;
        return l;
      }

      private int minRunLenJaaaV2( int n )
      {
        assert 0 <= n;
        int     s = 0, RUN_LEN = 16;
        if( n>>> 16 >= RUN_LEN ) s += 16;
        if( n>>>s+8 >= RUN_LEN ) s +=  8;
        if( n>>>s+4 >= RUN_LEN ) s +=  4;
        if( n>>>s+2 >= RUN_LEN ) s +=  2;
        if( n>>>s+1 >= RUN_LEN ) s +=  1;
        int    l = n>>>s;
        return l<<s != n ? l+1 : l;
      }

      {
        assertThat( minRunLenJaaaV1(n) ).isEqualTo( minRunLenJDK(n) );
        assertThat( minRunLenJaaaV2(n) ).isEqualTo( minRunLenJDK(n) );
      }
    };
  }

  @Property( tries=Integer.MAX_VALUE )
  void timSort_stackLengths( @ForAll @Positive @IntRange(min=0, max=1_000_000) int MIN_RUN_LEN )
  {
    int[] arrLens1; {
      int sum = MIN_RUN_LEN;

      long a = 0,
           b = sum;

      var builder = IntStream.builder();
      builder.add(0);
      builder.add(sum);

      for(;;) {
        var c = addExact(1+a,b);
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
      private static final int MIN_MERGE = 32;

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
  @PropertyDefaults( tries = 1_000 )
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
  @PropertyDefaults( tries = 1_000 )
  @Group class SorterTestLarge implements SorterTestTemplate {
    @Override public int maxArraySize() { return 1_000_000; }
    @Override public Sorter sorter() { return TIM_SORTER; }
  }
}
