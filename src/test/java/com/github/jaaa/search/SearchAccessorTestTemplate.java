package com.github.jaaa.search;

import com.github.jaaa.CompareAccessor;
import com.github.jaaa.misc.Revert;
import com.github.jaaa.WithIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SearchAccessorTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 10_000,
                          MAX_SIZE = 8192;

  protected static interface SearchAccessor<T>
  {
    int search    ( T a, int from, int until, T b, int i );
    int searchR   ( T a, int from, int until, T b, int i );
    int searchL   ( T a, int from, int until, T b, int i );
    int searchGap ( T a, int from, int until, T b, int i );
    int searchGapR( T a, int from, int until, T b, int i );
    int searchGapL( T a, int from, int until, T b, int i );
  }

  protected static abstract class CountingCompareAccessor<T> implements CompareAccessor<T>
  {
    public long nComps=0;
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc );
  abstract protected long comparisonLimit( int from, int until, int i );

    //
   // SEARCH
  //
  @Property( tries = N_TRIES )
  void searchArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
                assertThat(array[i  ]).usingComparator(cmp).          isEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
                assertThat(array[i  ]).usingComparator(cmp).          isEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until);

    if( i < 0 ) {
        i = ~i;
      if( i < until )
        assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(array[key]);

    if( i > from )
      assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until);

    if( i < 0 ) {
        i = ~i;
      if( i < until )
        assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(array[key]);

    if( i > from )
      assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void search2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(searchArr,0,searchArr.length, keyArray,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(0,searchArr.length);

    if( i < 0 ) {
        i = ~i;
      if( i < searchArr.length )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArray[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArray[key]);

    if( i > 0 )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void search2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArr = keyArrWithIndex.getData();
    int   key    = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(searchArr,0,searchArr.length, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(0,searchArr.length);

    if( i < 0 ) {
        i = ~i;
      if( i < searchArr.length )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i > 0 )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArr[key]);
  }


  @Property( tries = N_TRIES )
  void search2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until);

    if( i < 0 ) {
      i = ~i;
      if( i < until )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i > from )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArr[key]);
  }


  @Property( tries = N_TRIES )
  void search2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.search(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until);

    if( i < 0 ) {
        i = ~i;
      if( i < until )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i > from )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArr[key]);
  }


    //
   // SEARCH_L
  //
  @Property( tries = N_TRIES )
  void searchLArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0           ) assertThat(array[i-1]).usingComparator(cmp).            isLessThan(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchLArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0           ) assertThat(array[i-1]).usingComparator(cmp).            isLessThan(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchLArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until-1);

    if( i < 0 ) {
        i = ~i;
      if( i < until )
        assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(array[key]);

    if( i > from )
      assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchLArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until-1);

    if( i < 0 ) {
        i = ~i;
      if( i < until )
        assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(array[key]);

    if( i > from )
      assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchL2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(searchArr,0,searchArr.length, keyArray,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(0,searchArr.length-1);

    if( i < 0 ) {
      i = ~i;
      if( i < searchArr.length )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArray[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArray[key]);

    if( i > 0 )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArray[key]);
  }

  @Property( tries = N_TRIES )
  void searchL2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArray = keyArrWithIndex.getData();
    int   key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(searchArr,0,searchArr.length, keyArray,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(0,searchArr.length-1);

    if( i < 0 ) {
      i = ~i;
      if( i < searchArr.length )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArray[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArray[key]);

    if( i > 0 )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void searchL2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until-1);

    if( i < 0 ) {
      i = ~i;
      if( i < until )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i > from )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArr[key]);
  }

  @Property( tries = N_TRIES )
  void searchL2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchL(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from,until-1);

    if( i < 0 ) {
      i = ~i;
      if( i < until )
        assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i > from )
      assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArr[key]);
  }


    //
   // SEARCH_R
  //
  @Property( tries = N_TRIES )
  void searchRArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(array,0,array.length, array,key);
    assertThat(i).isBetween(1,array.length);

    if( i > 0           ) assertThat(array[i-1]).usingComparator(cmp).    isEqualTo(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchRArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(array,0,array.length, array,key);
    assertThat(i).isBetween(1,array.length);

    if( i > 0           ) assertThat(array[i-1]).usingComparator(cmp).    isEqualTo(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchRArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from+1,until);

    if( i < 0 ) {
        i = ~i;
      if( i > from )
        assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(array[key]);

    if( i < until )
      assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchRArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(array,from,until, array,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from+1,until);

    if( i < 0 ) {
        i = ~i;
      if( i > from )
        assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(array[key]);

    if( i < until )
      assertThat(array[i]).usingComparator(cmp).isGreaterThan(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchR2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
            ? (x,y) -> - Byte.compare(x,y)
            : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(searchArr,0,searchArr.length, keyArray,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(1,searchArr.length);

    if( i < 0 ) {
        i = ~i;
      if( i > 0 )
        assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArray[key]);
    }
    else
      assertThat(searchArr[i-1]).usingComparator(cmp).isEqualTo(keyArray[key]);

    if( i < searchArr.length )
      assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArray[key]);
  }

  @Property( tries = N_TRIES )
  void searchR2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArray = keyArrWithIndex.getData();
    int   key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(searchArr,0,searchArr.length, keyArray,key);

    if( i < 0 ) assertThat(i).isBetween( ~searchArr.length,~0);
    else        assertThat(i).isBetween(1,searchArr.length);

    if( i < 0 ) {
        i = ~i;
      if( i > 0 )
        assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArray[key]);
    }
    else
      assertThat(searchArr[i-1]).usingComparator(cmp).isEqualTo(keyArray[key]);

    if( i < searchArr.length )
      assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void searchR2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
            ? (x,y) -> - Byte.compare(x,y)
            : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from+1,until);

    if( i < 0 ) {
        i = ~i;
      if( i > from )
        assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i-1]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i < until )
      assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
  }

  @Property( tries = N_TRIES )
  void searchR2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchR(searchArr,from,until, keyArr,key);

    if( i < 0 ) assertThat(i).isBetween(~until,~from);
    else        assertThat(i).isBetween(from+1,until);

    if( i < 0 ) {
        i = ~i;
      if( i > from )
        assertThat(searchArr[i-1]).usingComparator(cmp).isLessThan(keyArr[key]);
    }
    else
      assertThat(searchArr[i-1]).usingComparator(cmp).isEqualTo(keyArr[key]);

    if( i < until )
      assertThat(searchArr[i]).usingComparator(cmp).isGreaterThan(keyArr[key]);
  }


    //
   // SEARCH_GAP
  //
  @Property( tries = N_TRIES )
  void searchGapArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
                assertThat(array[i  ]).usingComparator(cmp).          isEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
                assertThat(array[i  ]).usingComparator(cmp).          isEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGap2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArray[key]);
  }

  @Property( tries = N_TRIES )
  void searchGap2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArray = keyArrWithIndex.getData();
    int   key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void searchGap2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArr[key]);
  }

  @Property( tries = N_TRIES )
  void searchGap2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGap(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArr[key]);
  }


    //
   // SEARCH_GAP_L
  //
  @Property( tries = N_TRIES )
  void searchGapLArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
                assertThat(array[i  ]).usingComparator(cmp). isEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(array,0,array.length, array,key);
    assertThat(i).isBetween(0,array.length-1);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(array[key]);
    assertThat(array[i  ]).usingComparator(cmp). isEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).            isLessThan(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).            isLessThan(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapL2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).            isLessThan(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArray[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapL2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArray = keyArrWithIndex.getData();
    int   key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).            isLessThan(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapL2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).            isLessThan(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArr[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapL2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapL(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).            isLessThan(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(keyArr[key]);
  }


    //
   // SEARCH_GAP_R
  //
  @Property( tries = N_TRIES )
  void searchGapRArraysByte( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().clone();
    int      key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(array,0,array.length, array,key);
    assertThat(i).isBetween(1,array.length);

                          assertThat(array[i-1]).usingComparator(cmp).    isEqualTo(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(array,0,array.length, array,key);
    assertThat(i).isBetween(1,array.length);

    assertThat(array[i-1]).usingComparator(cmp).    isEqualTo(array[key]);
    if( i < array.length) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeByte( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) byte[]>> sample, @ForAll boolean reversed )
  {
    byte[] array = sample.getData().getData().clone();
    int      key = sample.getData().getIndex(),
            from = sample.getFrom(),
           until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).      isGreaterThan(array[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(array,from,until, array,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(array[key]);
    if( i < until) assertThat(array[i  ]).usingComparator(cmp).      isGreaterThan(array[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapR2ArraysByte( @ForAll byte[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    byte[] keyArray = keyArrWithIndex.getData();
    int    key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).      isGreaterThan(keyArray[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapR2ArraysInt( @ForAll int[] searchArr, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    searchArr = searchArr.clone();

    int[] keyArray = keyArrWithIndex.getData();
    int   key      = keyArrWithIndex.getIndex();

    Arrays.sort(searchArr);
    if(reversed) Revert.revert(searchArr);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(searchArr,0,searchArr.length, keyArray,key);
    assertThat(i).isBetween(0,searchArr.length);

    if( i > 0               ) assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArray[key]);
    if( i < searchArr.length) assertThat(searchArr[i  ]).usingComparator(cmp).      isGreaterThan(keyArray[key]);
  }


  @Property( tries = N_TRIES )
  void searchGapR2ArraysWithRangeByte( @ForAll WithRange<byte[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) byte[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    byte[] keyArr =    keyArrWithIndex.getData(),
        searchArr = searchArrWithRange.getData().clone();
    int       key =    keyArrWithIndex.getIndex(),
             from = searchArrWithRange.getFrom(),
            until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Byte> cmp = reversed
      ? (x,y) -> - Byte.compare(x,y)
      : (x,y) -> + Byte.compare(x,y);

    SearchAccessor<byte[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).      isGreaterThan(keyArr[key]);
  }

  @Property( tries = N_TRIES )
  void searchGapR2ArraysWithRangeInt( @ForAll WithRange<int[]> searchArrWithRange, @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> keyArrWithIndex, @ForAll boolean reversed )
  {
    int[] keyArr =    keyArrWithIndex.getData(),
       searchArr = searchArrWithRange.getData().clone();
    int      key =    keyArrWithIndex.getIndex(),
            from = searchArrWithRange.getFrom(),
           until = searchArrWithRange.getUntil();

    Arrays.sort(searchArr, from,until);
    if(reversed) Revert.revert(searchArr, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    SearchAccessor<int[]> accByte = createAccessor( (a,i, b,j) -> cmp.compare(a[i], b[j]) );

    int i = accByte.searchGapR(searchArr,from,until, keyArr,key);
    assertThat(i).isBetween(from,until);

    if( i > from ) assertThat(searchArr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(keyArr[key]);
    if( i < until) assertThat(searchArr[i  ]).usingComparator(cmp).      isGreaterThan(keyArr[key]);
  }


    //
   // COMPARISON LIMITS
  //
  @Property( tries = N_TRIES )
  void searchComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.search(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.search(array,from,until, array,key);
    if( i < 0 )
        i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }


  @Property( tries = N_TRIES )
  void searchRComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchR(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchRComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchR(array,from,until, array,key);
    if( i < 0 )
      i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }


  @Property( tries = N_TRIES )
  void searchLComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchL(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchLComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchL(array,from,until, array,key);
    if( i < 0 )
      i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }



  @Property( tries = N_TRIES )
  void searchGapComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGap(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchGapComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
      ? (x,y) -> - Integer.compare(x,y)
      : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGap(array,from,until, array,key);
    if( i < 0 )
        i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }


  @Property( tries = N_TRIES )
  void searchGapRComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGapR(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchGapRComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGapR(array,from,until, array,key);
    if( i < 0 )
      i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }


  @Property( tries = N_TRIES )
  void searchGapLComparisonLimitArraysInt( @ForAll WithIndex<@Size(min=1, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().clone();
    int     key = sample.getIndex();

    Arrays.sort(array);
    if(reversed) Revert.revert(array);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGapL(array,0,array.length, array,key);

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i) );
  }

  @Property( tries = N_TRIES )
  void searchGapLComparisonLimitArraysWithRangeInt( @ForAll WithRange<WithIndex<@Size(min=1, max=MAX_SIZE) int[]>> sample, @ForAll boolean reversed )
  {
    int[] array = sample.getData().getData().clone();
    int     key = sample.getData().getIndex(),
           from = sample.getFrom(),
          until = sample.getUntil();

    Arrays.sort(array, from,until);
    if(reversed) Revert.revert(array, from,until);

    Comparator<Integer> cmp = reversed
            ? (x,y) -> - Integer.compare(x,y)
            : (x,y) -> + Integer.compare(x,y);

    var counter = new CountingCompareAccessor<int[]>() {
      @Override public int compare( int[] a, int i, int[] b, int j ) { ++nComps; return cmp.compare(a[i], b[j]); }
    };
    var accByte = createAccessor(counter);

    int i = accByte.searchGapL(array,from,until, array,key);
    if( i < 0 )
        i = ~i;

    assertThat(counter.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i) );
  }
}
