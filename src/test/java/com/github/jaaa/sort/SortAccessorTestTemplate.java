package com.github.jaaa.sort;

import com.github.jaaa.*;
import com.github.jaaa.compare.*;
import com.github.jaaa.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.Tuple;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.permute.Revert.revert;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static net.jqwik.api.Tuple.Tuple2;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000 )
public interface SortAccessorTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS
  interface SortAccessor<T> {
    void sort( T arr, int from, int until );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  <T> SortAccessor<T> createAccessor( CompareRandomAccessor<T> srtAcc );

  boolean isStable();

  @Property default void sortsArraysByte_smallPermutations( @ForAll("arraysByte_smallPermutations") byte[] ref ) { sortsArraysByte(ref); }
  @Property default void sortsArraysByte_limitedRange     ( @ForAll("arraysByte_limitedRange"     ) byte[] ref ) { sortsArraysByte(ref); }
  @Property default void sortsArraysByte                  ( @ForAll("arraysByte"                  ) byte[] ref )
  {
           ref = ref.clone();
    byte[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<byte[]> acc = createAccessor( (CompareRandomAccessorArrByte) (a, i, b, j) -> Byte.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysShort( @ForAll("arraysShort") short[] ref )
  {
            ref = ref.clone();
    short[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<short[]> acc = createAccessor( (CompareRandomAccessorArrShort) (a,i, b,j) -> Short.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysInt( @ForAll("arraysInt") int[] ref )
  {
          ref = ref.clone();
    int[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<int[]> acc = createAccessor( (CompareRandomAccessorArrInt) (a,i, b,j) -> Integer.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysLong( @ForAll("arraysLong") long[] ref )
  {
           ref = ref.clone();
    long[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<long[]> acc = createAccessor( (CompareRandomAccessorArrLong) (a,i, b,j) -> Long.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysChar( @ForAll("arraysChar") char[] ref )
  {
           ref = ref.clone();
    char[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<char[]> acc = createAccessor( (CompareRandomAccessorArrChar) (a, i, b, j) -> Character.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysFloat( @ForAll("arraysFloat") float[] ref )
  {
            ref = ref.clone();
    float[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<float[]> acc = createAccessor( (CompareRandomAccessorArrFloat) (a, i, b, j) -> Float.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysDouble( @ForAll("arraysDouble") double[] ref )
  {
             ref = ref.clone();
    double[] tst = ref.clone();
    Arrays.sort(ref);
    SortAccessor<double[]> acc = createAccessor( (CompareRandomAccessorArrDouble) (a, i, b, j) -> Double.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }



  @Property default void sortsArraysWithRangeByte( @ForAll("arraysWithRangeByte") WithRange<byte[]> refRange )
  {
    byte[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    byte[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<byte[]> acc = createAccessor( (CompareRandomAccessorArrByte) (a,i, b,j) -> Byte.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeShort( @ForAll("arraysWithRangeShort") WithRange<short[]> refRange )
  {
    short[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    short[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<short[]> acc = createAccessor( (CompareRandomAccessorArrShort) (a,i, b,j) -> Short.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeInt( @ForAll("arraysWithRangeInt") WithRange<int[]> refRange )
  {
    int[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    int[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<int[]> acc = createAccessor( (CompareRandomAccessorArrInt) (a,i, b,j) -> Integer.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeLong( @ForAll("arraysWithRangeLong") WithRange<long[]> refRange )
  {
    long[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    long[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<long[]> acc = createAccessor( (CompareRandomAccessorArrLong) (a,i, b,j) -> Long.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeChar( @ForAll("arraysWithRangeChar") WithRange<char[]> refRange )
  {
    char[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    char[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<char[]> acc = createAccessor( (CompareRandomAccessorArrChar) (a,i, b,j) -> Character.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeFloat( @ForAll("arraysWithRangeFloat") WithRange<float[]> refRange )
  {
    float[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    float[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<float[]> acc = createAccessor( (CompareRandomAccessorArrFloat) (a,i, b,j) -> Float.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeDouble( @ForAll("arraysWithRangeDouble") WithRange<double[]> refRange )
  {
    double[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    double[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<double[]> acc = createAccessor( (CompareRandomAccessorArrDouble) (a,i, b,j) -> Double.compare(a[i], b[j]) );
    acc   .sort(tst,from,until);
    assertThat(tst).isEqualTo(ref);
  }



  @Property default void sortsArraysByteReversed( @ForAll("arraysByte") byte[] ref )
  {
           ref = ref.clone();
    byte[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<byte[]> acc = createAccessor( (CompareRandomAccessorArrByte) (a,i, b,j) -> -Byte.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysShortReversed( @ForAll("arraysShort") short[] ref )
  {
            ref = ref.clone();
    short[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<short[]> acc = createAccessor( (CompareRandomAccessorArrShort) (a,i, b,j) -> -Short.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysIntReversed( @ForAll("arraysInt") int[] ref )
  {
          ref = ref.clone();
    int[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<int[]> acc = createAccessor( (CompareRandomAccessorArrInt) (a,i, b,j) -> -Integer.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysLongReversed( @ForAll("arraysLong") long[] ref )
  {
           ref = ref.clone();
    long[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<long[]> acc = createAccessor( (CompareRandomAccessorArrLong) (a,i, b,j) -> -Long.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysCharReversed( @ForAll("arraysChar") char[] ref )
  {
           ref = ref.clone();
    char[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<char[]> acc = createAccessor( (CompareRandomAccessorArrChar) (a,i, b,j) -> -Character.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysFloatReversed( @ForAll("arraysFloat") float[] ref )
  {
            ref = ref.clone();
    float[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<float[]> acc = createAccessor( (CompareRandomAccessorArrFloat) (a,i, b,j) -> -Float.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysDoubleReversed( @ForAll("arraysDouble") double[] ref )
  {
             ref = ref.clone();
    double[] tst = ref.clone();
    Arrays.sort(ref); revert(ref);
    SortAccessor<double[]> acc = createAccessor( (CompareRandomAccessorArrDouble) (a,i, b,j) -> -Double.compare(a[i], b[j]) );
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }



  @Property default void sortsArraysWithRangeByteReversed( @ForAll("arraysWithRangeByte") WithRange<byte[]> refRange )
  {
    byte[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    byte[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<byte[]> acc = createAccessor( (CompareRandomAccessorArrByte) (a,i, b,j) -> -Byte.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeShortReversed( @ForAll("arraysWithRangeShort") WithRange<short[]> refRange )
  {
    short[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    short[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<short[]> acc = createAccessor( (CompareRandomAccessorArrShort) (a,i, b,j) -> -Short.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeIntReversed( @ForAll("arraysWithRangeInt") WithRange<int[]> refRange )
  {
    int[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    int[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<int[]> acc = createAccessor( (CompareRandomAccessorArrInt) (a,i, b,j) -> -Integer.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeLongReversed( @ForAll("arraysWithRangeLong") WithRange<long[]> refRange )
  {
    long[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    long[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<long[]> acc = createAccessor( (CompareRandomAccessorArrLong) (a,i, b,j) -> -Long.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeCharReversed( @ForAll("arraysWithRangeChar") WithRange<char[]> refRange )
  {
    char[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    char[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<char[]> acc = createAccessor( (CompareRandomAccessorArrChar) (a,i, b,j) -> -Character.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeFloatReversed( @ForAll("arraysWithRangeFloat") WithRange<float[]> refRange )
  {
    float[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    float[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<float[]> acc = createAccessor( (CompareRandomAccessorArrFloat) (a,i, b,j) -> -Float.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
  }
  @Property default void sortsArraysWithRangeDoubleReversed( @ForAll("arraysWithRangeDouble") WithRange<double[]> refRange )
  {
    double[] ref = refRange.getData().clone(); int from = refRange.getFrom ();
    double[] tst =                ref.clone(); int until= refRange.getUntil();
    Arrays.sort(ref,from,until); SortAccessor<double[]> acc = createAccessor( (CompareRandomAccessorArrDouble) (a,i, b,j) -> -Double.compare(a[i], b[j]) );
    acc   .sort(tst,from,until); revert(ref,from,until);
    assertThat(tst).isEqualTo(ref);
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
  default <T extends Comparable<? super T>> void sortsStablyArraysTuple( T[] sample, boolean reversed )
  {
    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[]
      ref = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
      tst = ref.clone();

    Comparator<Tuple2<T,Integer>> cmp =   comparing(Tuple2::get1);
    if( reversed )          cmp = cmp.reversed();
    if( ! isStable() )      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(ref,cmp);
    Comparator<Tuple2<T,Integer>> CMP = cmp;
    SortAccessor<Tuple2<T,Integer>[]> acc = createAccessor( new CompareRandomAccessorArrObj<Tuple2<T,Integer>>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int compare( Tuple2<T,Integer>[] a, int i,
                                    Tuple2<T,Integer>[] b, int j ) { return CMP.compare(a[i], b[j]); }
    });
    acc.sort(tst, 0,tst.length);
    assertThat(tst).isEqualTo(ref);
  }



  @Property default void                         sortsStablyArraysWithRangeTupleBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample.map(Boxing::boxed),reversed); }
  @Property default void                         sortsStablyArraysWithRangeTupleString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> sample, @ForAll boolean reversed ) { sortsStablyArraysWithRangeTuple(sample                   ,reversed); }
  default <T extends Comparable<? super T>> void sortsStablyArraysWithRangeTuple( WithRange<T[]> sampleRange, boolean reversed )
  {
    int   from = sampleRange.getFrom(),
         until = sampleRange.getUntil();
    T[] sample = sampleRange.getData();
    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[]
      ref = range(0,sample.length).mapToObj( i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
      tst = ref.clone();

    Comparator<Tuple2<T,Integer>> cmp =   comparing(Tuple2::get1);
    if( reversed )          cmp = cmp.reversed();
    if( ! isStable() )      cmp = cmp.thenComparing(Tuple2::get2);

    Arrays.sort(ref, from,until, cmp);
    Comparator<Tuple2<T,Integer>> CMP = cmp;
    SortAccessor<Tuple2<T,Integer>[]> acc = createAccessor( new CompareRandomAccessorArrObj<Tuple2<T,Integer>>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int compare( Tuple2<T,Integer>[] a, int i,
                                    Tuple2<T,Integer>[] b, int j ) { return CMP.compare(a[i], b[j]); }
    });
    acc.sort(tst, from,until);
    assertThat(tst).isEqualTo(ref);
  }
}
