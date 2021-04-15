package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.misc.Boxing.unboxed;
import static com.github.jaaa.misc.Revert.revert;
import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 10_000 )
public interface SorterTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS
  abstract class CmpIdx<T extends Comparable<? super T>> implements Comparable<CmpIdx<T>>
  {
    protected final  T  val;
    protected final int idx;

    public CmpIdx( T _val, int _idx ) { val=_val; idx=_idx; }

    @Override public String toString() { return format("{%s,%d}", val, idx); }

    @Override public boolean equals( Object o ) {
      assert getClass().isInstance(o);
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


  default long nCompMax( int len ) { return Long.MAX_VALUE; }
  default long nCompMin( int len ) { return 0; }


  abstract class CountingAccessor<T> implements CompareRandomAccessor<T> {
    public long nComps;
  }


  @Example
  default void usesCorrectTestTemplate() {
    assertThat( sorter() ).isNotInstanceOf( SorterInplace.class );
  }

  default <T extends Comparable<? super T>> CmpIdx<T> CmpIdx( T val, int idx, boolean reversed )
  {
    if( sorter().isStable() ) {
      if( reversed )
        return new CmpIdx<T>(val,idx) {
          @Override public int compareTo( CmpIdx<T> cmp ) {
            assert getClass().isInstance(cmp);
            return -val.compareTo(cmp.val);
          }
        };
      return new CmpIdx<T>(val,idx) {
        @Override public int compareTo(CmpIdx<T> cmp ) {
          assert getClass().isInstance(cmp);
          return +val.compareTo(cmp.val);
        }
      };
    }

    if( reversed )
      return new CmpIdx<T>(val,idx) {
        @Override public int compareTo( CmpIdx<T> cmp ) {
          assert getClass().isInstance(cmp);
          int result = val.compareTo(cmp.val);
          if( result==0 )
              result = Integer.compare(idx, cmp.idx);
          return -result;
        }
      };

    return new CmpIdx<T>(val,idx) {
      @Override public int compareTo(CmpIdx<T> cmp ) {
        assert getClass().isInstance(cmp);
        int result = val.compareTo(cmp.val);
        if( result==0 )
            result = Integer.compare(idx, cmp.idx);
        return +result;
      }
    };
  }

  @Property default                         void sortsStablyAccessorWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyAccessorWithRangeString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll boolean reversed ) { sortsStablyAccessorWithRange(sample                   , reversed); }
  private <T extends Comparable<? super T>> void sortsStablyAccessorWithRange( WithRange<T[]> sample, boolean reversed )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var array = sample.getData();

    Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )          cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    var CMP = cmp;
    var acc = new CountingAccessor<Tuple2<T,Integer>[]>() {
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void      swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public int    compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { nComps++; return CMP.compare(a[i], b[j]); }
    };

    var     input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(acc::malloc);
    var reference = input.clone();

    Arrays  .sort(reference, from,until, cmp);
    sorter().sort(input,     from,until, acc);

    assertThat(input).isEqualTo(reference);

    int n = until-from;
    assertThat(acc.nComps).isBetween( nCompMin(n), nCompMax(n) );
  }



  @Property
  default void sortsArraysByte( @ForAll("arraysByte") byte[] seq )
  {
              seq = seq.clone();
    byte[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysShort( @ForAll("arraysShort") short[] seq )
  {
               seq = seq.clone();
    short[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysInt( @ForAll("arraysInt") int[] seq )
  {
             seq = seq.clone();
    int[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysLong( @ForAll("arraysLong") long[] seq )
  {
              seq = seq.clone();
    long[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysChar( @ForAll("arraysChar") char[] seq )
  {
              seq = seq.clone();
    char[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysFloat( @ForAll("arraysFloat") float[] seq )
  {
               seq = seq.clone();
    float[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysDouble( @ForAll("arraysDouble") double[] seq )
  {
                seq = seq.clone();
    double[] backup = seq.clone();
    Arrays.sort(backup);
    sorter().sort(seq);
    assertThat(seq).isEqualTo(backup);
  }




  @Property default                         void sortsArraysComparableBoolean( @ForAll("arraysBoolean") boolean[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableByte   ( @ForAll("arraysByte"   )    byte[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableShort  ( @ForAll("arraysShort"  )   short[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableInt    ( @ForAll("arraysInt"    )     int[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableLong   ( @ForAll("arraysLong"   )    long[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableChar   ( @ForAll("arraysChar"   )    char[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableFloat  ( @ForAll("arraysFloat"  )   float[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableDouble ( @ForAll("arraysDouble" )  double[] sampleRaw ) { sortsArraysComparable( boxed(sampleRaw) ); }
  @Property default                         void sortsArraysComparableString ( @ForAll("arraysString" )  String[] sample    ) { sortsArraysComparable( sample.clone() ); }
  private <T extends Comparable<? super T>> void sortsArraysComparable( T[] sample )
  {
    T[] backup = sample.clone();
    Arrays.sort(backup);
    sorter().sort(sample);
    assertThat(sample).isEqualTo(backup);
  }



  @Property default                         void sortsStablyArraysComparableBoolean( @ForAll("arraysBoolean") boolean[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableByte   ( @ForAll("arraysByte"   )    byte[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableShort  ( @ForAll("arraysShort"  )   short[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableInt    ( @ForAll("arraysInt"    )     int[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableLong   ( @ForAll("arraysLong"   )    long[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableChar   ( @ForAll("arraysChar"   )    char[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableFloat  ( @ForAll("arraysFloat"  )   float[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableDouble ( @ForAll("arraysDouble" )  double[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(boxed(sample),reversed); }
  @Property default                         void sortsStablyArraysComparableString ( @ForAll("arraysString" )  String[] sample, @ForAll boolean reversed ) { sortsStablyArraysComparable(      sample, reversed); }
  private <T extends Comparable<? super T>> void sortsStablyArraysComparable( T[] sample, boolean reversed )
  {
    CmpIdx<T>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
       backup = input.clone();
    Arrays.sort(backup);
    sorter().sort(input);
    assertThat(input).isEqualTo(backup);
  }



  @Property
  default void sortsArraysComparatorByte( @ForAll("arraysByte") byte[] seq, @ForAll boolean reversed )
  {
    ComparatorByte cmp = Byte::compare;
    if(reversed)   cmp = cmp.reversed();
                   seq = seq.clone();
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
                   seq = seq.clone();
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
                   seq = seq.clone();
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
                   seq = seq.clone();
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
                   seq = seq.clone();
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
                    seq = seq.clone();
    var          backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorDouble( @ForAll("arraysDouble") double[] seq, @ForAll boolean reversed )
  {
    ComparatorDouble cmp = Double::compare;
    if( reversed )   cmp = cmp.reversed();
                   seq = seq.clone();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }

  @Property
  default void sortsArraysComparatorString( @ForAll("arraysString") String[] seq, @ForAll boolean reversed )
  {
    Comparator<String> cmp = naturalOrder();
    if(reversed) cmp = cmp.reversed();
                   seq = seq.clone();
    var         backup = seq.clone();
    Arrays.sort(backup); if(reversed) revert(backup);
    sorter().sort(seq, cmp);
    assertThat(seq).isEqualTo(backup);
  }



  @Property default void                         sortsStablyArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleByte   ( @ForAll("arraysByte"   )    byte[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleShort  ( @ForAll("arraysShort"  )   short[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleInt    ( @ForAll("arraysInt"    )     int[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleLong   ( @ForAll("arraysLong"   )    long[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleChar   ( @ForAll("arraysChar"   )    char[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleFloat  ( @ForAll("arraysFloat"  )   float[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleDouble ( @ForAll("arraysDouble" )  double[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple( boxed(sample), reversed ); }
  @Property default void                         sortsStablyArraysTupleString ( @ForAll("arraysString" )  String[] sample, @ForAll boolean reversed ) { sortsStablyArraysTuple(       sample , reversed ); }
  private <T extends Comparable<? super T>> void sortsStablyArraysTuple( T[] sample, boolean reversed )
  {
    Tuple2<T,Integer>[] input = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )          cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays  .sort(reference,cmp);
    sorter().sort(input,    cmp);
    assertThat(input).isEqualTo(reference);
  }




  @Property
  default void sortsArraysWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    byte[] input = sample.getData().clone(),
       reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeShort( @ForAll("arraysWithRangeShort") WithRange<short[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    short[] input = sample.getData().clone(),
        reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] input = sample.getData().clone(),
      reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeLong( @ForAll("arraysWithRangeLong") WithRange<long[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    long[] input = sample.getData().clone(),
       reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeChar( @ForAll("arraysWithRangeChar") WithRange<char[]> sample )
  {
    int     from = sample.getFrom(),
           until = sample.getUntil();
    char[] input = sample.getData().clone(),
       reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeFloat( @ForAll("arraysWithRangeFloat") WithRange<float[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    float[] input = sample.getData().clone(),
        reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }

  @Property
  default void sortsArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> sample )
  {
    int       from = sample.getFrom(),
             until = sample.getUntil();
    double[] input = sample.getData().clone(),
         reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }




  @Property default                         void sortsArraysWithRangeComparableBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample ) { sortsArraysWithRangeComparable( sample.map(Boxing::boxed) ); }
  @Property default                         void sortsArraysWithRangeComparableString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample ) { sortsArraysWithRangeComparable( sample.map(x->x.clone() ) ); }
  private <T extends Comparable<? super T>> void sortsArraysWithRangeComparable( WithRange<T[]> sample )
  {
    int      from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData();
    var reference = input.clone();
    Arrays  .sort(reference, from,until);
    sorter().sort(input,     from,until);
    assertThat(input).isEqualTo(reference);
  }



  @Property default                         void sortsStablyArraysWithRangeComparableBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyArraysWithRangeComparableString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeComparable(sample,                    reversed); }
  default <T extends Comparable<? super T>> void sortsStablyArraysWithRangeComparable( WithRange<T[]> sampleWithRange, boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData();
    CmpIdx<String>[] input = range(0,sample.length).mapToObj( i -> CmpIdx(sample[i],i,reversed) ).toArray(CmpIdx[]::new),
            backup = input.clone();
    Arrays  .sort(backup, from,until);
    sorter().sort(input,  from,until);
    assertThat( input ).isEqualTo(backup);
  }




  @Property
  default void sortsArraysWithRangeComparatorByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorByte cmp = Byte::compare;
    if( reversed ) cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorShort( @ForAll("arraysWithRangeShort") WithRange<short[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorShort cmp = Short::compare;
    if( reversed )  cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorLong( @ForAll("arraysWithRangeLong") WithRange<long[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorChar( @ForAll("arraysWithRangeChar") WithRange<char[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorChar cmp = Character::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorFloat( @ForAll("arraysWithRangeFloat") WithRange<float[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorFloat cmp = Float::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }

  @Property
  default void sortsArraysWithRangeComparatorDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> sampleWithRange, @ForAll boolean reversed )
  {
    int   from = sampleWithRange.getFrom(),
         until = sampleWithRange.getUntil();
    var sample = sampleWithRange.getData().clone();
    var backup = sample.clone();

    ComparatorDouble cmp = Double::compare;
    if(reversed)   cmp = cmp.reversed();
    Arrays  .sort(backup, from,until); if(reversed) revert(backup, from,until);
    sorter().sort(sample, from,until, cmp);
    assertThat(sample).isEqualTo(backup);
  }




  @Property default                         void sortsStablyTupleArraysWithRangeBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample.map(Boxing::boxed), reversed); }
  @Property default                         void sortsStablyTupleArraysWithRangeString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll boolean reversed ) { sortsStablyTupleArraysWithRange(sample                   , reversed); }
  private <T extends Comparable<? super T>> void sortsStablyTupleArraysWithRange( WithRange<T[]> sample, boolean reversed )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil();
    var array = sample.getData();

    Tuple2<T,Integer>[] input = range(0,array.length).mapToObj( i -> Tuple.of(array[i],i) ).toArray(Tuple2[]::new),
            reference = input.clone();

    Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
    if( reversed )                cmp = cmp.reversed();

    if( ! sorter().isStable() )
      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays  .sort(reference, from, until, cmp);
    sorter().sort(input,     from, until, cmp);
    assertThat(input).isEqualTo(reference);
  }



  @Property()
  default void sortsBufInt( @ForAll("arraysInt") int[] seq )
  {
    seq = seq.clone();
    var backup = boxed(seq);
    Arrays.sort(backup);
    sorter().sort( IntBuffer.wrap(seq) );
    assertThat(seq).isEqualTo(backup);
  }

  @Property()
  default void sortsBufWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] input = sample.getData().clone(),
      reference = input.clone();
    Arrays  .sort(reference, from, until);
    sorter().sort( IntBuffer.wrap(input), from, until );
    assertThat(input).isEqualTo(reference);
  }

  @Property()
  default void sortsBufWithPosLimInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample )
  {
    int    from = sample.getFrom(),
          until = sample.getUntil();
    int[] input = sample.getData().clone(),
      reference = input.clone();
    Arrays.sort(reference, from, until);
    sorter().sort( IntBuffer.wrap(input, from,until-from) );
    assertThat(input).isEqualTo(reference);
  }



  @Property()
  default void sortsComparatorBufInt( @ForAll("arraysInt") int[] seq, @ForAll boolean reversed )
  {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();

    seq = seq.clone();
    var backup = boxed(seq);
    Arrays.sort(backup, cmp::compare);
    sorter().sort( IntBuffer.wrap(seq), cmp );
    assertThat(seq).isEqualTo( unboxed(backup) );
  }

  @Property()
  default void sortsComparatorBufWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample, @ForAll boolean reversed )
  {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();

    int      from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData().clone();
    var reference = boxed(input);
    Arrays.sort(reference, from,until, cmp::compare);
    sorter().sort( IntBuffer.wrap(input), from, until, cmp );
    assertThat(input).isEqualTo(reference);
  }

  @Property()
  default void sortsComparatorBufWithPosLimInt( @ForAll("arraysWithRangeInt") WithRange<int[]> sample, @ForAll boolean reversed )
  {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();

    int      from = sample.getFrom(),
            until = sample.getUntil();
    var     input = sample.getData().clone();
    var reference = boxed(input);
    Arrays.sort(reference, from,until, cmp::compare);
    sorter().sort( IntBuffer.wrap(input,from,until-from), cmp );
    assertThat(input).isEqualTo(reference);
  }
}
