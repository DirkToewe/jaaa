package com.github.jaaa.sort;

import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.Swap;
import com.github.jaaa.WithRange;
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


public interface NewSorterInplaceTestTemplate extends NewSorterTestTemplate
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Override SorterInplace sorter();

  @Example
  @Override default void usesCorrectTestTemplate() {
    assertThat( sorter() ).isInstanceOf( SorterInplace.class );
  }

  @Property
  default void sortsStablyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed )
  {
    int        from = sample.getFrom(),
              until = sample.getUntil();
    boolean[] array = sample.getData();

    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsStablyAccessWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample, @ForAll boolean reversed )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    byte[] array = sample.getData();

    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )             cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsStablyAccessWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample, @ForAll boolean reversed )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] array = sample.getData();

    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(from, until, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }



  @Property
  default void sortsStablyAccessWithOffsetBoolean( @ForAll("arraysBoolean") boolean[] array )
  {
    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(0,array.length, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsStablyAccessWithOffsetByte( @ForAll("arraysByte") byte[] array )
  {
    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
               reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(0,array.length, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsStablyAccessWithOffsetInt( @ForAll("arraysInt") int[] array )
  {
    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
                  reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, cmp);

    var CMP = cmp;
    sorter().sort(0,array.length, new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(input[i], input[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(input, i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }
}
