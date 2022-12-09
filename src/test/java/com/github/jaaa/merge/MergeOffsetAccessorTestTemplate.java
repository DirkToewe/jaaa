package com.github.jaaa.merge;

import com.github.jaaa.*;
import com.github.jaaa.compare.ComparatorByte;
import com.github.jaaa.compare.ComparatorInt;
import com.github.jaaa.compare.CompareAccessor;
import com.github.jaaa.Boxing;
import com.github.jaaa.permute.Revert;
import com.github.jaaa.search.BinarySearch;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.github.jaaa.Boxing.boxed;
import static java.lang.Long.bitCount;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 100_000 )
public abstract class MergeOffsetAccessorTestTemplate implements ArrayProviderTemplate
{
// STATIC FIELDS
  public interface MergeOffsetAccessor<T>
  {
    int mergeOffset( T a, int a0, int aLen,
                     T b, int b0, int bLen, int nSkip );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected <T> MergeOffsetAccessor<T> createAccessor( CompareAccessor<T> srtAcc );


  @Provide
  static Arbitrary<Tuple2<int[],int[]>> exhaustiveSamples()
  {
    return Arbitraries.integers().between(1,10).flatMap( len -> {
      if( 1 > len || len >= 32 )
        throw new AssertionError();

      return Arbitraries.longs().between(0,(1L << 2*len-1)-1).map( bits -> {
        int nA = bitCount(bits & (1L<<len)-1),
            nB = len - nA;

        int[] ab = new int[len];
        for( int i=0; ++i < len; )
          ab[i] = ab[i-1] + (int) (1 & bits>>>len+i-1);

        int[] a = range(0,len).filter( i -> 1 == (1 & bits>>>i) ).map(i -> ab[i]).toArray(),
              b = range(0,len).filter( i -> 0 == (1 & bits>>>i) ).map(i -> ab[i]).toArray();
        if( a.length != nA ) throw new AssertionError();
        if( b.length != nB ) throw new AssertionError();

        return Tuple.of(a,b);
      });
    });
  }


  @Property void mergeOffsetArraysIntExhaustive( @ForAll("exhaustiveSamples") Tuple2<int[],int[]> ab )
  {
    mergeOffsetArraysInt(ab.get1(), ab.get2(), false);
    mergeOffsetArraysInt(ab.get1(), ab.get2(), true );
  }


  @Property void mergeOffsetArraysByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef,
    @ForAll boolean reversed
  )
  {
    int aLen = aRef.length,
        bLen = bRef.length;
    var cRef = new byte[aLen + bLen];

    Arrays.sort(aRef); System.arraycopy(aRef,0, cRef,0,    aLen);
    Arrays.sort(bRef); System.arraycopy(bRef,0, cRef,aLen, bLen);
    Arrays.sort(cRef);

    ComparatorByte cmp = reversed
      ? (x,y) -> -Byte.compare(x,y)
      : (x,y) -> +Byte.compare(x,y);

    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
      Revert.revert(cRef);
    }

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<byte[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    for( int nSkip=0; nSkip <= aLen+bLen; nSkip++ )
    {
      int nA = acc.mergeOffset(
        aTest,0,aLen,
        bTest,0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat(aTest[nA-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(bTest[nB]);
      if( 0 < nB && nA < aLen ) assertThat(bTest[nB-1]).usingComparator(cmp::compare).isLessThan         (aTest[nA]);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[nA], bTest[nB]);
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[nA-1], bTest[nB-1]);
      }
    }

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property void mergeOffsetArraysInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef,
    @ForAll boolean reversed
  )
  {
    int aLen = aRef.length,
        bLen = bRef.length;
    var cRef = new int[aLen + bLen];

    Arrays.sort(aRef); System.arraycopy(aRef,0, cRef,0,    aLen);
    Arrays.sort(bRef); System.arraycopy(bRef,0, cRef,aLen, bLen);
    Arrays.sort(cRef);

    ComparatorInt cmp = reversed
      ? (x,y) -> -Integer.compare(x,y)
      : (x,y) -> +Integer.compare(x,y);

    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
      Revert.revert(cRef);
    }

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<int[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    for( int nSkip=0; nSkip <= aLen+bLen; nSkip++ )
    {
      int nA = acc.mergeOffset(
        aTest,0,aLen,
        bTest,0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat(aTest[nA-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(bTest[nB]);
      if( 0 < nB && nA < aLen ) assertThat(bTest[nB-1]).usingComparator(cmp::compare).isLessThan         (aTest[nA]);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[nA], bTest[nB]);
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[nA-1], bTest[nB-1]);
      }
    }

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }



  @Property                                 void mergeSkipArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] aRef, @ForAll("arraysBoolean") boolean[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleByte   ( @ForAll("arraysByte"   )    byte[] aRef, @ForAll("arraysByte"   )    byte[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleShort  ( @ForAll("arraysShort"  )   short[] aRef, @ForAll("arraysShort"  )   short[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleInt    ( @ForAll("arraysInt"    )     int[] aRef, @ForAll("arraysInt"    )     int[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleLong   ( @ForAll("arraysLong"   )    long[] aRef, @ForAll("arraysLong"   )    long[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleChar   ( @ForAll("arraysChar"   )    char[] aRef, @ForAll("arraysChar"   )    char[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleFloat  ( @ForAll("arraysFloat"  )   float[] aRef, @ForAll("arraysFloat"  )   float[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleDouble ( @ForAll("arraysDouble" )  double[] aRef, @ForAll("arraysDouble" )  double[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergeSkipArraysTupleString ( @ForAll("arraysString" )  String[] aRef, @ForAll("arraysString" )  String[] bRef, @ForAll boolean reversed ) { mergeSkipArraysTuple(      aRef ,       bRef , reversed); }
  private <T extends Comparable<? super T>> void mergeSkipArraysTuple( T[] aRefRaw, T[] bRefRaw, boolean reversed )
  {
    int aLen = aRefRaw.length,
        bLen = bRefRaw.length;

    Comparator<Tuple2<T,Integer>> cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      if( reversed )       _cmp = _cmp.reversed();
      cmp =_cmp;
    }

    Tuple2<T,Integer>[]
      aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],     i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],aLen+i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef =                          Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new);

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<Tuple2<T,Integer>[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    rangeClosed(0,cRef.length).forEach( nSkip -> {
      int nA = acc.mergeOffset(
              aTest,0,aLen,
              bTest,0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat( cmp.compare(aTest[nA-1], bTest[nB]) ).isLessThanOrEqualTo(0);
      if( 0 < nB && nA < aLen ) assertThat( cmp.compare(bTest[nB-1], aTest[nA]) ).isLessThan         (0);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[nA], bTest[nB]);
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[nA-1], bTest[nB-1]);
      }
    });

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }



  @Property void mergeSkipArraysWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> aRefWithRange,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    byte[] aRef = aRefWithRange.getData().clone(),
           bRef = bRefWithRange.getData().clone();
    int a0 = aRefWithRange.getFrom(),
        b0 = bRefWithRange.getFrom(),
      aLen = -a0 + aRefWithRange.getFrom(),
      bLen = -b0 + bRefWithRange.getFrom();

    var cRef = new byte[aLen + bLen];

    Arrays.sort(aRef,a0,a0+aLen); System.arraycopy(aRef,a0, cRef,0,    aLen);
    Arrays.sort(bRef,b0,b0+bLen); System.arraycopy(bRef,b0, cRef,aLen, bLen);
    Arrays.sort(cRef);

    ComparatorByte cmp = reversed
      ? (x,y) -> -Byte.compare(x,y)
      : (x,y) -> +Byte.compare(x,y);

    if( reversed ) {
      Revert.revert(aRef, a0,a0+aLen);
      Revert.revert(bRef, b0,b0+bLen);
      Revert.revert(cRef);
    }

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<byte[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    for( int nSkip=0; nSkip <= aLen+bLen; nSkip++ )
    {
      int nA = acc.mergeOffset(
        aTest,a0,aLen,
        bTest,b0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[a0+nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[b0+nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat(aTest[a0+nA-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(bTest[nB]);
      if( 0 < nB && nA < aLen ) assertThat(bTest[b0+nB-1]).usingComparator(cmp::compare).isLessThan         (aTest[nA]);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[a0+nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[b0+nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[b0+nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[a0+nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[a0+nA], bTest[b0+nB]);
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[b0+nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[a0+nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[a0+nA-1], bTest[b0+nB-1]);
      }
    }

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property void mergeSkipArraysWithRangeInt(
    @ForAll("arraysWithRangeInt") WithRange<int[]> aRefWithRange,
    @ForAll("arraysWithRangeInt") WithRange<int[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    int[] aRef = aRefWithRange.getData().clone(),
          bRef = bRefWithRange.getData().clone();
    int a0 = aRefWithRange.getFrom(),
        b0 = bRefWithRange.getFrom(),
      aLen = -a0 + aRefWithRange.getFrom(),
      bLen = -b0 + bRefWithRange.getFrom();

    var cRef = new int[aLen + bLen];

    Arrays.sort(aRef,a0,a0+aLen); System.arraycopy(aRef,a0, cRef,0,    aLen);
    Arrays.sort(bRef,b0,b0+bLen); System.arraycopy(bRef,b0, cRef,aLen, bLen);
    Arrays.sort(cRef);

    ComparatorInt cmp = reversed
      ? (x,y) -> -Integer.compare(x,y)
      : (x,y) -> +Integer.compare(x,y);

    if( reversed ) {
      Revert.revert(aRef, a0,a0+aLen);
      Revert.revert(bRef, b0,b0+bLen);
      Revert.revert(cRef);
    }

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<int[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    for( int nSkip=0; nSkip <= aLen+bLen; nSkip++ )
    {
      int nA = acc.mergeOffset(
              aTest,a0,aLen,
              bTest,b0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[a0+nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[b0+nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat(aTest[a0+nA-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(bTest[nB]);
      if( 0 < nB && nA < aLen ) assertThat(bTest[b0+nB-1]).usingComparator(cmp::compare).isLessThan         (aTest[nA]);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[a0+nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[b0+nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[b0+nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[a0+nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[a0+nA], bTest[b0+nB]);
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[b0+nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[a0+nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[a0+nA-1], bTest[b0+nB-1]);
      }
    }

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }



  @Property void mergeSkipArraysWithRangeTupleBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> aRef, @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> aRef, @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> aRef, @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> aRef, @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> aRef, @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> aRef, @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> aRef, @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> aRef, @ForAll("arraysWithRangeDouble" ) WithRange< double[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), reversed); }
  @Property void mergeSkipArraysWithRangeTupleString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> aRef, @ForAll("arraysWithRangeString" ) WithRange< String[]> bRef, @ForAll boolean reversed ) { mergeSkipArraysWithRangeTuple(aRef                   , bRef                   , reversed); }
  private <T extends Comparable<? super T>> void mergeSkipArraysWithRangeTuple( WithRange<T[]> aRefWithRange, WithRange<T[]> bRefWithRange, boolean reversed )
  {
    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      if( reversed )             _cmp = _cmp.reversed();
      cmp =_cmp;
    }

    Tuple2<Integer,Integer>[] aRef, bRef, cRef; {
    var aRaw = aRefWithRange.getData();
    var bRaw = bRefWithRange.getData();
    aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],            i) ).toArray(Tuple2[]::new);
    bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],aRaw.length+i) ).toArray(Tuple2[]::new);
    cRef =                           Stream.concat( stream(aRef), stream(bRef) ).toArray(Tuple2[]::new);
  }
    int a0 = aRefWithRange.getFrom(),
            b0 = bRefWithRange.getFrom(),
            aLen = -a0 + aRefWithRange.getFrom(),
            bLen = -b0 + bRefWithRange.getFrom();

    Arrays.sort(aRef, a0,a0+aLen, cmp); System.arraycopy(aRef,a0, cRef,0,    aLen);
    Arrays.sort(bRef, b0,b0+bLen, cmp); System.arraycopy(bRef,b0, cRef,aLen, bLen);
    Arrays.sort(cRef, cmp);

    if( reversed ) {
      Revert.revert(aRef, a0,a0+aLen);
      Revert.revert(bRef, b0,b0+bLen);
      Revert.revert(cRef);
    }

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<Tuple2<Integer,Integer>[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

    for( int nSkip=0; nSkip <= aLen+bLen; nSkip++ )
    {
      int nA = acc.mergeOffset(
              aTest,a0,aLen,
              bTest,b0,bLen, nSkip
      );
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(cRef, aTest[a0+nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(cRef, bTest[b0+nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat( cmp.compare(aTest[a0+nA-1],bTest[nB]) ).isLessThanOrEqualTo(0);
      if( 0 < nB && nA < aLen ) assertThat( cmp.compare(bTest[b0+nB-1],aTest[nA]) ).isLessThan         (0);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(cRef, aTest[a0+nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(cRef, bTest[b0+nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
        if( nA >= aLen ) assertThat(cRef[nSkip]).isEqualTo(bTest[b0+nB]);
        else if( nB >= bLen ) assertThat(cRef[nSkip]).isEqualTo(aTest[a0+nA]);
        else                  assertThat(cRef[nSkip]).isIn(aTest[a0+nA], bTest[b0+nB]);
      }

      if( 0 < nSkip ) {
        if( nA <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(bTest[b0+nB-1]);
        else if( nB <= 0 ) assertThat(cRef[nSkip-1]).isEqualTo(aTest[a0+nA-1]);
        else               assertThat(cRef[nSkip-1]).isIn(aTest[a0+nA-1], bTest[b0+nB-1]);
      }
    };

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }
}
