package com.github.jaaa.merge;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import com.github.jaaa.compare.*;
import com.github.jaaa.fn.Int2Fn;
import com.github.jaaa.permute.Revert;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.sort.TimSort;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.Tuple.Tuple3;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.github.jaaa.ArrayProviderTemplate.withRange;
import static com.github.jaaa.Concat.concat;
import static com.github.jaaa.permute.Revert.revert;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 10_000, shrinking = ShrinkingMode.OFF )
public interface MergerTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  Merger merger();


  @Provide default <T> Arbitrary<Tuple3<
    WithRange<T>,
    WithRange<T>,
    WithInsertIndex<T>
  >> mergeSamplesWithRange( Int2Fn<Arbitrary<T>> arraysWithSizeBetween )
  {
    return
    withRange( arraysWithSizeBetween.apply(0,maxArraySize()) ).flatMap( a ->
    withRange( arraysWithSizeBetween.apply(0,maxArraySize()) ).flatMap( b -> { int cLen = a.rangeLength() + b.rangeLength();
      return   arraysWithSizeBetween.apply(cLen,cLen*4)       .flatMap( c ->
        Arbitraries.integers().between(0, Array.getLength(c)-cLen).map( c0 -> Tuple.of(a,b, new WithInsertIndex<>(c0,c) ) )
      );
    }));
  }
  @Provide default Arbitrary<Tuple3<WithRange<  byte[]>,WithRange<  byte[]>, WithInsertIndex<  byte[]>>> mergeSamplesWithRangeByte  () { return mergeSamplesWithRange( (m,n) -> Arbitraries.bytes   ().array(  byte[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange< short[]>,WithRange< short[]>, WithInsertIndex< short[]>>> mergeSamplesWithRangeShort () { return mergeSamplesWithRange( (m,n) -> Arbitraries.shorts  ().array( short[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange<   int[]>,WithRange<   int[]>, WithInsertIndex<   int[]>>> mergeSamplesWithRangeInt   () { return mergeSamplesWithRange( (m,n) -> Arbitraries.integers().array(   int[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange<  long[]>,WithRange<  long[]>, WithInsertIndex<  long[]>>> mergeSamplesWithRangeLong  () { return mergeSamplesWithRange( (m,n) -> Arbitraries.longs   ().array(  long[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange<  char[]>,WithRange<  char[]>, WithInsertIndex<  char[]>>> mergeSamplesWithRangeChar  () { return mergeSamplesWithRange( (m,n) -> Arbitraries.chars   ().array(  char[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange< float[]>,WithRange< float[]>, WithInsertIndex< float[]>>> mergeSamplesWithRangeFloat () { return mergeSamplesWithRange( (m,n) -> Arbitraries.floats  ().array( float[].class).ofMinSize(m).ofMaxSize(n) ); }
  @Provide default Arbitrary<Tuple3<WithRange<double[]>,WithRange<double[]>, WithInsertIndex<double[]>>> mergeSamplesWithRangeDouble() { return mergeSamplesWithRange( (m,n) -> Arbitraries.doubles ().array(double[].class).ofMinSize(m).ofMaxSize(n) ); }



  @Property default void mergeNewArrayByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); byte[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    byte[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayShort(
    @ForAll("arraysShort") short[] aRef,
    @ForAll("arraysShort") short[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); short[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    short[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); int[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    int[] aTst = aRef.clone(),
          bTst = bRef.clone(),
          cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayLong(
    @ForAll("arraysLong") long[] aRef,
    @ForAll("arraysLong") long[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); long[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    long[] aTst = aRef.clone(),
           bTst = bRef.clone(),
           cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayChar(
    @ForAll("arraysChar") char[] aRef,
    @ForAll("arraysChar") char[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); char[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    char[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayFloat(
    @ForAll("arraysFloat") float[] aRef,
    @ForAll("arraysFloat") float[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); float[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    float[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewArrayDouble(
    @ForAll("arraysDouble") double[] aRef,
    @ForAll("arraysDouble") double[] bRef
  ) {
    Arrays.sort(aRef);
    Arrays.sort(bRef); double[] cRef = concat(aRef,bRef);
    Arrays.sort(cRef);
    double[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }



  @Property default void mergeNewComparatorArrayByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorByte cmp = Byte::compare;
    if( reversed ) cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); byte[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    byte[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayShort(
    @ForAll("arraysShort") short[] aRef,
    @ForAll("arraysShort") short[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorShort cmp = Short::compare;
    if( reversed )  cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); short[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    short[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); int[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    int[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayLong(
      @ForAll("arraysLong") long[] aRef,
      @ForAll("arraysLong") long[] bRef,
      @ForAll boolean reversed
  ) {
    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); long[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    long[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayChar(
    @ForAll("arraysChar") char[] aRef,
    @ForAll("arraysChar") char[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorChar cmp = Character::compare;
    if( reversed ) cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); char[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    char[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayFloat(
    @ForAll("arraysFloat") float[] aRef,
    @ForAll("arraysFloat") float[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorFloat cmp = Float::compare;
    if( reversed )  cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); float[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    float[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeNewComparatorArrayDouble(
    @ForAll("arraysDouble") double[] aRef,
    @ForAll("arraysDouble") double[] bRef,
    @ForAll boolean reversed
  ) {
    ComparatorDouble cmp = Double::compare;
    if(reversed) cmp = cmp.reversed();
    TimSort.sort(aRef, cmp);
    TimSort.sort(bRef, cmp); double[] cRef = concat(aRef,bRef);
    TimSort.sort(cRef, cmp);
    double[]
      aTst = aRef.clone(),
      bTst = bRef.clone(),
      cTst = merger().merge(aTst,bTst, cmp);
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }



  @Property default void mergeArrayWithRangeByte(
    @ForAll("mergeSamplesWithRangeByte") Tuple3<
      WithRange<byte[]>,
      WithRange<byte[]>,
      WithInsertIndex<byte[]>
    > ref
  ) {
    byte[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    byte[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    byte[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); byte[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); byte[] bTst = bRef.clone();
                                   byte[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeShort(
    @ForAll("mergeSamplesWithRangeShort") Tuple3<
      WithRange<short[]>,
      WithRange<short[]>,
      WithInsertIndex<short[]>
    > ref
  ) {
    short[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    short[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    short[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); short[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); short[] bTst = bRef.clone();
                                   short[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeInt(
    @ForAll("mergeSamplesWithRangeInt") Tuple3<
      WithRange<int[]>,
      WithRange<int[]>,
      WithInsertIndex<int[]>
    > ref
  ) {
    int[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    int[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    int[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); int[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); int[] bTst = bRef.clone();
                                   int[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeLong(
    @ForAll("mergeSamplesWithRangeLong") Tuple3<
      WithRange<long[]>,
      WithRange<long[]>,
      WithInsertIndex<long[]>
    > ref
  ) {
    long[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    long[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    long[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); long[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); long[] bTst = bRef.clone();
                                   long[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeChar(
    @ForAll("mergeSamplesWithRangeChar") Tuple3<
      WithRange<char[]>,
      WithRange<char[]>,
      WithInsertIndex<char[]>
    > ref
  ) {
    char[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    char[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    char[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); char[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); char[] bTst = bRef.clone();
                                   char[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeFloat(
    @ForAll("mergeSamplesWithRangeFloat") Tuple3<
      WithRange<float[]>,
      WithRange<float[]>,
      WithInsertIndex<float[]>
    > ref
  ) {
    float[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    float[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    float[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); float[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); float[] bTst = bRef.clone();
                                   float[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeArrayWithRangeDouble(
    @ForAll("mergeSamplesWithRangeDouble") Tuple3<
      WithRange<double[]>,
      WithRange<double[]>,
      WithInsertIndex<double[]>
    > ref
  ) {
    double[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    double[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    double[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    Arrays.sort(aRef, a0,a0+aLen); double[] aTst = aRef.clone();
    Arrays.sort(bRef, b0,b0+bLen); double[] bTst = bRef.clone();
                                   double[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    Arrays.sort(cRef, c0,c0+aLen+bLen);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }



  @Property default void mergeComparatorArrayWithRangeByte(
    @ForAll("mergeSamplesWithRangeByte") Tuple3<
      WithRange<byte[]>,
      WithRange<byte[]>,
      WithInsertIndex<byte[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorByte cmp = Byte::compare;
    if( reversed ) cmp = cmp.reversed();
    byte[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    byte[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    byte[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); byte[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); byte[] bTst = bRef.clone();
                                         byte[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeShort(
    @ForAll("mergeSamplesWithRangeShort") Tuple3<
      WithRange<short[]>,
      WithRange<short[]>,
      WithInsertIndex<short[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorShort cmp = Short::compare;
    if( reversed )  cmp = cmp.reversed();
    short[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    short[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    short[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); short[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); short[] bTst = bRef.clone();
                                         short[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeInt(
    @ForAll("mergeSamplesWithRangeInt") Tuple3<
      WithRange<int[]>,
      WithRange<int[]>,
      WithInsertIndex<int[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp = cmp.reversed();
    int[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    int[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    int[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); int[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); int[] bTst = bRef.clone();
                                         int[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeLong(
    @ForAll("mergeSamplesWithRangeLong") Tuple3<
      WithRange<long[]>,
      WithRange<long[]>,
      WithInsertIndex<long[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorLong cmp = Long::compare;
    if( reversed ) cmp = cmp.reversed();
    long[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    long[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    long[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); long[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); long[] bTst = bRef.clone();
                                         long[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeChar(
    @ForAll("mergeSamplesWithRangeChar") Tuple3<
      WithRange<char[]>,
      WithRange<char[]>,
      WithInsertIndex<char[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorChar cmp = Character::compare;
    if( reversed ) cmp = cmp.reversed();
    char[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    char[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    char[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); char[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); char[] bTst = bRef.clone();
                                         char[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeFloat(
    @ForAll("mergeSamplesWithRangeFloat") Tuple3<
      WithRange<float[]>,
      WithRange<float[]>,
      WithInsertIndex<float[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorFloat cmp = Float::compare;
    if( reversed ) cmp = cmp.reversed();
    float[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    float[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    float[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); float[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); float[] bTst = bRef.clone();
                                         float[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property default void mergeComparatorArrayWithRangeDouble(
    @ForAll("mergeSamplesWithRangeDouble") Tuple3<
      WithRange<double[]>,
      WithRange<double[]>,
      WithInsertIndex<double[]>
    > ref,
    @ForAll boolean reversed
  ) {
    ComparatorDouble cmp = Double::compare;
    if(reversed) cmp=cmp.reversed();
    double[] aRef = ref.get1().getData().clone(); int a0 = ref.get1().getFrom(), aLen = ref.get1().rangeLength();
    double[] bRef = ref.get2().getData().clone(); int b0 = ref.get2().getFrom(), bLen = ref.get2().rangeLength();
    double[] cRef = ref.get3().getData().clone(); int c0 = ref.get3().getIndex();
    TimSort.sort(aRef, a0,a0+aLen, cmp); double[] aTst = aRef.clone();
    TimSort.sort(bRef, b0,b0+bLen, cmp); double[] bTst = bRef.clone();
                                         double[] cTst = cRef.clone();
    arraycopy(aRef,a0, cRef,c0,aLen);
    arraycopy(bRef,b0, cRef,c0+aLen, bLen);
    TimSort.sort(cRef, c0,c0+aLen+bLen, cmp);
    merger().merge(
      aTst,a0,aLen,
      bTst,b0,bLen,
      cTst,c0, cmp
    );
    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }



  @Property
  default void mergeAccessorArrayByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef,
    @ForAll boolean reversed
  ) {
    byte[]            cRef = new byte[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = new byte[aTest.length+bTest.length];

    CompareRandomAccessor<byte[]> acc = new CompareRandomAccessor<byte[]>(){
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) {
        return reversed ? -Integer.compare(a[i],b[j])
                        : +Integer.compare(a[i],b[j]);
      }
    };

    merger().merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  default void mergeAccessorArrayInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef,
    @ForAll boolean reversed
  ) {
    int[]             cRef = new int[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = new int[aTest.length+bTest.length];

    CompareRandomAccessor<int[]> acc = new CompareRandomAccessor<int[]>(){
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( int[] a, int i, int[] b, int j ) {
        return reversed ? -Integer.compare(a[i],b[j])
                        : +Integer.compare(a[i],b[j]);
      }
    };

    merger().merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  default void mergeStablyAccessorArrayTupleByte(
    @ForAll("arraysByte") byte[] aRaw,
    @ForAll("arraysByte") byte[] bRaw,
    @ForAll boolean reversed
  ) {
    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    @SuppressWarnings("unchecked")
    Tuple2<Byte,Integer>[]
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],              i) ).toArray(Tuple2[]::new),
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],aRaw.length + i) ).toArray(Tuple2[]::new),
      cRef =                             Stream.concat( stream(aRef), stream(bRef) ).toArray(Tuple2[]::new);

    Arrays.sort(aRef,cmp); Tuple2<Byte,Integer>[] aTest = aRef.clone();
    Arrays.sort(bRef,cmp); Tuple2<Byte,Integer>[] bTest = bRef.clone();
                           Tuple2<Byte,Integer>[] cTest = cRef.clone();
    Arrays.sort(cRef,cmp);

    CompareRandomAccessor<Tuple2<Byte,Integer>[]> acc = new CompareRandomAccessor<Tuple2<Byte,Integer>[]>(){
      @SuppressWarnings("unchecked")
      @Override public Tuple2<Byte,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
    };

    merger().merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  default void mergeStablyAccessorArrayTupleInt(
    @ForAll("arraysInt") int[] aRaw,
    @ForAll("arraysInt") int[] bRaw,
    @ForAll boolean reversed
  ) {
    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    @SuppressWarnings("unchecked")
    Tuple2<Integer,Integer>[]
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],              i) ).toArray(Tuple2[]::new),
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],aRaw.length + i) ).toArray(Tuple2[]::new),
      cRef =                             Stream.concat( stream(aRef), stream(bRef) ).toArray(Tuple2[]::new);

    Arrays.sort(aRef,cmp); Tuple2<Integer,Integer>[] aTest = aRef.clone();
    Arrays.sort(bRef,cmp); Tuple2<Integer,Integer>[] bTest = bRef.clone();
                           Tuple2<Integer,Integer>[] cTest = cRef.clone();
    Arrays.sort(cRef,cmp);

    CompareRandomAccessor<Tuple2<Integer,Integer>[]> acc = new CompareRandomAccessor<Tuple2<Integer,Integer>[]>(){
      @SuppressWarnings("unchecked")
      @Override public Tuple2<Integer,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
    };

    merger().merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property
  default void mergeNewArrayWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> aSample,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> bSample
  ) {
    byte[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    byte[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    byte[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    byte[] aTst = aRef.clone();
    byte[] bTst = bRef.clone();
    byte[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeShort(
    @ForAll("arraysWithRangeShort") WithRange<short[]> aSample,
    @ForAll("arraysWithRangeShort") WithRange<short[]> bSample
  ) {
    short[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    short[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    short[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    short[] aTst = aRef.clone();
    short[] bTst = bRef.clone();
    short[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeInt(
    @ForAll("arraysWithRangeInt") WithRange<int[]> aSample,
    @ForAll("arraysWithRangeInt") WithRange<int[]> bSample
  ) {
    int[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    int[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    int[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    int[] aTst = aRef.clone();
    int[] bTst = bRef.clone();
    int[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeLong(
    @ForAll("arraysWithRangeLong") WithRange<long[]> aSample,
    @ForAll("arraysWithRangeLong") WithRange<long[]> bSample
  ) {
    long[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    long[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    long[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    long[] aTst = aRef.clone();
    long[] bTst = bRef.clone();
    long[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeChar(
    @ForAll("arraysWithRangeChar") WithRange<char[]> aSample,
    @ForAll("arraysWithRangeChar") WithRange<char[]> bSample
  ) {
    char[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    char[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    char[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    char[] aTst = aRef.clone();
    char[] bTst = bRef.clone();
    char[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeFloat(
    @ForAll("arraysWithRangeFloat") WithRange<float[]> aSample,
    @ForAll("arraysWithRangeFloat") WithRange<float[]> bSample
  ) {
    float[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    float[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    float[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    float[] aTst = aRef.clone();
    float[] bTst = bRef.clone();
    float[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewArrayWithRangeDouble(
    @ForAll("arraysWithRangeDouble") WithRange<double[]> aSample,
    @ForAll("arraysWithRangeDouble") WithRange<double[]> bSample
  ) {
    double[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1);
    double[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1);
    double[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef);

    double[] aTst = aRef.clone();
    double[] bTst = bRef.clone();
    double[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }



  @Property
  default void mergeNewComparatorArrayWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> aSample,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorByte cmp = Byte::compare;
    if( reversed ) cmp=cmp.reversed();

    byte[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    byte[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    byte[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    byte[] aTst = aRef.clone();
    byte[] bTst = bRef.clone();
    byte[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeShort(
    @ForAll("arraysWithRangeShort") WithRange<short[]> aSample,
    @ForAll("arraysWithRangeShort") WithRange<short[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorShort cmp = Short::compare;
    if( reversed )  cmp=cmp.reversed();

    short[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    short[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    short[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    short[] aTst = aRef.clone();
    short[] bTst = bRef.clone();
    short[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeInt(
   @ForAll("arraysWithRangeInt") WithRange<int[]> aSample,
   @ForAll("arraysWithRangeInt") WithRange<int[]> bSample,
   @ForAll boolean reversed
  ) {
    ComparatorInt cmp = Integer::compare;
    if(reversed)  cmp=cmp.reversed();

    int[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    int[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    int[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    int[] aTst = aRef.clone();
    int[] bTst = bRef.clone();
    int[] cTst = merger().merge(
            aTst, a0, a1-a0,
            bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeLong(
    @ForAll("arraysWithRangeLong") WithRange<long[]> aSample,
    @ForAll("arraysWithRangeLong") WithRange<long[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorLong   cmp = Long::compare;
    if(reversed) cmp=cmp.reversed();

    long[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    long[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    long[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    long[] aTst = aRef.clone();
    long[] bTst = bRef.clone();
    long[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeChar(
    @ForAll("arraysWithRangeChar") WithRange<char[]> aSample,
    @ForAll("arraysWithRangeChar") WithRange<char[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorChar cmp = Character::compare;
    if(reversed) cmp=cmp.reversed();

    char[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    char[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    char[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    char[] aTst = aRef.clone();
    char[] bTst = bRef.clone();
    char[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeFloat(
    @ForAll("arraysWithRangeFloat") WithRange<float[]> aSample,
    @ForAll("arraysWithRangeFloat") WithRange<float[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorFloat cmp = Float::compare;
    if(reversed) cmp=cmp.reversed();

    float[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    float[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    float[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    float[] aTst = aRef.clone();
    float[] bTst = bRef.clone();
    float[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }

  @Property
  default void mergeNewComparatorArrayWithRangeDouble(
    @ForAll("arraysWithRangeDouble") WithRange<double[]> aSample,
    @ForAll("arraysWithRangeDouble") WithRange<double[]> bSample,
    @ForAll boolean reversed
  ) {
    ComparatorDouble cmp = Double::compare;
    if(reversed) cmp=cmp.reversed();

    double[] aRef = aSample.getData().clone(); int a0 = aSample.getFrom(), a1 = aSample.getUntil(); Arrays.sort(aRef,a0,a1); if(reversed) revert(aRef,a0,a1);
    double[] bRef = bSample.getData().clone(); int b0 = bSample.getFrom(), b1 = bSample.getUntil(); Arrays.sort(bRef,b0,b1); if(reversed) revert(bRef,b0,b1);
    double[] cRef = concat(
      copyOfRange(aRef,a0,a1),
      copyOfRange(bRef,b0,b1)
    );
    Arrays.sort(cRef); if(reversed) revert(cRef);

    double[] aTst = aRef.clone();
    double[] bTst = bRef.clone();
    double[] cTst = merger().merge(
      aTst, a0, a1-a0,
      bTst, b0, b1-b0, cmp
    );

    assertThat(aTst).isEqualTo(aRef);
    assertThat(bTst).isEqualTo(bRef);
    assertThat(cTst).isEqualTo(cRef);
  }
}
