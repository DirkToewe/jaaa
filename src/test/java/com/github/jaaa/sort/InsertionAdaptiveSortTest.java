package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.sort.InsertionAdaptiveSort.INSERTION_ADAPTIVE_SORTER;
import static com.github.jaaa.util.IMath.log2Ceil;
import static java.lang.Math.max;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@Group
public class InsertionAdaptiveSortTest
{
  private final SorterInplace sorter = new StaticMethodsSorterInplace(InsertionAdaptiveSort.class) {
    @Override public boolean isStable    () { return INSERTION_ADAPTIVE_SORTER.isStable    (); }
    @Override public boolean isThreadSafe() { return INSERTION_ADAPTIVE_SORTER.isThreadSafe(); }
  };

  @Group class SortTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return sorter; }
  }
  @Group class SortTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return sorter; }
  }

  @Group class SorterTestSmall implements TestTemplate {
    @Override public int maxArraySize() { return 100; }
    @Override public SorterInplace sorter() { return INSERTION_ADAPTIVE_SORTER; }
  }
  @Group class SorterTestMedium implements TestTemplate {
    @Override public int maxArraySize() { return 10_000; }
    @Override public SorterInplace sorter() { return INSERTION_ADAPTIVE_SORTER; }
  }

  interface TestTemplate extends SorterInplaceTestTemplate
  {
    @Override default long nCompMax( int len ) { return len<=1 ? 0 : len-1 + range(1,len-1).mapToLong( n -> log2Ceil(n+1) ).sum(); }
    @Override default long nCompMin( int len ) { return len<=1 ? 0 : len-1; }
    @Override default long nSwapMax( int len ) { return len*(len-1L) / 2; }

    @Property default                         void sortsAdaptivelyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed) ); }
    @Property default                         void sortsAdaptivelyAccessWithRangeString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample ) { sortsAdaptivelyAccessWithRange( sample                    ); }
    private <T extends Comparable<? super T>> void sortsAdaptivelyAccessWithRange( WithRange<T[]> sample )
    {
      int  from = sample.getFrom(),
          until = sample.getUntil();
      var array = sample.getData();

      Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

      Tuple2<Integer,Integer>[] reference = range(0,array.length).mapToObj(i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(reference, from,until, cmp);
      var input = reference.clone();

      class CountAcc implements CompareSwapAccess {
        public long nSwaps = 0L,
                    nComps = 0L;
        @Override public int compare( int i, int j ) { nComps++; return cmp.compare(input[i], input[j]); }
        @Override public void   swap( int i, int j ) { nSwaps++; Swap.swap(input,i,j); }
      };

      var acc = new CountAcc() {};

      sorter().sort(from,until, acc);

      assertThat(input).isEqualTo(reference);
      assertThat(acc.nComps).isEqualTo( max(0, until-from-1) );
      assertThat(acc.nSwaps).isEqualTo(0);
    }
  }
}