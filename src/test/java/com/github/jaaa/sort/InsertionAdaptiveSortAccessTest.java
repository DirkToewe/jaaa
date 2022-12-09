package com.github.jaaa.sort;

import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.WithRange;
import com.github.jaaa.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;

import java.util.Arrays;
import java.util.Comparator;

import static java.lang.Math.max;
import static org.assertj.core.api.Assertions.assertThat;


@Group
public class InsertionAdaptiveSortAccessTest
{
  private record Acc<T>( CompareRandomAccessor<T> acc ) implements SortAccessorTestTemplate.SortAccessor<T>
  {
    @Override public void sort( T arr, int from, int until) {
      new InsertionAdaptiveSortAccess() {
        @Override public void   swap( int i, int j ) {        acc.   swap(arr,i, arr,j); }
        @Override public int compare( int i, int j ) { return acc.compare(arr,i, arr,j); }
      }.insertionAdaptiveSort(from,until);
    }
  }

  @Group class SorterTestMedium      implements TestTemplate { @Override public int maxArraySize() {return    10_000;} }
  @Group class SortAccessorTestSmall implements TestTemplate { @Override public int maxArraySize() {return       100;} }

  private interface TestTemplate extends SortAccessorTestTemplate {
    @Override default <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> acc ) { return new Acc<>(acc); }
    @Override default boolean isStable() { return false; }

    @Property default void sortsAdaptivelyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll Comparator<  Boolean> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll Comparator<     Byte> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll Comparator<    Short> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll Comparator<  Integer> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll Comparator<     Long> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll Comparator<Character> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll Comparator<    Float> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll Comparator<   Double> cmp ) { sortsAdaptivelyAccessWithRange( sample.map(Boxing::boxed), cmp ); }
    @Property default void sortsAdaptivelyAccessWithRangeString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll Comparator<   String> cmp ) { sortsAdaptivelyAccessWithRange( sample                   , cmp ); }
    private       <T> void sortsAdaptivelyAccessWithRange( WithRange<T[]> sample, Comparator<? super T> cmp )
    {
      int  from = sample.getFrom(),
          until = sample.getUntil();
      var input = sample.getData();
      Arrays.sort(input, from,until, cmp);
      var reference = input.clone();

      class CountAcc implements CompareRandomAccessor<T[]> {
        public long nComps = 0L;
        @Override public  T[] malloc( int len )                    { throw new AssertionError("Insertion*Sort should happen in-place."); }
        @Override public void   copy( T[] a, int i, T[] b, int j ) { throw new AssertionError("Insertion*Sort should happen in-place."); }
        @Override public void   swap( T[] a, int i, T[] b, int j ) { throw new AssertionError("Adaptive sorting should not require swaps on sorted input."); }
        @Override public int compare( T[] a, int i, T[] b, int j ) {
          ++nComps;
          assertThat(a).isSameAs(b);
          return cmp.compare(a[i], b[j]);
        }
      }

      var acc = new CountAcc();

      createAccessor(acc).sort(input, from,until);

      assertThat(input).isEqualTo(reference);
      assertThat(acc.nComps).isEqualTo( max(0, until-from-1) );
    }
  }
}
