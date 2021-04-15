package com.github.jaaa.search;

import com.github.jaaa.ComparatorByte;
import com.github.jaaa.ComparatorInt;
import com.github.jaaa.misc.Revert;
import com.github.jaaa.WithRange;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.util.IMath.*;
import static java.lang.Math.min;
import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class SearcherTestTemplate
{
// STATIC FIELDS
  static final int N_TRIES = 10_000,
                  MAX_SIZE = 8192;

  private static abstract class CountingComparator<T> implements Comparator<T>
  {
    public long nComps = 0;
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

  @Property( tries = N_TRIES )
  void searchComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.search( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.search( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt;
    else
        assertThat(1L*sqrt*sqrt).isEqualTo(sqr);
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );

  }

  @Property( tries = N_TRIES )
  void searchComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + ~searcher.search( 0,sqr+1, x -> sqr < 1L*x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchLComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.searchL( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchLComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchL( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt;
    else
        assertThat(1L*sqrt*sqrt).isEqualTo(sqr);
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchLComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + ~searcher.searchL( 0,sqr+1, x -> sqr < 1L*x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchRComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.searchR( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out-1).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchRComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchR( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    if( sqrt < 0 )
      sqrt = ~sqrt;
    else {
      sqrt -= 1;
      assertThat(1L*sqrt*sqrt).isEqualTo(sqr);
    }
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchRComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchR( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    if( sqrt < 0 )
        sqrt = ~sqrt - 1;
    else {
        sqrt -= 1;
        assertThat(1L*sqrt*sqrt).isEqualTo(sqr);
    }
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.search(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }


  @Property( tries = N_TRIES )
  void searchArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.search(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.search(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
        i = ~i;
      if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.search(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.search(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    Comparator<Integer>cmp = naturalOrder();
    if( reversed )     cmp = cmp.reversed();
    Arrays.sort(array, cmp);

    Integer[] input = array.clone();

    int i = searcher.search(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.search(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysComparatorInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.search(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
      if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
            until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.search(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.search(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.search(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.search(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i]).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.search(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparatorObjInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.search(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
            until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;

    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.search(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchArraysWithRangeComparatorInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;

    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.search(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
      if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan   (key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.searchR(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.searchR(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.searchR(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.searchR(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp::compare).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysComparatorInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp::compare).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.searchR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array,  cmp);

    Integer[] input = array.clone();

    int i = searcher.searchR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(key);

    if( i < array.length ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.searchR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < until ) assertThat(array[i]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.searchR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < until ) assertThat(array[i]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.searchR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < until ) assertThat(array[i]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.searchR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).isLessThan   (key);
    }
    else
      assertThat(array[i-1]).isEqualTo(key);

    if( i < until ) assertThat(array[i]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp::compare).isEqualTo(key);

    if( i < until ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparatorInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp::compare).isEqualTo(key);

    if( i < until ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.searchR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(key);

    if( i < until ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchRArraysWithRangeComparatorObjInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer>      cmp = naturalOrder();
    if( reversed )           cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.searchR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
    }
    else
      assertThat(array[i-1]).usingComparator(cmp).isEqualTo(key);

    if( i < until ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.searchL(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.searchL(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.searchL(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.searchL(input,key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysComparatorInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.searchL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array,  cmp);

    Integer[] input = array.clone();

    int i = searcher.searchL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < array.length ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);

    if( i > 0 ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.searchL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.searchL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.searchL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.searchL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).isGreaterThan(key);
    }
    else
      assertThat(array[i]).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparatorInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).usingComparator(cmp::compare).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp::compare).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.searchL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchLArraysWithRangeComparatorObjInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer>      cmp = naturalOrder();
    if( reversed )           cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.searchL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < 0 ) {
      i = ~i;
      if( i < until ) assertThat(array[i]).usingComparator(cmp).isGreaterThan(key);
    }
    else
      assertThat(array[i]).usingComparator(cmp).isEqualTo(key);

    if( i > from ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.search(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.search(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.search(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.search(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



    //============//
   // SEARCH GAP //
  //============//
  @Property( tries = N_TRIES )
  void searchGapComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.searchGap( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchGap( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );

  }

  @Property( tries = N_TRIES )
  void searchGapComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGap( 0,sqr+1, x -> sqr < 1L*x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapLComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.searchGapL( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapLComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = searcher.searchGapL( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchGapLComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGapL( 0,sqr+1, x -> sqr < 1L*x*x ? -1 : +1 );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapRComputesSqrt( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqrt )
  {
    long sqr = 1L*sqrt*sqrt,
         out = searcher.searchGapR( 0, (int) min(Integer.MAX_VALUE, sqr+1), x -> sign(sqr - 1L*x*x) );
    assertThat(out-1).isEqualTo(sqrt);
  }

  @Property( tries = N_TRIES )
  void searchGapRComputesSqrtCeil( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = +1 - searcher.searchGapR( -sqr,1, x -> sign(1L*x*x - sqr) );
    assertThat(sqrt).isEqualTo( sqrtCeil(sqr) );
  }

  @Property( tries = N_TRIES )
  void searchGapRComputesSqrtFloor( @ForAll @IntRange(min=0, max=Integer.MAX_VALUE-1) int sqr )
  {
    int sqrt = -1 + searcher.searchGapR( 0,sqr+1, x -> sign(sqr - 1L*x*x) );
    assertThat(sqrt).isEqualTo( sqrtFloor(sqr) );
  }



  @Property( tries = N_TRIES )
  void searchGapArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.searchGap(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.searchGap(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.searchGap(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.searchGap(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGap(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysComparatorInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGap(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGap(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array,  cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGap(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
            until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparatorInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
  }

  @Property( tries = N_TRIES )
  void searchGapArraysWithRangeComparatorObjInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer>      cmp = naturalOrder();
    if( reversed )           cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGap(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.searchGapR(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.searchGapR(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.searchGapR(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.searchGapR(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGapR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysComparatorInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGapR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGapR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array,  cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGapR(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparatorInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
            until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapRArraysWithRangeComparatorObjInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer>      cmp = naturalOrder();
    if( reversed )           cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGapR(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);
    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    byte[] input = array.clone();

    int i = searcher.searchGapL(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);
    int[] input = array.clone();

    int i = searcher.searchGapL(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysComparableByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);
    Byte[] input = array.clone();

    int i = searcher.searchGapL(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysComparableInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);
    Integer[] input = array.clone();

    int i = searcher.searchGapL(input,key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysComparatorByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGapL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysComparatorInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key, @ForAll boolean reversed )
  {
    array = array.clone();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array);
    if( reversed ) {
      Revert.revert(array);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGapL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysComparatorObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array,cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGapL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysComparatorObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key, @ForAll boolean reversed )
  {
    array = array.clone();

    Comparator<Integer> cmp = naturalOrder();
    if( reversed )      cmp = cmp.reversed();
    Arrays.sort(array,  cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGapL(input,key, cmp);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(0,array.length);
    if( i < array.length ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > 0            ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    byte[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    int[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparableByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Byte[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparableInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    Integer[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key);
    assertThat(input).isEqualTo(array);

    assertThat(i).isBetween(from,until);
    if( i < until ) assertThat(array[i  ]).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparatorByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key, @ForAll boolean reversed )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    ComparatorByte cmp = Byte::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    byte[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparatorInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key, @ForAll boolean reversed )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    ComparatorInt cmp = Integer::compare;
    Arrays.sort(array, from,until);
    if( reversed ) {
      Revert.revert(array, from,until);
      cmp = cmp.reversed();
    }

    int[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < until ) assertThat(array[i  ]).usingComparator(cmp::compare).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp::compare).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparatorObjByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Byte[]> arrayWithRange, @ForAll Byte key, @ForAll boolean reversed )
  {
    Byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Comparator<Byte>  cmp = naturalOrder();
    if( reversed )    cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Byte[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }

  @Property( tries = N_TRIES )
  void searchGapLArraysWithRangeComparatorObjInteger( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Integer[]> arrayWithRange, @ForAll Integer key, @ForAll boolean reversed )
  {
    Integer[] array = arrayWithRange.getData().clone();
    int        from = arrayWithRange.getFrom(),
              until = arrayWithRange.getUntil();

    Comparator<Integer>      cmp = naturalOrder();
    if( reversed )           cmp = cmp.reversed();
    Arrays.sort(array, from,until, cmp);

    Integer[] input = array.clone();

    int i = searcher.searchGapL(input, from,until, key, cmp);
    assertThat(input).isEqualTo(array);

    if( i < until ) assertThat(array[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
    if( i > from  ) assertThat(array[i-1]).usingComparator(cmp).isLessThan(key);
  }



  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] array, @ForAll byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] array, @ForAll int key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array,key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array,key, cmp);
    if( i < 0 )
        i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitObjByte( @ForAll @Size(min=0, max=MAX_SIZE) Byte[] array, @ForAll Byte key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array,key, cmp);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitObjInteger( @ForAll @Size(min=0, max=MAX_SIZE) Integer[] array, @ForAll Integer key )
  {
    array = array.clone();

    Arrays.sort(array);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
      ++nComps;
      return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array,key, cmp);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(0,array.length, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGap(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapRSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapR(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchGapLSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchGapL(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.search(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.search(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchRSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchR(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }



  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> arrayWithRange, @ForAll byte key )
  {
    byte[] array = arrayWithRange.getData().clone();
    int     from = arrayWithRange.getFrom(),
           until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Byte>() {
      @Override public int compare( Byte x, Byte y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }

  @Property( tries = N_TRIES )
  void searchLSatisfiesComparisonLimitWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> arrayWithRange, @ForAll int key )
  {
    int[] array = arrayWithRange.getData().clone();
    int    from = arrayWithRange.getFrom(),
          until = arrayWithRange.getUntil();

    Arrays.sort(array, from,until);

    var cmp = new CountingComparator<Integer>() {
      @Override public int compare( Integer x, Integer y ) {
        ++nComps;
        return x.compareTo(y);
      }
    };

    int i = searcher.searchL(array, from,until, key, cmp::compare);
    if( i < 0 )
      i = ~i;

    assertThat(cmp.nComps).isLessThanOrEqualTo( comparisonLimit(from,until, i)  );
  }
}
