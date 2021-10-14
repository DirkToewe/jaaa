package com.github.jaaa.search;

import com.github.jaaa.*;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.constraints.IntRange;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jaaa.misc.Boxing.boxed;
import static com.github.jaaa.util.IMath.*;
import static java.lang.Math.min;
import static org.assertj.core.api.Assertions.assertThat;
import static java.lang.String.format;
import static java.util.Arrays.stream;


public abstract class SearcherTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 1_000;

  private static class CountingComparator<T> implements Comparator<T> {
    public long nComp = 0;
    private final Comparator<? super T> cmp;
    CountingComparator( Comparator<? super T> _cmp ) { cmp =_cmp; }
    @Override public int compare( T x, T y ) { nComp++; assert 0 < nComp; return cmp.compare(x,y); }
  }

  private static record CountingComparable<T extends Comparable<? super T>>( T cmp, LongAdder counter ) implements Comparable<CountingComparable<? extends T>>
  {
    @Override public int compareTo( CountingComparable<? extends T> o ) {
      counter.increment();
      return cmp.compareTo(o.cmp);
    }
    @Override public String toString() { return format("CountingComparable(%s)", cmp); }
    @Override public boolean equals( Object o ) { throw new AssertionError(); }
    @Override public int hashCode() { throw new AssertionError(); }
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final Searcher searcher;

// CONSTRUCTORS
  public SearcherTestTemplate( Searcher _searcher )
  {
    searcher =_searcher;
  }

// METHODS
  protected abstract long comparisonLimit( int from, int until, int i );

  protected int maxArraySize() { return 8192; }



  @Property( tries = N_TRIES )
  void searchComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
         out = searcher.search( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.search( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt;
    else
        assertThat((long) sqrt*sqrt).isEqualTo(sqr);
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );

  }

  @Property( tries = N_TRIES )
  void searchComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + ~searcher.search( 0,sqr+1, x -> sqr < (long) x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchLComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
         out = searcher.searchL( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchLComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchL( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt;
    else
        assertThat((long) sqrt*sqrt).isEqualTo(sqr);
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchLComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + ~searcher.searchL( 0,sqr+1, x -> sqr < (long) x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchRComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
         out = searcher.searchR( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out-1).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchRComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchR( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    if( sqrt < 0 )
      sqrt = ~sqrt;
    else {
      sqrt -= 1;
      assertThat((long) sqrt*sqrt).isEqualTo(sqr);
    }
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchRComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchR( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt - 1;
    else {
        sqrt -= 1;
        assertThat((long) sqrt*sqrt).isEqualTo(sqr);
    }
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
         out = searcher.searchGap( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchGap( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );

  }

  @Property( tries = N_TRIES )
  void searchGapComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGap( 0,sqr+1, x -> sqr < (long) x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapLComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
         out = searcher.searchGapL( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapLComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchGapL( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchGapLComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGapL( 0,sqr+1, x -> sqr < (long) x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapRComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = (long) sqrt*sqrt,
            out = searcher.searchGapR( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - (long) x*x) );
    assertThat(out-1).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapRComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = sqr+1 - searcher.searchGapR( 0,sqr+1, x -> {
      x-=sqr;
      return sign((long) x*x - sqr);
    });
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchGapRComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGapR( 0,sqr+1, x -> sign(sqr - (long) x*x) );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Group class ByCompass extends SearchAccessorTestTemplate
  {
    @Override public int maxArraySize() { return SearcherTestTemplate.this.maxArraySize(); }
    @Override long comparisonLimit( int from, int until, int i ) { return SearcherTestTemplate.this.comparisonLimit(from,until, i); }
    @Override <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> acc ) {
      return new SearchAccessor<>() {
        @Override public int search    ( T b, int from, int until, T a, int i ) { return searcher.search    (from,until, j -> acc.compare(a,i, b,j)); }
        @Override public int searchL   ( T b, int from, int until, T a, int i ) { return searcher.searchL   (from,until, j -> acc.compare(a,i, b,j)); }
        @Override public int searchR   ( T b, int from, int until, T a, int i ) { return searcher.searchR   (from,until, j -> acc.compare(a,i, b,j)); }
        @Override public int searchGap ( T b, int from, int until, T a, int i ) { return searcher.searchGap (from,until, j -> acc.compare(a,i, b,j)); }
        @Override public int searchGapL( T b, int from, int until, T a, int i ) { return searcher.searchGapL(from,until, j -> acc.compare(a,i, b,j)); }
        @Override public int searchGapR( T b, int from, int until, T a, int i ) { return searcher.searchGapR(from,until, j -> acc.compare(a,i, b,j)); }
      };
    }
  }



  @PropertyDefaults( tries = N_TRIES )
  @Group class BoxedComparable
  {
    abstract class Template implements ArrayProviderTemplate
    {
      @Override public int maxArraySize() { return SearcherTestTemplate.this.maxArraySize(); }

      @Property                         void noRange_idx_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_idx_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()]); }
      @Property                         void noRange_key_Boolean( @ForAll("arraysBoolean") boolean[] vals, @ForAll boolean key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Byte   ( @ForAll("arraysByte"   )    byte[] vals, @ForAll    byte key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Short  ( @ForAll("arraysShort"  )   short[] vals, @ForAll   short key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Int    ( @ForAll("arraysInt"    )     int[] vals, @ForAll     int key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Long   ( @ForAll("arraysLong"   )    long[] vals, @ForAll    long key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Char   ( @ForAll("arraysChar"   )    char[] vals, @ForAll    char key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Float  ( @ForAll("arraysFloat"  )   float[] vals, @ForAll   float key ) { noRange(boxed(vals), key); }
      @Property                         void noRange_key_Double ( @ForAll("arraysDouble" )  double[] vals, @ForAll  double key ) { noRange(boxed(vals), key); }
      <T extends Comparable<? super T>> void noRange( T[] vals, T key )
      {
        Arrays.sort(vals);
        var valCpy = vals.clone();
        noRange_testImpl(vals,key);
        assertThat(vals).isEqualTo(valCpy);
      }
      abstract <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key );

      @Property                         void withRange_idx_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange<WithIndex<boolean[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange<WithIndex<   byte[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange<WithIndex<  short[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange<WithIndex<    int[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange<WithIndex<   long[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange<WithIndex<   char[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange<WithIndex<  float[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_idx_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange<WithIndex< double[]>> sample ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()]); }
      @Property                         void withRange_key_Boolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> vals, @ForAll boolean key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Byte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> vals, @ForAll    byte key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Short  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> vals, @ForAll   short key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Int    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> vals, @ForAll     int key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Long   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> vals, @ForAll    long key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Char   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> vals, @ForAll    char key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Float  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> vals, @ForAll   float key ) { withRange(vals.map(Boxing::boxed), key); }
      @Property                         void withRange_key_Double ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> vals, @ForAll  double key ) { withRange(vals.map(Boxing::boxed), key); }
      <T extends Comparable<? super T>> void withRange( WithRange<T[]> sample, T key )
      {
        Arrays.sort( sample.getData() );
        var vals = sample.getData().clone();
        withRange_testImpl(vals, sample.getFrom(),sample.getUntil(), key);
        assertThat(vals).isEqualTo( sample.getData() );
      }
      abstract <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key );
    }

    class LimitTemplate extends Template
    {
      interface Searcher          { <T extends Comparable<? super T>> int search(T[] arr, T key ); }
      interface SearcherWithRange { <T extends Comparable<? super T>> int search( T[] arr, int from, int until, T key ); }
      private final Searcher          searcher;
      private final SearcherWithRange searcherWithRange;
      LimitTemplate( Searcher _searcher, SearcherWithRange _searcherWithRange ) {
        searcher          =_searcher;
        searcherWithRange =_searcherWithRange;
      }
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        var counter = new LongAdder();
        @SuppressWarnings({"rawtypes", "unchecked"})
        CountingComparable<T>[] ccs = stream(vals).map( x -> new CountingComparable(x,counter) ).toArray(CountingComparable[]::new);

        int        i =           searcher.search(ccs, new CountingComparable<>(key,counter) );
        assertThat(i).isEqualTo( searcher.search(vals,key) );
        if( i < 0 )
            i = ~i;

        assertThat(counter.sum()).isBetween( 0L, comparisonLimit(0,vals.length,i) );
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        var counter = new LongAdder();
        @SuppressWarnings({"rawtypes", "unchecked"})
        CountingComparable<T>[] ccs = stream(vals).map( x -> new CountingComparable(x,counter) ).toArray(CountingComparable[]::new);

        int        i =           searcherWithRange.search(ccs, from,until, new CountingComparable<>(key,counter) );
        assertThat(i).isEqualTo( searcherWithRange.search(vals,from,until, key) );
        if( i < 0 )
            i = ~i;
        assertThat(counter.sum()).isBetween( 0L, comparisonLimit(from,until,i) );
      }
    }

    @Group class Limits_Search     extends LimitTemplate { public Limits_Search    () { super(searcher::search    , searcher::search    ); } }
    @Group class Limits_SearchL    extends LimitTemplate { public Limits_SearchL   () { super(searcher::searchL   , searcher::searchL   ); } }
    @Group class Limits_SearchR    extends LimitTemplate { public Limits_SearchR   () { super(searcher::searchR   , searcher::searchR   ); } }
    @Group class Limits_SearchGap  extends LimitTemplate { public Limits_SearchGap () { super(searcher::searchGap , searcher::searchGap ); } }
    @Group class Limits_SearchGapL extends LimitTemplate { public Limits_SearchGapL() { super(searcher::searchGapL, searcher::searchGapL); } }
    @Group class Limits_SearchGapR extends LimitTemplate { public Limits_SearchGapR() { super(searcher::searchGapR, searcher::searchGapR); } }

    @Group class Search extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.search(vals, key),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
          assertThat(i).isBetween(from,until-1);
          assertThat(vals[i]).isEqualTo(key);
        }
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.search(vals, from,until, key);
        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
          assertThat(i).isBetween(from,until-1);
          assertThat(vals[i]).isEqualTo(key);
        }
      }
    }

    @Group class SearchL extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.searchL(vals, key),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
                         assertThat(i).isBetween(from,until-1);
                         assertThat(vals[i  ]).isEqualTo (key);
          if( from < i ) assertThat(vals[i-1]).isLessThan(key);
        }
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.searchL(vals, from,until, key);
        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
                         assertThat(i).isBetween(from,until-1);
                         assertThat(vals[i  ]).isEqualTo (key);
          if( from < i ) assertThat(vals[i-1]).isLessThan(key);
        }
      }
    }

    @Group class SearchR extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.searchR(vals, key),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
                          assertThat(i).isBetween(from+1,until);
                          assertThat(vals[i-1]).isEqualTo    (key);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
        }
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.searchR(vals, from,until, key);

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).   isLessThan(key);
        }
        else {
                          assertThat(i).isBetween(from+1,until);
                          assertThat(vals[i-1]).isEqualTo    (key);
          if( i < until ) assertThat(vals[i  ]).isGreaterThan(key);
        }
      }
    }

    @Group class SearchGap extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.searchGap(vals, key),
         from = 0,
        until = vals.length;
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThanOrEqualTo(key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.searchGap(vals, from,until, key);
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThanOrEqualTo(key);
      }
    }

    @Group class SearchGapL extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.searchGapL(vals, key),
         from = 0,
        until = vals.length;
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThan         (key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.searchGapL(vals, from,until, key);
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThan         (key);
      }
    }

    @Group class SearchGapR extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key )
      {
        int i = searcher.searchGapR(vals, key),
         from = 0,
        until = vals.length;
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThan         (key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThanOrEqualTo(key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key )
      {
        int i = searcher.searchGapR(vals, from,until, key);
                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).isGreaterThan         (key);
        if( i > from  ) assertThat(vals[i-1]).   isLessThanOrEqualTo(key);
      }
    }
  }



  @PropertyDefaults( tries = N_TRIES )
  @Group class BoxedComparator
  {
    abstract class Template implements ArrayProviderTemplate
    {
      @Override public int maxArraySize() { return SearcherTestTemplate.this.maxArraySize(); }

      @Property                         void noRange_idx_Boolean( @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<  Boolean> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<     Byte> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<    Short> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<  Integer> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<     Long> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<    Float> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_idx_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<   Double> cmp ) { var vals = boxed(sample.getData()); noRange(vals, vals[sample.getIndex()], cmp); }
      @Property                         void noRange_key_Boolean( @ForAll("arraysBoolean") boolean[] vals, @ForAll boolean key, @ForAll Comparator<  Boolean> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Byte   ( @ForAll("arraysByte"   )    byte[] vals, @ForAll    byte key, @ForAll Comparator<     Byte> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Short  ( @ForAll("arraysShort"  )   short[] vals, @ForAll   short key, @ForAll Comparator<    Short> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Int    ( @ForAll("arraysInt"    )     int[] vals, @ForAll     int key, @ForAll Comparator<  Integer> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Long   ( @ForAll("arraysLong"   )    long[] vals, @ForAll    long key, @ForAll Comparator<     Long> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Char   ( @ForAll("arraysChar"   )    char[] vals, @ForAll    char key, @ForAll Comparator<Character> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Float  ( @ForAll("arraysFloat"  )   float[] vals, @ForAll   float key, @ForAll Comparator<    Float> cmp ) { noRange(boxed(vals), key, cmp); }
      @Property                         void noRange_key_Double ( @ForAll("arraysDouble" )  double[] vals, @ForAll  double key, @ForAll Comparator<   Double> cmp ) { noRange(boxed(vals), key, cmp); }
      <T extends Comparable<? super T>> void noRange( T[] vals, T key, Comparator<? super T> cmp )
      {
        Arrays.sort(vals,cmp);
        var copy = vals.clone();
        noRange_testImpl(vals,key,cmp);
        assertThat(vals).isEqualTo(copy);
      }
      abstract <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp );

      @Property                         void withRange_idx_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange<WithIndex<boolean[]>> sample, @ForAll Comparator<  Boolean> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange<WithIndex<   byte[]>> sample, @ForAll Comparator<     Byte> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange<WithIndex<  short[]>> sample, @ForAll Comparator<    Short> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange<WithIndex<    int[]>> sample, @ForAll Comparator<  Integer> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange<WithIndex<   long[]>> sample, @ForAll Comparator<     Long> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange<WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange<WithIndex<  float[]>> sample, @ForAll Comparator<    Float> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_idx_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange<WithIndex< double[]>> sample, @ForAll Comparator<   Double> cmp ) { var vals = boxed(sample.getData().getData()); withRange(sample.map(x -> vals), vals[sample.getData().getIndex()], cmp); }
      @Property                         void withRange_key_Boolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> vals, @ForAll boolean key, @ForAll Comparator<  Boolean> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Byte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> vals, @ForAll    byte key, @ForAll Comparator<     Byte> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Short  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> vals, @ForAll   short key, @ForAll Comparator<    Short> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Int    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> vals, @ForAll     int key, @ForAll Comparator<  Integer> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Long   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> vals, @ForAll    long key, @ForAll Comparator<     Long> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Char   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> vals, @ForAll    char key, @ForAll Comparator<Character> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Float  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> vals, @ForAll   float key, @ForAll Comparator<    Float> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      @Property                         void withRange_key_Double ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> vals, @ForAll  double key, @ForAll Comparator<   Double> cmp ) { withRange(vals.map(Boxing::boxed), key, cmp); }
      <T extends Comparable<? super T>> void withRange( WithRange<T[]> sample, T key, Comparator<? super T> cmp )
      {
        int from = sample.getFrom(),
           until = sample.getUntil();
        Arrays.sort( sample.getData(), from,until, cmp );
        var vals = sample.getData().clone();
        withRange_testImpl(vals,from,until, key, cmp);
        assertThat(vals).isEqualTo( sample.getData() );
      }
      abstract <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp );
    }

    @Group class Search extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.search(vals, key, ctr),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
          assertThat(i).isBetween(from,until-1);
          assertThat(vals[i]).usingComparator(cmp).isEqualTo(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.search(vals, from,until, key, ctr);

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
          assertThat(i).isBetween(from,until-1);
          assertThat(vals[i]).usingComparator(cmp).isEqualTo(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
    }

    @Group class SearchL extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchL(vals, key, ctr),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
                         assertThat(i).isBetween(from,until-1);
                         assertThat(vals[i  ]).usingComparator(cmp).isEqualTo (key);
          if( from < i ) assertThat(vals[i-1]).usingComparator(cmp).isLessThan(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchL(vals, from,until, key, ctr);

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
                         assertThat(i).isBetween(from,until-1);
                         assertThat(vals[i  ]).usingComparator(cmp).isEqualTo (key);
          if( from < i ) assertThat(vals[i-1]).usingComparator(cmp).isLessThan(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
    }

    @Group class SearchR extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchR(vals, key, ctr),
         from = 0,
        until = vals.length;

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
                          assertThat(i).isBetween(from+1,until);
                          assertThat(vals[i-1]).usingComparator(cmp).isEqualTo    (key);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchR(vals, from,until, key, ctr);

        if( i < 0 ) {
            i = ~i;       assertThat(i).isBetween(from,until);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
          if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
        }
        else {
                          assertThat(i).isBetween(from+1,until);
                          assertThat(vals[i-1]).usingComparator(cmp).isEqualTo    (key);
          if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
        }

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
      }
    }

    @Group class SearchGap extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGap(vals, key, ctr),
         from = 0,
        until = vals.length;

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGap(vals, from,until, key, ctr);

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
      }
    }

    @Group class SearchGapL extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGapL(vals, key, ctr),
         from = 0,
        until = vals.length;

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan         (key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGapL(vals, from,until, key, ctr);

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan         (key);
      }
    }

    @Group class SearchGapR extends Template
    {
      @Override <T extends Comparable<? super T>> void noRange_testImpl( T[] vals, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGapR(vals, key, ctr),
                from = 0,
                until = vals.length;

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan         (key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
      }
      @Override <T extends Comparable<? super T>> void withRange_testImpl( T[] vals, int from, int until, T key, Comparator<? super T> cmp )
      {
        var ctr = new CountingComparator<>(cmp);

        int i = searcher.searchGapR(vals, from,until, key, ctr);

        assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));

                        assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan         (key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
      }
    }
  }



  @PropertyDefaults( tries = N_TRIES )
  @Group class Unboxed implements ArrayProviderTemplate
  {
  // STATIC FIELDS
    interface Searcher                       <U,C> { int search( U unboxed,                      C key ); }
    interface SearcherWithRange              <U,C> { int search( U unboxed, int from, int until, C key ); }
    interface SearcherWithComparator         <U,C> { int search( U unboxed,                      C key, Comparator<? super C> cmp ); }
    interface SearcherWithComparatorWithRange<U,C> { int search( U unboxed, int from, int until, C key, Comparator<? super C> cmp ); }
  // STATIC CONSTRUCTOR
  // STATIC METHODS
  // FIELDS
  // CONSTRUCTORS
  // METHODS
    @Override public int maxArraySize() { return SearcherTestTemplate.this.maxArraySize(); }

    <U,C extends Comparable<? super C>> void test( U unboxed, C key, Searcher<U,C> searchUnboxed, Searcher<C[],C> searchBoxed )
    {
      var sample = new WithRange<>(0, Array.getLength(unboxed), unboxed);
      withRange_test(sample, key, (w,x,y,z) -> searchUnboxed.search(w,z), (w,x,y,z) -> searchBoxed.search(w,z));
    }
    @SuppressWarnings("unchecked")
    <U,C extends Comparable<? super C>> void withRange_test( WithRange<U> sample, C key, SearcherWithRange<U,C> searchUnboxed, SearcherWithRange<C[],C> searchBoxed )
    {
      U unboxed = sample.getData();
      int len = Array.getLength(unboxed),
         from = sample.getFrom(),
        until = sample.getUntil();
      var unboxed_class = unboxed.getClass();
      C[] boxed;
      try {
        Arrays.class.getMethod("sort", unboxed_class, int.class, int.class).invoke(null,unboxed, from,until);
        boxed = (C[]) Boxing.class.getDeclaredMethod("boxed", unboxed_class).invoke(null,unboxed);
      } catch( IllegalAccessException|InvocationTargetException|NoSuchMethodException e ) {
        throw new AssertionError(e);
      }
      U backup = (U) Array.newInstance(unboxed_class.getComponentType(), len);
      System.arraycopy(unboxed,0, backup,0, len);

      int        i =           searchUnboxed.search(unboxed, from,until, key);
      assertThat(i).isEqualTo( searchBoxed  .search(  boxed, from,until, key) );

      assertThat(unboxed).isEqualTo(backup);
    }

    <U,C extends Comparable<? super C>> void withComparator_test( U unboxed, C key, Comparator<? super C> cmp, SearcherWithComparator<U,C> searchUnboxed, SearcherWithComparator<C[],C> searchBoxed )
    {
      var sample = new WithRange<>(0, Array.getLength(unboxed), unboxed);
      withComparator_withRange_test(sample, key, cmp, (w,x,y,z,c) -> searchUnboxed.search(w,z,c), (w,x,y,z,c) -> searchBoxed.search(w,z,c));
    }
    @SuppressWarnings("unchecked")
    <U,C extends Comparable<? super C>> void withComparator_withRange_test( WithRange<U> sample, C key, Comparator<? super C> cmp, SearcherWithComparatorWithRange<U,C> searchUnboxed, SearcherWithComparatorWithRange<C[],C> searchBoxed )
    {
      U unboxed = sample.getData();
      int len = Array.getLength(unboxed),
         from = sample.getFrom(),
        until = sample.getUntil();
      var unboxed_class = unboxed.getClass();
      C[] boxed;
      try {
        Arrays.class.getMethod("sort", unboxed_class, int.class, int.class).invoke(null,unboxed, from,until);
        boxed = (C[]) Boxing.class.getMethod("boxed", unboxed_class).invoke(null,unboxed);
      } catch( IllegalAccessException|InvocationTargetException|NoSuchMethodException e ) {
        throw new AssertionError(e);
      }
      U backup = (U) Array.newInstance(unboxed_class.getComponentType(), len);
      System.arraycopy(unboxed,0, backup,0, len);

      var ctr = new CountingComparator<>(cmp);

      int        i =           searchUnboxed.search(unboxed, from,until, key, ctr);
      assertThat(i).isEqualTo( searchBoxed  .search(  boxed, from,until, key, cmp) );

      assertThat(unboxed).isEqualTo(backup);

      if( i < 0 )
          i = ~i;
      assertThat(ctr.nComp).isBetween(0L, comparisonLimit(from,until,i));
    }

    @Group class ArraysByte
    {
      @Property void                search                        ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::search    ,searcher::search    ); }
      @Property void                searchL                       ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::searchL   ,searcher::searchL   ); }
      @Property void                searchR                       ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::searchR   ,searcher::searchR   ); }
      @Property void                searchGap                     ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::searchGap ,searcher::searchGap ); }
      @Property void                searchGapL                    ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::searchGapL,searcher::searchGapL); }
      @Property void                searchGapR                    ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key ) {                                                  test(array, key,                                                   searcher::searchGapR,searcher::searchGapR); }
      @Property void                withIndex_search              ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::search    ,searcher::search    ); }
      @Property void                withIndex_searchL             ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchL   ,searcher::searchL   ); }
      @Property void                withIndex_searchR             ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchR   ,searcher::searchR   ); }
      @Property void                withIndex_searchGap           ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGap ,searcher::searchGap ); }
      @Property void                withIndex_searchGapL          ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGapL,searcher::searchGapL); }
      @Property void                withIndex_searchGapR          ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                   ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGapR,searcher::searchGapR); }
      @Property void                withRange_search              ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::search    ,searcher::search    ); }
      @Property void                withRange_searchL             ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::searchL   ,searcher::searchL   ); }
      @Property void                withRange_searchR             ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::searchR   ,searcher::searchR   ); }
      @Property void                withRange_searchGap           ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::searchGap ,searcher::searchGap ); }
      @Property void                withRange_searchGapL          ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::searchGapL,searcher::searchGapL); }
      @Property void                withRange_searchGapR          ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key ) {                                        withRange_test(sample, key,                                                  searcher::searchGapR,searcher::searchGapR); }
      @Property void                withIndex_withRange_search    ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::search    ,searcher::search    ); }
      @Property void                withIndex_withRange_searchL   ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchL   ,searcher::searchL   ); }
      @Property void                withIndex_withRange_searchR   ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchR   ,searcher::searchR   ); }
      @Property void                withIndex_withRange_searchGap ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGap ,searcher::searchGap ); }
      @Property void                withIndex_withRange_searchGapL( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGapL,searcher::searchGapL); }
      @Property void                withIndex_withRange_searchGapR( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                   ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGapR,searcher::searchGapR); }
      @Property void withComparator_search                        ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.search    (v,    k,c::compare), searcher::search    ); }
      @Property void withComparator_searchL                       ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchL   (v,    k,c::compare), searcher::searchL   ); }
      @Property void withComparator_searchR                       ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchR   (v,    k,c::compare), searcher::searchR   ); }
      @Property void withComparator_searchGap                     ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGap (v,    k,c::compare), searcher::searchGap ); }
      @Property void withComparator_searchGapL                    ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGapL(v,    k,c::compare), searcher::searchGapL); }
      @Property void withComparator_searchGapR                    ( @ForAll(                  "arraysByte")                     byte[]   array,  @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGapR(v,    k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withIndex_search              ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.search    (v,    k,c::compare), searcher::search    ); }
      @Property void withComparator_withIndex_searchL             ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchL   (v,    k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withIndex_searchR             ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchR   (v,    k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withIndex_searchGap           ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGap (v,    k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withIndex_searchGapL          ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGapL(v,    k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withIndex_searchGapR          ( @ForAll(         "arraysWithIndexByte")           WithIndex<byte[]>  sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGapR(v,    k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withRange_search              ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.search    (v,l,r,k,c::compare), searcher::search    ); }
      @Property void withComparator_withRange_searchL             ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchL   (v,l,r,k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withRange_searchR             ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchR   (v,l,r,k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withRange_searchGap           ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGap (v,l,r,k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withRange_searchGapL          ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGapL(v,l,r,k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withRange_searchGapR          ( @ForAll(         "arraysWithRangeByte") WithRange<          byte[]>  sample, @ForAll byte key, @ForAll Comparator<? super Byte> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGapR(v,l,r,k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withIndex_withRange_search    ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.search    (v,l,r,k,c::compare), searcher::search    ); }
      @Property void withComparator_withIndex_withRange_searchL   ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchL   (v,l,r,k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withIndex_withRange_searchR   ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchR   (v,l,r,k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withIndex_withRange_searchGap ( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGap (v,l,r,k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withIndex_withRange_searchGapL( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGapL(v,l,r,k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withIndex_withRange_searchGapR( @ForAll("arraysWithIndexWithRangeByte") WithRange<WithIndex<byte[]>> sample                  , @ForAll Comparator<? super Byte> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGapR(v,l,r,k,c::compare), searcher::searchGapR); }
    }

    @Group class ArraysInt
    {
      @Property void                search                        ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::search    ,searcher::search    ); }
      @Property void                searchL                       ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::searchL   ,searcher::searchL   ); }
      @Property void                searchR                       ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::searchR   ,searcher::searchR   ); }
      @Property void                searchGap                     ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::searchGap ,searcher::searchGap ); }
      @Property void                searchGapL                    ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::searchGapL,searcher::searchGapL); }
      @Property void                searchGapR                    ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key ) {                                                  test(array, key,                                                   searcher::searchGapR,searcher::searchGapR); }
      @Property void                withIndex_search              ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::search    ,searcher::search    ); }
      @Property void                withIndex_searchL             ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchL   ,searcher::searchL   ); }
      @Property void                withIndex_searchR             ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchR   ,searcher::searchR   ); }
      @Property void                withIndex_searchGap           ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGap ,searcher::searchGap ); }
      @Property void                withIndex_searchGapL          ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGapL,searcher::searchGapL); }
      @Property void                withIndex_searchGapR          ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                  ) { var vals = sample.getData();                     test(vals, vals[sample.getIndex()],                                searcher::searchGapR,searcher::searchGapR); }
      @Property void                withRange_search              ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::search    ,searcher::search    ); }
      @Property void                withRange_searchL             ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::searchL   ,searcher::searchL   ); }
      @Property void                withRange_searchR             ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::searchR   ,searcher::searchR   ); }
      @Property void                withRange_searchGap           ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::searchGap ,searcher::searchGap ); }
      @Property void                withRange_searchGapL          ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::searchGapL,searcher::searchGapL); }
      @Property void                withRange_searchGapR          ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key ) {                                        withRange_test(sample, key,                                                  searcher::searchGapR,searcher::searchGapR); }
      @Property void                withIndex_withRange_search    ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::search    ,searcher::search    ); }
      @Property void                withIndex_withRange_searchL   ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchL   ,searcher::searchL   ); }
      @Property void                withIndex_withRange_searchR   ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchR   ,searcher::searchR   ); }
      @Property void                withIndex_withRange_searchGap ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGap ,searcher::searchGap ); }
      @Property void                withIndex_withRange_searchGapL( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGapL,searcher::searchGapL); }
      @Property void                withIndex_withRange_searchGapR( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                  ) { var vals = sample.getData().getData(); withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], searcher::searchGapR,searcher::searchGapR); }
      @Property void withComparator_search                        ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.search    (v,    k,c::compare), searcher::search    ); }
      @Property void withComparator_searchL                       ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchL   (v,    k,c::compare), searcher::searchL   ); }
      @Property void withComparator_searchR                       ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchR   (v,    k,c::compare), searcher::searchR   ); }
      @Property void withComparator_searchGap                     ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGap (v,    k,c::compare), searcher::searchGap ); }
      @Property void withComparator_searchGapL                    ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGapL(v,    k,c::compare), searcher::searchGapL); }
      @Property void withComparator_searchGapR                    ( @ForAll(                  "arraysInt")                     int[]   array,  @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                              withComparator_test(array, key                                                                      , cmp, (v,    k,c) -> searcher.searchGapR(v,    k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withIndex_search              ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.search    (v,    k,c::compare), searcher::search    ); }
      @Property void withComparator_withIndex_searchL             ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchL   (v,    k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withIndex_searchR             ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchR   (v,    k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withIndex_searchGap           ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGap (v,    k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withIndex_searchGapL          ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGapL(v,    k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withIndex_searchGapR          ( @ForAll(         "arraysWithIndexInt")           WithIndex<int[]>  sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData(); withComparator_test(vals, vals[sample.getIndex()]                                                   , cmp, (v,    k,c) -> searcher.searchGapR(v,    k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withRange_search              ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.search    (v,l,r,k,c::compare), searcher::search    ); }
      @Property void withComparator_withRange_searchL             ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchL   (v,l,r,k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withRange_searchR             ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchR   (v,l,r,k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withRange_searchGap           ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGap (v,l,r,k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withRange_searchGapL          ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGapL(v,l,r,k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withRange_searchGapR          ( @ForAll(         "arraysWithRangeInt") WithRange<          int[]>  sample, @ForAll int key, @ForAll Comparator<? super Integer> cmp ) {                                        withComparator_withRange_test(sample, key                                                 , cmp, (v,l,r,k,c) -> searcher.searchGapR(v,l,r,k,c::compare), searcher::searchGapR); }
      @Property void withComparator_withIndex_withRange_search    ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.search    (v,l,r,k,c::compare), searcher::search    ); }
      @Property void withComparator_withIndex_withRange_searchL   ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchL   (v,l,r,k,c::compare), searcher::searchL   ); }
      @Property void withComparator_withIndex_withRange_searchR   ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchR   (v,l,r,k,c::compare), searcher::searchR   ); }
      @Property void withComparator_withIndex_withRange_searchGap ( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGap (v,l,r,k,c::compare), searcher::searchGap ); }
      @Property void withComparator_withIndex_withRange_searchGapL( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGapL(v,l,r,k,c::compare), searcher::searchGapL); }
      @Property void withComparator_withIndex_withRange_searchGapR( @ForAll("arraysWithIndexWithRangeInt") WithRange<WithIndex<int[]>> sample                 , @ForAll Comparator<? super Integer> cmp ) { var vals = sample.getData().getData(); withComparator_withRange_test(sample.map(With::getData), vals[sample.getData().getIndex()], cmp, (v,l,r,k,c) -> searcher.searchGapR(v,l,r,k,c::compare), searcher::searchGapR); }
    }
  }
}
