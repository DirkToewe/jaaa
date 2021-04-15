package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.jaaa.misc.Boxing.boxed;


public interface SorterInplaceTestTemplate extends SorterTestTemplate
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override SorterInplace sorter();


  default long nSwapMax( int len ) { return Long.MAX_VALUE; }
  default long nSwapMin( int len ) { return 0; }


  abstract class CountingAccess implements CompareSwapAccess {
    public long nComps,
                nSwaps;
  }


  @Example
  @Override default void usesCorrectTestTemplate() {
    assertThat( sorter() ).isInstanceOf( SorterInplace.class );
  }

  @Property default                         void sortsStablyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessWithRangeStrong ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll boolean reversed ) { sortsStablyAccessWithRange(sample                   , reversed); }
  private <T extends Comparable<? super T>> void sortsStablyAccessWithRange( WithRange<T[]> sample, boolean reversed )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var array = sample.getData();

    Tuple2<T,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )          cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    var acc = new CountingAccess() {
      @Override public int compare( int i, int j ) { ++nComps; return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(input,i,j); }
    };
    sorter().sort(from,until, acc);

    assertThat(input).isEqualTo(reference);

    int n = until-from;
    assertThat(acc.nComps).isBetween( nCompMin(n), nCompMax(n) );
    assertThat(acc.nSwaps).isBetween( nSwapMin(n), nSwapMax(n) );
  }



  @Property default                         void sortsStablyAccessBoolean( @ForAll("arraysBoolean") boolean[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessByte   ( @ForAll("arraysByte"   )    byte[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessShort  ( @ForAll("arraysShort"  )   short[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessInt    ( @ForAll("arraysInt"    )     int[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessLong   ( @ForAll("arraysLong"   )    long[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessChar   ( @ForAll("arraysChar"   )    char[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessFloat  ( @ForAll("arraysFloat"  )   float[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessDouble ( @ForAll("arraysDouble" )  double[] array ) { sortsStablyAccess( boxed(array) ); }
  @Property default                         void sortsStablyAccessString ( @ForAll("arraysString" )  String[] array ) { sortsStablyAccess(       array  ); }
  private <T extends Comparable<? super T>> void sortsStablyAccess( T[] array )
  {
    Tuple2<T,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    var acc = new CountingAccess() {
      @Override public int compare( int i, int j ) { ++nComps; return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { ++nSwaps; Swap.swap(input, i,j); }
    };
    sorter().sort(0,array.length, acc);

    assertThat(input).isEqualTo(reference);

    int n = array.length;
    assertThat(acc.nComps).isBetween( nCompMin(n), nCompMax(n) );
    assertThat(acc.nSwaps).isBetween( nSwapMin(n), nSwapMax(n) );
  }
}
