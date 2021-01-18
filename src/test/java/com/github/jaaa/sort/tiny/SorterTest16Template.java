package com.github.jaaa.sort.tiny;

import com.github.jaaa.*;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.misc.Revert.revert;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


public interface SorterTest16Template extends SorterTest8Template
{
// STATIC FIELDS
  static final int MAX_SIZE = 16;

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeBoolean16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sample, @ForAll boolean reversed )
  {
    int  from = sample.getFrom(),
            until = sample.getUntil();
    var array = sample.getData();

    Tuple2<Boolean,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(input, from, until, new CompareRandomAccessor<>() {
      @Override public int        len( Tuple2<Boolean,Integer>[] buf ) { return buf.length; }
      @Override public void      swap( Tuple2<Boolean,Integer>[] a, int i, Tuple2<Boolean,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Boolean,Integer>[] a, int i, Tuple2<Boolean,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Boolean,Integer>[] a, int i, Tuple2<Boolean,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public int    compare( Tuple2<Boolean,Integer>[] a, int i, Tuple2<Boolean,Integer>[] b, int j ) { return CMP.compare(a[i], b[j]); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    int  from = sample.getFrom(),
            until = sample.getUntil();
    var array = sample.getData();

    Tuple2<Byte,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )             cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(input, from, until, new CompareRandomAccessor<>() {
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return CMP.compare(a[i], b[j]); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyAccessWithRangeInt16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int  from = sample.getFrom(),
            until = sample.getUntil();
    var array = sample.getData();

    Tuple2<Integer,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(reference, from, until, cmp);

    var CMP = cmp;
    sorter().sort(input, from, until, new CompareRandomAccessor<>() {
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return CMP.compare(a[i], b[j]); }
    });

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  default void sortsArraysByte16( @ForAll @Size(min=0, max=MAX_SIZE) byte[] seq )
  {
    byte[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysShort16( @ForAll @Size(min=0, max=MAX_SIZE) short[] seq )
  {
    short[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysInt16( @ForAll @Size(min=0, max=MAX_SIZE) int[] seq )
  {
    int[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysLong16( @ForAll @Size(min=0, max=MAX_SIZE) long[] seq )
  {
    long[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysChar16( @ForAll @Size(min=0, max=MAX_SIZE) char[] seq )
  {
    char[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysFloat16( @ForAll @Size(min=0, max=MAX_SIZE) float[] seq )
  {
    float[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysDouble16( @ForAll @Size(min=0, max=MAX_SIZE) double[] seq )
  {
    double[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsArraysComparableBoolean16( @ForAll @Size(min=0, max=MAX_SIZE) Boolean[] sample )
  {
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparableByte16( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] sample )
  {
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparableInteger16( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] sample )
  {
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES / 8 )
  default void sortsArraysComparableString16( @ForAll @Size(min=0, max=MAX_SIZE) String[] sample )
  {
    String[] backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsStablyArraysComparableBoolean16( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Boolean>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysComparableByte16( @ForAll @Size(min=0, max=MAX_SIZE) byte[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Byte>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysComparableInteger16( @ForAll @Size(min=0, max=MAX_SIZE) int[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Integer>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES / 8 )
  default void sortsStablyArraysComparableString16( @ForAll @Size(min=0, max=MAX_SIZE) String[] sample, @ForAll boolean reversed )
  {
    CmpIdx<String>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsArraysComparatorByte16( @ForAll @Size(min=0, max=MAX_SIZE) byte[] seq, @ForAll boolean reversed )
  {
    ComparatorByte cmp = Byte::compare;
    if(reversed)   cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorShort16( @ForAll @Size(min=0, max=MAX_SIZE) short[] seq, @ForAll boolean reversed )
  {
    ComparatorShort cmp = Short::compare;
    if(reversed)    cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorInt16( @ForAll @Size(min=0, max=MAX_SIZE) int[] seq, @ForAll boolean reversed )
  {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorLong16( @ForAll @Size(min=0, max=MAX_SIZE) long[] seq, @ForAll boolean reversed )
  {
    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorChar16( @ForAll @Size(min=0, max=MAX_SIZE) char[] seq, @ForAll boolean reversed )
  {
    ComparatorChar cmp = Character::compare;
    if( reversed ) cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorFloat16( @ForAll @Size(min=0, max=MAX_SIZE) float[] seq, @ForAll boolean reversed )
  {
    ComparatorFloat cmp = Float::compare;
    if( reversed )  cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysComparatorDouble16( @ForAll @Size(min=0, max=MAX_SIZE) double[] seq, @ForAll boolean reversed )
  {
    ComparatorDouble cmp = Double::compare;
    if( reversed )   cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsStablyArraysTupleBoolean16( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample, @ForAll boolean reversed )
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Boolean,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter().sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysTupleByte16( @ForAll @Size(min=0, max=MAX_SIZE) byte[] sample, @ForAll boolean reversed )
  {
    Tuple2<Byte,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Byte,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )             cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter().sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysTupleInt16( @ForAll @Size(min=0, max=MAX_SIZE) int[] sample, @ForAll boolean reversed )
  {
    Tuple2<Integer,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<Integer,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort( reference, cmp );
    sorter().sort( input,     cmp );
    assertThat(input).isEqualTo(reference);
  }




  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    byte[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeShort16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) short[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    short[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeInt16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    int[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeLong16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) long[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    long[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeChar16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) char[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    char[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeFloat16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) float[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    float[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeDouble16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) double[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    double[] input = sample.getData(),
            reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }




  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparableBoolean16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Boolean[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparableByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparableInteger16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES / 8 )
  default void sortsArraysWithRangeComparableString16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) String[]> sample )
  {
    int from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }




  @Property( tries = N_TRIES )
  default void sortsStablyArraysWithRangeComparableBoolean16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    CmpIdx<Boolean>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup, from,until);
    sorter().sort(input,  from,until);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysWithRangeComparableByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    CmpIdx<Byte>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup, from,until);
    sorter().sort(input,  from,until);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyArraysWithRangeComparableInteger16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    CmpIdx<Integer>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup, from,until);
    sorter().sort(input,  from,until);
    assertThat( input ).isEqualTo(backup);
  }

  @Property( tries = N_TRIES / 8 )
  default void sortsStablyArraysWithRangeComparableString16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) String[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    CmpIdx<String>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup, from,until);
    sorter().sort(input,  from,until);
    assertThat( input ).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorByte cmp = Byte::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorShort16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) short[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorShort cmp = Short::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorInt16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorInt cmp = Integer::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorLong16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) long[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorLong cmp = Long::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorChar16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) char[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorChar cmp = Character::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorFloat16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) float[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorFloat cmp = Float::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property( tries = N_TRIES )
  default void sortsArraysWithRangeComparatorDouble16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) double[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorDouble cmp = Double::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }




  @Property( tries = N_TRIES )
  default void sortsStablyTupleArraysWithRangeBoolean16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) boolean[]> sample, @ForAll boolean reversed )
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
    sorter().sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyTupleArraysWithRangeByte16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
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
    sorter().sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  default void sortsStablyTupleArraysWithRangeInt16( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
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
    sorter().sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }
}
