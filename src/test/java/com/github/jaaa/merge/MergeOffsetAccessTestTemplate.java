package com.github.jaaa.merge;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.compare.CompareAccess;
import com.github.jaaa.WithRange;
import com.github.jaaa.search.BinarySearch;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple5;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.Concat.concat;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 10_000 )
public abstract class MergeOffsetAccessTestTemplate implements ArrayProviderTemplate
{
// STATIC FIELDS
  public interface MergeOffsetAccess
  {
    int mergeOffset(
      int a0, int aLen,
      int b0, int bLen, int nSkip
    );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS
  @Provide
  static Arbitrary< Tuple5<Integer,Integer, Integer,Integer, int[]> > exhaustiveSamples()
  {
    return MergeOffsetAccessorTestTemplate.exhaustiveSamples().map( ab -> {
      int[] a = ab.get1(); int nA = a.length;
      int[] b = ab.get2(); int nB = b.length;
      return Tuple.of(0,nA, nA,nB, concat(a,b));
    });
  }

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected MergeOffsetAccess createAccess( CompareAccess srtAcc );


  @Property void mergeOffsetArraysIntExhaustive( @ForAll("exhaustiveSamples") Tuple5<Integer,Integer, Integer,Integer, int[]> sample )
  {
    int[] input = sample.get5();
    int      a0 = sample.get1(), aLen = sample.get2(),
             b0 = sample.get3(), bLen = sample.get4();
    mergeOffsetArrays(a0,aLen, b0,bLen, boxed(input), false);
    mergeOffsetArrays(a0,aLen, b0,bLen, boxed(input), true );
  }


  @Property void mergeOffsetArraysByte( @ForAll WithRange<WithRange<byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] input = sample.getData().getData();
    int       a0 = sample          .getFrom(), aLen = sample         .rangeLength(),
              b0 = sample.getData().getFrom(), bLen = sample.getData().rangeLength();
    mergeOffsetArrays(a0,aLen, b0,bLen, boxed(input), reversed);
  }


  @Property void mergeOffsetArraysInt( @ForAll WithRange<WithRange<int[]>> sample, @ForAll boolean reversed )
  {
    int[] input = sample.getData().getData();
    int      a0 = sample          .getFrom(), aLen = sample         .rangeLength(),
             b0 = sample.getData().getFrom(), bLen = sample.getData().rangeLength();
    mergeOffsetArrays(a0,aLen, b0,bLen, boxed(input), reversed);
  }


  private <T extends Comparable<? super T>> void mergeOffsetArrays( int a0, int aLen, int b0, int bLen, T[] input, boolean reversed )
  {
    Comparator<T> cmp = reversed
      ? Comparator.<T>naturalOrder().reversed()
      : Comparator.<T>naturalOrder();

    boolean rangesOverlap = a0 <= b0 && b0 < a0+aLen
                         || b0 <= a0 && a0 < b0+bLen;
    if( rangesOverlap ) {
      Arrays.sort(input, min(a0,b0), max(a0+aLen,b0+bLen), cmp);
    }
    else {
      Arrays.sort(input, a0,a0+aLen, cmp);
      Arrays.sort(input, b0,b0+bLen, cmp);
    }

    @SuppressWarnings("unchecked")
    T[] tst = input.clone(),
        ref = (T[]) Stream.concat(
          stream(input, a0, a0+aLen),
          stream(input, b0, b0+bLen)
        ).sorted(cmp).toArray(Comparable[]::new);

    MergeOffsetAccess acc = createAccess( (i, j) -> cmp.compare(tst[i],tst[j]) );

    int len = aLen+bLen;

    for( int nSkip=0; nSkip <= len; nSkip++ )
    {
      int nA = acc.mergeOffset(a0,aLen, b0,bLen, nSkip);
      int nB = nSkip - nA;

      assertThat(nA).isBetween(0,aLen);
      assertThat(nB).isBetween(0,bLen);

      if( 0 < nA              ) assertThat( BinarySearch.searchL(ref, tst[a0+nA-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nB              ) assertThat( BinarySearch.searchL(ref, tst[b0+nB-1], cmp) ).isBetween(0, nSkip-1);
      if( 0 < nA && nB < bLen ) assertThat( tst[a0+nA-1]).usingComparator(cmp).isLessThanOrEqualTo( tst[b0+nB]);
      if( 0 < nB && nA < aLen ) assertThat( tst[b0+nB-1]).usingComparator(cmp).isLessThan         ( tst[a0+nA]);
      if(           nA < aLen ) assertThat( BinarySearch.searchR(ref, tst[a0+nA], cmp) ).isGreaterThanOrEqualTo(nSkip);
      if(           nB < bLen ) assertThat( BinarySearch.searchR(ref, tst[b0+nB], cmp) ).isGreaterThanOrEqualTo(nSkip);

      if( nSkip < aLen+bLen ) {
             if( nA >= aLen ) assertThat(ref[nSkip]).isEqualTo( tst[b0+nB] );
        else if( nB >= bLen ) assertThat(ref[nSkip]).isEqualTo( tst[a0+nA] );
        else                  assertThat(ref[nSkip]).isIn( tst[a0+nA], tst[b0+nB] );
      }

      if( 0 < nSkip ) {
             if( nA <= 0 ) assertThat(ref[nSkip-1]).isEqualTo(tst[b0+nB-1]);
        else if( nB <= 0 ) assertThat(ref[nSkip-1]).isEqualTo(tst[a0+nA-1]);
        else               assertThat(ref[nSkip-1]).isIn( tst[a0+nA-1], tst[b0+nB-1] );
      }
    }

    assertThat(tst).isEqualTo(input);
  }
}
