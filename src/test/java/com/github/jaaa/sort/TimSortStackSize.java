package com.github.jaaa.sort;

import java.util.Spliterators;
import java.util.function.IntConsumer;

import static java.lang.Math.addExact;
import static java.lang.Math.min;
import static java.util.Spliterator.*;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.intStream;


public class TimSortStackSize
{
  public static void main( String... args )
  {
    int[] sums_v1 = intStream(
      new Spliterators.AbstractIntSpliterator(Long.MAX_VALUE, DISTINCT|IMMUTABLE|NONNULL|ORDERED)
      {
        private int sum = 0;
        private long  a = 0,
                      b = 16;
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

    range(0,sums_v1.length).forEach( i -> System.out.printf("%2d ->%10d\n", i, sums_v1[i]) );
  }
}
