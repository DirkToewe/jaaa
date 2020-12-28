package com.github.jaaa.merge;

import com.github.jaaa.*;
import com.github.jaaa.misc.Revert;
import com.github.jaaa.search.BinarySearch;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.lang.Long.bitCount;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class MergeOffsetAccessorTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 100_000,
                          MAX_SIZE = 4096;

  public static interface MergeOffsetAccessor<T>
  {
    public int mergeOffset( T a, int a0, int aLen,
                            T b, int b0, int bLen, int nSkip );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected <T> MergeOffsetAccessor<T> createAccessor(CompareAccessor<T> srtAcc );


  @Provide
  Arbitrary<Tuple2<int[],int[]>> exhaustiveSamples()
  {
    return Arbitraries.integers().between(1,10).flatMap( len -> {
      if( 1 > len || len >= 32 )
        throw new AssertionError();

      return Arbitraries.longs().between(0,(1 << 2*len-1)-1).map( bits -> {
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


  @Property( tries = Integer.MAX_VALUE )
  void mergeSkipArraysIntExhaustive( @ForAll("exhaustiveSamples") Tuple2<int[],int[]> ab )
  {
    mergeSkipArraysInt(ab.get1(), ab.get2(), false);
  }


  @Property( tries = N_TRIES )
  void mergeSkipArraysByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRef,
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
    };

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property( tries = N_TRIES )
  void mergeSkipArraysInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRef,
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
    };

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property( tries = N_TRIES )
  void mergeSkipArraysTupleByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRefRaw,
    @ForAll boolean reversed
  )
  {
    int aLen = aRefRaw.length,
        bLen = bRefRaw.length;

    Comparator<Tuple2<Byte,Integer>> cmp; {
      Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      if( reversed )            _cmp = _cmp.reversed();
                           cmp =_cmp;
    }

    Tuple2<Byte,Integer>[]
      aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],     i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],aLen+i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef =                          Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new);

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<Tuple2<Byte,Integer>[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

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

  @Property( tries = N_TRIES )
  void mergeSkipArraysTupleInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRefRaw,
    @ForAll boolean reversed
  )
  {
    int aLen = aRefRaw.length,
        bLen = bRefRaw.length;

    Comparator<Tuple2<Integer,Integer>> cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
    if( reversed )               _cmp = _cmp.reversed();
                            cmp =_cmp;
  }

    Tuple2<Integer,Integer>[]
      aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],     i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],aLen+i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef =                          Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new);

    var aTest = aRef.clone();
    var bTest = bRef.clone();

    MergeOffsetAccessor<Tuple2<Integer,Integer>[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

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



  @Property( tries = N_TRIES )
  void mergeSkipArraysWithRangeByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    byte[] aRef = aRefWithRange.getData(),
           bRef = bRefWithRange.getData();
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
    };

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property( tries = N_TRIES )
  void mergeSkipArraysWithRangeInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    int[] aRef = aRefWithRange.getData(),
          bRef = bRefWithRange.getData();
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
    };

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
  }

  @Property( tries = N_TRIES )
  void mergeSkipArraysWithRangeTupleByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      if( reversed )          _cmp = _cmp.reversed();
                         cmp =_cmp;
    }

    Tuple2<Byte,Integer>[] aRef, bRef, cRef; {
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

    MergeOffsetAccessor<Tuple2<Byte,Integer>[]> acc = createAccessor( (a, i, b, j) -> cmp.compare(a[i], b[j]) );

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

  @Property( tries = N_TRIES )
  void mergeSkipArraysWithRangeTupleInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll boolean reversed
  )
  {
    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
    if( reversed )            _cmp = _cmp.reversed();
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
