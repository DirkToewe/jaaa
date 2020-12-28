package com.github.jaaa.search;

import com.github.jaaa.CompareAccess;
import com.github.jaaa.WithIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;

import static java.lang.Math.abs;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class SearchAccessTestTemplate
{
// STATIC FIELDS
  static final int N_TRIES = 10_000,
                  MAX_SIZE = 8192;

  private static abstract class CountingCompareAccess implements CompareAccess
  {
    public long nComps=0;
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected int searchGap ( int from, int until, CompareAccess access, int key );
  abstract protected int searchGapR( int from, int until, CompareAccess access, int key );
  abstract protected int searchGapL( int from, int until, CompareAccess access, int key );

  protected abstract long comparisonLimit( int from, int until, int i );


  @Property( tries = N_TRIES )
  void searchGapArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int      key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGap(0,array.length, (j,k) -> Byte.compare(array[j], array[k]), key);

    assertThat(i).isBetween(0, array.length-1);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGap(0,array.length, (j,k) -> Integer.compare(array[j], array[k]), key);

    assertThat(i).isBetween(0, array.length-1);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapRArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int      key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGapR(0,array.length, (j,k) -> Byte.compare(array[j], array[k]), key);

    assertThat(i).isBetween(1, array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan         (array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGapR(0,array.length, (j,k) -> Integer.compare(array[j], array[k]), key);

    assertThat(i).isBetween(1, array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan         (array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapLArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int      key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGapL(0,array.length, (j,k) -> Byte.compare(array[j], array[k]), key);

    assertThat(i).isBetween(0, array.length-1);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThan         (array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    int i = searchGapL(0,array.length, (j,k) -> Integer.compare(array[j], array[k]), key);

    assertThat(i).isBetween(0, array.length-1);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
    if( i > 0            ) assertThat(array[i-1]).   isLessThan         (array[key]);
  }


//  @Property( tries = N_TRIES )
//  void searchGapArraysWithOffsetByte(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    byte[] array = arrayWithIndex.getData();
//    int      key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGap(offset,offset+array.length, (j,k) -> Byte.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(0, array.length-1);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
//  }
//
//  @Property( tries = N_TRIES )
//  void searchGapArraysWithOffsetInt(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    int[] array = arrayWithIndex.getData();
//    int     key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGap(offset,offset+array.length, (j,k) -> Integer.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(0, array.length-1);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
//  }
//
//
//  @Property( tries = N_TRIES )
//  void searchGapRArraysWithOffsetByte(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    byte[] array = arrayWithIndex.getData();
//    int      key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGapR(offset,offset+array.length, (j,k) -> Byte.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(1, array.length);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThan         (array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
//  }
//
//  @Property( tries = N_TRIES )
//  void searchGapRArraysWithOffsetInt(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    int[] array = arrayWithIndex.getData();
//    int     key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGapR(offset,offset+array.length, (j,k) -> Integer.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(1, array.length);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThan         (array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(array[key]);
//  }
//
//
//  @Property( tries = N_TRIES )
//  void searchGapLArraysWithOffsetByte(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    byte[] array = arrayWithIndex.getData();
//    int      key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGapL(offset,offset+array.length, (j,k) -> Byte.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(0, array.length-1);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThan         (array[key]);
//  }
//
//  @Property( tries = N_TRIES )
//  void searchGapLArraysWithOffsetInt(
//    @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex,
//    @ForAll @IntRange(max=Integer.MAX_VALUE-MAX_SIZE) int offset
//  )
//  {
//    int[] array = arrayWithIndex.getData();
//    int     key = arrayWithIndex.getIndex();
//
//    Arrays.sort(array);
//
//    int i = -offset + searchGapL(offset,offset+array.length, (j,k) -> Integer.compare(array[j-offset], array[k-offset]), key+offset);
//
//    assertThat(i).isBetween(0, array.length-1);
//    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(array[key]);
//    if( i > 0            ) assertThat(array[i-1]).   isLessThan         (array[key]);
//  }



  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Byte.compare(array[i],array[j]);
      }
    };

    int i = searchGap(0,array.length, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGap(0,array.length, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Byte.compare(array[i],array[j]);
      }
    };

    int i = searchGapR(0,array.length, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapR(0,array.length, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData();
    int      key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapL(0,array.length, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData();
    int     key = arrayWithIndex.getIndex();

    Arrays.sort(array);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapL(0,array.length, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData().getData();
    int      key = arrayWithIndex.getData().getIndex(),
            from = arrayWithIndex.getFrom(),
           until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGap(from,until, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData().getData();
    int     key = arrayWithIndex.getData().getIndex(),
           from = arrayWithIndex.getFrom(),
          until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
        ++nComps;
        return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGap(from,until, cmp, key);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchRGapSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData().getData();
    int      key = arrayWithIndex.getData().getIndex(),
            from = arrayWithIndex.getFrom(),
            until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
        ++nComps;
        return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapR(from,until, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchRGapSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData().getData();
    int     key = arrayWithIndex.getData().getIndex(),
            from = arrayWithIndex.getFrom(),
            until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
        ++nComps;
        return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapR(from,until, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchLGapSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> arrayWithIndex )
  {
    byte[] array = arrayWithIndex.getData().getData();
    int      key = arrayWithIndex.getData().getIndex(),
            from = arrayWithIndex.getFrom(),
            until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
        ++nComps;
        return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapL(from,until, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchLGapSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> arrayWithIndex )
  {
    int[] array = arrayWithIndex.getData().getData();
    int     key = arrayWithIndex.getData().getIndex(),
            from = arrayWithIndex.getFrom(),
            until = arrayWithIndex.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
        ++nComps;
        return Integer.compare(array[i],array[j]);
      }
    };

    int i = searchGapL(from,until, cmp, key);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }
}
