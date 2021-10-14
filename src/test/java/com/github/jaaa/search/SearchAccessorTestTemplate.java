package com.github.jaaa.search;

import com.github.jaaa.*;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.jaaa.misc.Boxing.boxed;


public abstract class SearchAccessorTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 1_000;

  interface SearchAccessor<T>
  {
    int search    ( T a, int from, int until, T b, int i );
    int searchR   ( T a, int from, int until, T b, int i );
    int searchL   ( T a, int from, int until, T b, int i );
    int searchGap ( T a, int from, int until, T b, int i );
    int searchGapR( T a, int from, int until, T b, int i );
    int searchGapL( T a, int from, int until, T b, int i );
  }

  private static class Acc<T> implements CompareAccessor<T[]>
  {
    // FIELDS
    public final Comparator<? super T> cmp;
    public final T[] vals, keys;
    public final int from, until, iKey;
    public long nComps=0;
    // CONSTRUCTORS
    public Acc( WithRange<T[]> valSample, WithIndex<T[]> keySample, Comparator<? super T> _cmp ) {
      cmp =_cmp;
      vals  = valSample.getData();
      from  = valSample.getFrom();
      until = valSample.getUntil();
      keys = keySample.getData();
      iKey = keySample.getIndex();
    }
    // METHODS
    @Override public int compare( T[] a, int i, T[] b, int j ) {
      ++nComps;
      if( vals==keys ) {
        assert i==iKey && from <= j && j < until
            || j==iKey && from <= i && i < until;
      }
      else if( a==vals ) { assertThat(b).isSameAs(keys); assertThat(i).isBetween(from,until-1); assertThat(j).isEqualTo(iKey); }
      else               { assertThat(a).isSameAs(keys); assertThat(j).isBetween(from,until-1); assertThat(i).isEqualTo(iKey); assertThat(b).isSameAs(vals); }
      return cmp.compare(a[i],b[j]);
    }
  }

  public interface TemplateRaw extends SearchAccessTestTemplate.TemplateRaw
  {
    @Override default <T extends Comparable<? super T>> void findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
    {
      twoWithRange(sample.map(With::getData), sample.getData(), cmp);
    }

    @Property default                         void twoArrays_Boolean( @ForAll("arraysBoolean") boolean[] vals, @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> keys, @ForAll Comparator<Boolean  > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Byte   ( @ForAll("arraysByte"   )    byte[] vals, @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> keys, @ForAll Comparator<Byte     > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Short  ( @ForAll("arraysShort"  )   short[] vals, @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> keys, @ForAll Comparator<Short    > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Int    ( @ForAll("arraysInt"    )     int[] vals, @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> keys, @ForAll Comparator<Integer  > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Long   ( @ForAll("arraysLong"   )    long[] vals, @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> keys, @ForAll Comparator<Long     > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Char   ( @ForAll("arraysChar"   )    char[] vals, @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> keys, @ForAll Comparator<Character> cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Float  ( @ForAll("arraysFloat"  )   float[] vals, @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> keys, @ForAll Comparator<Float    > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    @Property default                         void twoArrays_Double ( @ForAll("arraysDouble" )  double[] vals, @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> keys, @ForAll Comparator<Double   > cmp ) { twoArrays(boxed(vals), keys.map(Boxing::boxed), cmp); }
    default <T extends Comparable<? super T>> void twoArrays( T[] vals, WithIndex<T[]> keySample, Comparator<? super T> cmp )
    {
      twoWithRange( new WithRange<>(0,vals.length,vals), keySample, cmp );
    }

    @Property default                 void twoWithRange_Boolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> vals, @ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> keys, @ForAll Comparator<Boolean  > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Byte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> vals, @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> keys, @ForAll Comparator<Byte     > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Short  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> vals, @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> keys, @ForAll Comparator<Short    > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Int    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> vals, @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> keys, @ForAll Comparator<Integer  > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Long   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> vals, @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> keys, @ForAll Comparator<Long     > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Char   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> vals, @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> keys, @ForAll Comparator<Character> cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Float  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> vals, @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> keys, @ForAll Comparator<Float    > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    @Property default                 void twoWithRange_Double ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> vals, @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> keys, @ForAll Comparator<Double   > cmp ) { twoWithRange(vals.map(Boxing::boxed), keys.map(Boxing::boxed), cmp); }
    <T extends Comparable<? super T>> void twoWithRange( WithRange<T[]> valSample, WithIndex<T[]> keySample, Comparator<? super T> cmp );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private abstract class TstTmpl implements TemplateRaw
  {
    @Override public int maxArraySize() { return SearchAccessorTestTemplate.this.maxArraySize(); }
    @Override public <T extends Comparable<? super T>> void twoWithRange( WithRange<T[]> valSample, WithIndex<T[]> keySample, Comparator<? super T> cmp )
    {
      var acc = new Acc<>(valSample, keySample, cmp);

      assertThat(acc.from ).isBetween(       0,acc.vals.length);
      assertThat(acc.until).isBetween(acc.from,acc.vals.length);
      assertThat(acc.iKey ).isBetween(       0,acc.keys.length);

      Arrays.sort(acc.vals, acc.from,acc.until, cmp);
      var valCpy = acc.vals.clone();
      var keyCpy = acc.keys.clone();

      int i = testImpl(createAccessor(acc), cmp, acc.vals, acc.from, acc.until, acc.keys, acc.iKey);
      assertThat(i).isNotNegative();

      assertThat(acc.vals).isEqualTo(valCpy);
      assertThat(acc.keys).isEqualTo(keyCpy);
      assertThat(acc.nComps).isBetween(0L, comparisonLimit(acc.from,acc.until, i));
    }

    abstract <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey );
  }

// CONSTRUCTORS

// METHODS
  abstract <T> SearchAccessor<T> createAccessor( CompareAccessor<? super T> cmpAcc );
  abstract long comparisonLimit( int from, int until, int i );

  protected int maxArraySize() { return 8192; }

  @PropertyDefaults( tries = N_TRIES )
  @Group class Search extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.search(vals,from,until, keys,iKey);

      var key = keys[iKey];
      if( i < 0 ) {
          i = ~i;       assertThat(i).isBetween(from,until);
        if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan(key);
        if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThan(key);
      }
      else {
        assertThat(i).isBetween(from,until-1);
        assertThat(vals[i]).usingComparator(cmp).isEqualTo(key);
      }

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchL extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.searchL(vals,from,until, keys,iKey);

      var key = keys[iKey];
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

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchR extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.searchR(vals,from,until, keys,iKey);

      var key = keys[iKey];
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

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchGap extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.searchGap(vals,from,until, keys,iKey);
      assertThat(i).isBetween(from,until);

      var key = keys[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchGapL extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.searchGapL(vals,from,until, keys,iKey);
      assertThat(i).isBetween(from,until);

      var key = keys[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).isLessThan            (key);

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchGapR extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccessor<T[]> acc, Comparator<? super T> cmp, T[] vals, int from, int until, T[] keys, int iKey )
    {
      int i = acc.searchGapR(vals,from,until, keys,iKey);
      assertThat(i).isBetween(from,until);

      var key = keys[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan      (key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);

      return i;
    }
  }
}
