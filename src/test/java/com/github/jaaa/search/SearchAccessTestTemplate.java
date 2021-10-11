package com.github.jaaa.search;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.CompareAccess;
import com.github.jaaa.WithIndex;
import com.github.jaaa.WithRange;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 100_000 )
public interface SearchAccessTestTemplate extends ArrayProviderTemplate
{
// STATIC FIELDS
  abstract class CountingCompareAccess implements CompareAccess
  {
    public long nComps=0;
  }

  interface SearchAccess
  {
    int search    ( int from, int until, int key );
    int searchR   ( int from, int until, int key );
    int searchL   ( int from, int until, int key );
    int searchGap ( int from, int until, int key );
    int searchGapR( int from, int until, int key );
    int searchGapL( int from, int until, int key );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  SearchAccess createAccess( CompareAccess acc );

  long comparisonLimit( int from, int until, int i );

  @Override default int maxArraySize() { return 8192; }


  @Property default                         void search_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void search_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { search_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void search_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).search(0,len, key);
    assertThat(i).isBetween(0,len-1);

    if( i > 0 ) assertThat(arr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(arr[key]);
                assertThat(arr[i  ]).usingComparator(cmp).          isEqualTo(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }


  @Property default                         void search_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange< WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange< WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange< WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange< WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange< WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange< WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange< WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void search_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange< WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { search_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void search_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
       from = sample.getFrom(),
      until = sample.getUntil();

    Arrays.sort(arr, from,until, cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).search(from,until, key);

    if( i < 0 ) {
      i = ~i;
      assertThat(i).isBetween(from,until);
      if( i < until ) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThan(arr[key]);
      if( i > from  ) assertThat(arr[i-1]).usingComparator(cmp).isLessThan(arr[key]);
    }
    else {
      assertThat(i).isBetween(from,until-1);
                      assertThat(arr[i  ]).usingComparator(cmp).isEqualTo(arr[key]);
      if( i > from  ) assertThat(arr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(arr[key]);
    }

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }


  @Property default                         void searchL_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchL_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { searchL_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void searchL_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchL(0,len, key);
    assertThat(i).isBetween(0,len-1);

    if( i > 0 ) assertThat(arr[i-1]).usingComparator(cmp).isLessThan(arr[key]);
                assertThat(arr[i  ]).usingComparator(cmp).isEqualTo (arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }


  @Property default                         void searchL_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange<WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange<WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange<WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange<WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange<WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange<WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange<WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchL_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange<WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { searchL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void searchL_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
       from = sample.getFrom(),
      until = sample.getUntil();

    Arrays.sort(arr, from,until, cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchL(from,until, key);

    if( i < 0 ) {
      i = ~i;
      assertThat(i).isBetween(from,until);
      if( i < until )
        assertThat(arr[i]).usingComparator(cmp).isGreaterThan(arr[key]);
    }
    else {
      assertThat(i).isBetween(from,until-1);
      assertThat(arr[i]).usingComparator(cmp).isEqualTo(arr[key]);
    }

    if( i > from )
      assertThat(arr[i-1]).usingComparator(cmp).isLessThan(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }


  @Property default                         void searchR_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchR_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { searchR_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void searchR_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchR(0,len, key);
    assertThat(i).isBetween(1,len);

                  assertThat(arr[i-1]).usingComparator(cmp).    isEqualTo(arr[key]);
    if( i < len ) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThan(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }





  @Property default                         void searchR_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange<WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange<WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange<WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange<WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange<WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange<WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange<WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchR_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange<WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { searchR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void searchR_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
       from = sample.getFrom(),
      until = sample.getUntil();

    Arrays.sort(arr, from,until, cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchR(from,until, key);
    if( i < 0 ) {
      i = ~i;
      assertThat(i).isBetween(from,until);
      if( i > from )
        assertThat(arr[i-1]).usingComparator(cmp).isLessThan(arr[key]);
    }
    else {
      assertThat(i).isBetween(from+1,until);
      assertThat(arr[i-1]).usingComparator(cmp).isEqualTo(arr[key]);
    }

    if( i < until )
      assertThat(arr[i]).usingComparator(cmp).isGreaterThan(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }


  @Property default                         void searchGap_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGap_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { searchGap_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void searchGap_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGap(0,len, key);
    assertThat(i).isBetween(0,len-1);

                  assertThat(arr[i  ]).usingComparator(cmp).          isEqualTo(arr[key]);
    if( i > 0   ) assertThat(arr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }


  @Property default                         void searchGap_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange< WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange< WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange< WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange< WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange< WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange< WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange< WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGap_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange< WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { searchGap_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void searchGap_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
       from = sample.getFrom(),
      until = sample.getUntil();

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGap(from,until, key);
    assertThat(i).isBetween(from,until);

    if( i < until) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(arr[key]);
    if( i > from ) assertThat(arr[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }


  @Property default                         void searchGapL_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapL_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { searchGapL_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void searchGapL_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGapL(0,len, key);
    assertThat(i).isBetween(0,len-1);

                assertThat(arr[i  ]).usingComparator(cmp).isEqualTo (arr[key]);
    if( i > 0 ) assertThat(arr[i-1]).usingComparator(cmp).isLessThan(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }


  @Property default                         void searchGapL_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange< WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange< WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange< WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange< WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange< WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange< WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange< WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapL_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange< WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { searchGapL_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void searchGapL_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
            from = sample.getFrom(),
            until = sample.getUntil();

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGapL(from,until, key);
    assertThat(i).isBetween(from,until);

    if( i < until) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(arr[key]);
    if( i > from ) assertThat(arr[i-1]).usingComparator(cmp).isLessThan            (arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }


  @Property default                         void searchGapR_findsItem_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  @Property default                         void searchGapR_findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { searchGapR_findsItem(sample.map(Boxing::boxed),cmp); }
  default <T extends Comparable<? super T>> void searchGapR_findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().clone();
    int key = sample.getIndex(),
        len = arr.length;

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGapR(0,len, key);
    assertThat(i).isBetween(1,len);

    if( i < len ) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThan(arr[key]);
                  assertThat(arr[i-1]).usingComparator(cmp).isEqualTo    (arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(0,len, i));
  }


  @Property default                         void searchGapR_findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange< WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange< WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange< WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange< WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange< WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange< WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange< WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  @Property default                         void searchGapR_findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange< WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { searchGapR_findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
  default <T extends Comparable<? super T>> void searchGapR_findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
  {
    var arr = sample.getData().getData().clone();
    int key = sample.getData().getIndex(),
       from = sample.getFrom(),
      until = sample.getUntil();

    Arrays.sort(arr,cmp);

    var acc = new CountingCompareAccess() {
      @Override public int compare( int i, int j ) {
      ++nComps;
      return cmp.compare(arr[i],arr[j]);
      }
    };

    int i = createAccess(acc).searchGapR(from,until, key);
    assertThat(i).isBetween(from,until);

    if( i < until) assertThat(arr[i  ]).usingComparator(cmp).isGreaterThan      (arr[key]);
    if( i > from ) assertThat(arr[i-1]).usingComparator(cmp).isLessThanOrEqualTo(arr[key]);

    assertThat(acc.nComps).isBetween(0L, comparisonLimit(from,until, i));
  }
}
