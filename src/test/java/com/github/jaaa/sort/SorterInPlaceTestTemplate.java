package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.util.ZipWithIndex;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.github.jaaa.util.ZipWithIndex.zipWithIndex;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000 )
public interface SorterInPlaceTestTemplate extends SorterTestTemplate
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override
  SorterInPlace sorter();


  default long nSwapMax( int len ) { return Long.MAX_VALUE; }
  default long nSwapMin( int len ) { return 0; }


  abstract class CountingAccess implements CompareSwapAccess {
    public long nComps,
                nSwaps;
  }


  @Example
  @Override default void usesCorrectTestTemplate() {
    assertThat( sorter() ).isInstanceOf( SorterInPlace.class );
  }

  @Property default void sortsStablyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll Comparator<Short    > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll Comparator<Long     > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll Comparator<Character> cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll Comparator<Float    > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  @Property default void sortsStablyAccessWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll Comparator<Double   > cmp ) { sortsStablyAccessWithRange(sample.map(ZipWithIndex::zipWithIndex), cmp); }
  default <T>       void sortsStablyAccessWithRange( WithRange<Entry<T,Integer>[]> sample, Comparator<? super T> comparator )
  {
    int from = sample.getFrom(),
       until = sample.getUntil();
    Entry<T,Integer>[] input = sample.getData(),
                   reference = input.clone();

    Comparator<Entry<T,Integer>> cmp = comparingByKey(comparator);
    if( ! sorter().isStable() )  cmp = cmp.thenComparing(comparingByValue());

    Arrays.sort(reference, from, until, cmp);

    Comparator<Entry<T,Integer>> CMP = cmp;
    CountingAccess acc = new CountingAccess() {
      @Override public int compare( int i, int j ) { ++nComps; return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(input,i,j); }
    };

    sorter().sort(from,until, acc);

    assertThat(input).isEqualTo(reference);

    int n = until-from;
    assertThat(acc.nComps).isBetween( nCompMin(n), nCompMax(n) );
    assertThat(acc.nSwaps).isBetween( nSwapMin(n), nSwapMax(n) );
  }



  @Property default                         void sortsStablyAccessBoolean( @ForAll("arraysBoolean") boolean[] array, @ForAll Comparator<Boolean  > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessByte   ( @ForAll("arraysByte"   )    byte[] array, @ForAll Comparator<Byte     > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessShort  ( @ForAll("arraysShort"  )   short[] array, @ForAll Comparator<Short    > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessInt    ( @ForAll("arraysInt"    )     int[] array, @ForAll Comparator<Integer  > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessLong   ( @ForAll("arraysLong"   )    long[] array, @ForAll Comparator<Long     > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessChar   ( @ForAll("arraysChar"   )    char[] array, @ForAll Comparator<Character> cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessFloat  ( @ForAll("arraysFloat"  )   float[] array, @ForAll Comparator<Float    > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  @Property default                         void sortsStablyAccessDouble ( @ForAll("arraysDouble" )  double[] array, @ForAll Comparator<Double   > cmp ) { sortsStablyAccess( zipWithIndex(array), cmp ); }
  default <T extends Comparable<? super T>> void sortsStablyAccess( Entry<T,Integer>[] input, Comparator<? super T> comparator )
  {
    Entry<T,Integer>[] reference = input.clone();

    Comparator<Entry<T,Integer>> cmp = comparingByKey(comparator);
    if( ! sorter().isStable() )  cmp = cmp.thenComparing(comparingByValue());

    Arrays.sort(reference, cmp);

    Comparator<Entry<T,Integer>> CMP = cmp;
    CountingAccess acc = new CountingAccess() {
      @Override public int compare( int i, int j ) { ++nComps; return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(input, i,j); }
    };
    sorter().sort(0,input.length, acc);

    assertThat(input).isEqualTo(reference);

    int n = input.length;
    assertThat(acc.nComps).isBetween( nCompMin(n), nCompMax(n) );
    assertThat(acc.nSwaps).isBetween( nSwapMin(n), nSwapMax(n) );
  }
}
