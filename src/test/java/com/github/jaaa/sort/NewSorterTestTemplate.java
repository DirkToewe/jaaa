package com.github.jaaa.sort;

import com.github.jaaa.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.misc.Boxed.boxed;
import static com.github.jaaa.misc.Revert.revert;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 10_000 )
public interface NewSorterTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS
  abstract class CmpIdx<T extends Comparable<? super T>> implements Comparable<CmpIdx<T>>
  {
    protected final  T  val;
    protected final int idx;
    protected final boolean reversed;

    public CmpIdx( T _val, int _idx, boolean _reversed ) { val=_val; idx=_idx; reversed=_reversed; }

    @Override public String toString() { return format("{%s,%d}", val, idx); }

    @Override public boolean equals( Object o ) {
      if( ! (o instanceof CmpIdx) ) return false;
      CmpIdx cmp = (CmpIdx) o;
      return cmp.val==val
          && cmp.idx==idx;
    }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  Sorter sorter();

  @Example
  default void usesCorrectTestTemplate() {
    assertThat( sorter() ).isNotInstanceOf( SorterInplace.class );
  }

  default  <T extends Comparable<? super T>> CmpIdx<T> CmpIdx( T val, int idx, boolean reversed ) {
    return new CmpIdx<T>(val, idx, reversed) {
      @Override public int compareTo( CmpIdx<T> cmp ) {
        assert cmp.reversed == reversed;
        int result = val.compareTo(cmp.val);
        if( result==0 && ! sorter().isStable() )
            result = Integer.compare(idx, cmp.idx);
        return reversed ? -result
                        : +result;
      }
    };
  }

  @Property
  default void sortsStablyAccessWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyAccessWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyAccessWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample, @ForAll boolean reversed )
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



  @Property
  default void sortsArraysByte( @ForAll("arraysByte") byte[] seq )
  {
    byte[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysShort( @ForAll("arraysShort") short[] seq )
  {
    short[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysInt( @ForAll("arraysInt") int[] seq )
  {
    int[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysLong( @ForAll("arraysLong") long[] seq )
  {
    long[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysChar( @ForAll("arraysChar") char[] seq )
  {
    char[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysFloat( @ForAll("arraysFloat") float[] seq )
  {
    float[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysDouble( @ForAll("arraysDouble") double[] seq )
  {
    double[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }




  @Property
  default void sortsArraysComparableBoolean( @ForAll("arraysBoolean") boolean[] sampleRaw )
  {
    var sample = boxed(sampleRaw);
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparableByte( @ForAll("arraysByte") byte[] sampleRaw )
  {
    var sample = boxed(sampleRaw);
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparableInteger( @ForAll("arraysInt") int[] sampleRaw )
  {
    var sample = boxed(sampleRaw);
    var backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparableString( @ForAll("arraysString") String[] sample )
  {
    String[] backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }




  @Property
  default void sortsStablyArraysComparableBoolean( @ForAll("arraysBoolean") boolean[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Boolean>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
             backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property
  default void sortsStablyArraysComparableByte( @ForAll("arraysByte") byte[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Byte>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
          backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property
  default void sortsStablyArraysComparableInteger( @ForAll("arraysInt") int[] sample, @ForAll boolean reversed )
  {
    CmpIdx<Integer>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
             backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }

  @Property
  default void sortsStablyArraysComparableString( @ForAll("arraysString") String[] sample, @ForAll boolean reversed )
  {
    CmpIdx<String>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat( input ).isEqualTo(backup);
  }




  @Property
  default void sortsArraysComparatorByte( @ForAll("arraysByte") byte[] seq, @ForAll boolean reversed )
  {
    ComparatorByte cmp = Byte::compare;
    if(reversed)   cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorShort( @ForAll("arraysShort") short[] seq, @ForAll boolean reversed )
  {
    ComparatorShort cmp = Short::compare;
    if(reversed)    cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorInt( @ForAll("arraysInt") int[] seq, @ForAll boolean reversed )
  {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorLong( @ForAll("arraysLong") long[] seq, @ForAll boolean reversed )
  {
    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorChar( @ForAll("arraysChar") char[] seq, @ForAll boolean reversed )
  {
    ComparatorChar cmp = Character::compare;
    if( reversed ) cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorFloat( @ForAll("arraysFloat") float[] seq, @ForAll boolean reversed )
  {
    ComparatorFloat cmp = Float::compare;
    if( reversed )  cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorDouble( @ForAll("arraysDouble") double[] seq, @ForAll boolean reversed )
  {
    ComparatorDouble cmp = Double::compare;
    if( reversed )   cmp = cmp.reversed();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }




  @Property
  default void sortsStablyArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyArraysTupleByte( @ForAll("arraysByte") byte[] sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyArraysTupleInt( @ForAll("arraysInt") int[] sample, @ForAll boolean reversed )
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




  @Property
  default void sortsArraysWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    byte[] input = sample.getData(),
       reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeShort( @ForAll("arraysWithRangeShort") WithRange<short[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    short[] input = sample.getData(),
        reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] input = sample.getData(),
      reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeLong( @ForAll("arraysWithRangeLong") WithRange<long[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    long[] input = sample.getData(),
       reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeChar( @ForAll("arraysWithRangeChar") WithRange<char[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    char[] input = sample.getData(),
       reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeFloat( @ForAll("arraysWithRangeFloat") WithRange<float[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    float[] input = sample.getData(),
        reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> sample )
  {
    int       from = sample.getFrom(),
             until = sample.getUntil();
    double[] input = sample.getData(),
         reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }




  @Property
  default void sortsArraysWithRangeComparableBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var input = boxed( sample.getData() );
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeComparableByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var input = boxed( sample.getData() );
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeComparableInteger( @ForAll("arraysWithRangeInt") WithRange<int[]> sample )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var input = boxed( sample.getData() );
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeComparableString( @ForAll("arraysWithRangeString") WithRange<String[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort(input,     from, until);
    assertThat(input).isEqualTo(reference);
  }




  @Property
  default void sortsStablyArraysWithRangeComparableBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sampleWithRange, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyArraysWithRangeComparableByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sampleWithRange, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyArraysWithRangeComparableInteger( @ForAll("arraysWithRangeInt") WithRange<int[]> sampleWithRange, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyArraysWithRangeComparableString( @ForAll("arraysWithRangeString") WithRange<String[]> sampleWithRange, @ForAll boolean reversed )
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




  @Property
  default void sortsArraysWithRangeComparatorByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorByte cmp = Byte::compare;
    if( reversed ) cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorShort( @ForAll("arraysWithRangeShort") WithRange<short[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorShort cmp = Short::compare;
    if( reversed )  cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorLong( @ForAll("arraysWithRangeLong") WithRange<long[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
            until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    var backup = sample.clone();

    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    Arrays.sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorChar( @ForAll("arraysWithRangeChar") WithRange<char[]> sampleWithRange, @ForAll boolean reversed )
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

  @Property
  default void sortsArraysWithRangeComparatorFloat( @ForAll("arraysWithRangeFloat") WithRange<float[]> sampleWithRange, @ForAll boolean reversed )
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

  @Property
  default void sortsArraysWithRangeComparatorDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> sampleWithRange, @ForAll boolean reversed )
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




  @Property
  default void sortsStablyTupleArraysWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyTupleArraysWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample, @ForAll boolean reversed )
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

  @Property
  default void sortsStablyTupleArraysWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample, @ForAll boolean reversed )
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
