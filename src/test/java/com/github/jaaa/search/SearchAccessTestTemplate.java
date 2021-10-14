package com.github.jaaa.search;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.CompareAccess;
import com.github.jaaa.WithIndex;
import com.github.jaaa.WithRange;
import com.github.jaaa.misc.Boxing;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class SearchAccessTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 1_000;

  interface SearchAccess
  {
    int search    ( int from, int until, int key );
    int searchR   ( int from, int until, int key );
    int searchL   ( int from, int until, int key );
    int searchGap ( int from, int until, int key );
    int searchGapR( int from, int until, int key );
    int searchGapL( int from, int until, int key );
  }

  private static class Acc<T> implements CompareAccess
  {
  // FIELDS
    public final Comparator<? super T> cmp;
    public final T[] vals;
    public final int from, until, iKey;
    public long nComps=0;
  // CONSTRUCTORS
    public Acc( WithRange<WithIndex<T[]>> sample, Comparator<? super T> _cmp ) {
      cmp  =_cmp;
      vals = sample.getData().getData();
      iKey = sample.getData().getIndex();
      from = sample.getFrom();
      until= sample.getUntil();
    }
  // METHODS
    @Override public int compare( int i, int j ) {
      ++nComps;
      assertThat(iKey).isIn(i,j);
      if( i==iKey ) assertThat(j).isBetween(from,until-1);
      if( j==iKey ) assertThat(i).isBetween(from,until-1);
      return cmp.compare(vals[i],vals[j]);
    }
  }

  public interface TemplateRaw extends ArrayProviderTemplate
  {
    @Property default                         void findsItem_Boolean(@ForAll("arraysWithIndexBoolean") WithIndex<boolean[]> sample, @ForAll Comparator<Boolean  > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Byte   ( @ForAll("arraysWithIndexByte"   ) WithIndex<   byte[]> sample, @ForAll Comparator<Byte     > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Short  ( @ForAll("arraysWithIndexShort"  ) WithIndex<  short[]> sample, @ForAll Comparator<Short    > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Int    ( @ForAll("arraysWithIndexInt"    ) WithIndex<    int[]> sample, @ForAll Comparator<Integer  > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Long   ( @ForAll("arraysWithIndexLong"   ) WithIndex<   long[]> sample, @ForAll Comparator<Long     > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Char   ( @ForAll("arraysWithIndexChar"   ) WithIndex<   char[]> sample, @ForAll Comparator<Character> cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Float  ( @ForAll("arraysWithIndexFloat"  ) WithIndex<  float[]> sample, @ForAll Comparator<Float    > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    @Property default                         void findsItem_Double ( @ForAll("arraysWithIndexDouble" ) WithIndex< double[]> sample, @ForAll Comparator<Double   > cmp ) { findsItem(sample.map(Boxing::boxed),cmp); }
    default <T extends Comparable<? super T>> void findsItem( WithIndex<T[]> sample, Comparator<? super T> cmp )
    {
      findsIndex( new WithRange<>(0,sample.getData().length,sample), cmp );
    }

    @Property default                 void findsIndex_Boolean( @ForAll("arraysWithIndexWithRangeBoolean") WithRange< WithIndex<boolean[]>> sample, @ForAll Comparator<Boolean  > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Byte   ( @ForAll("arraysWithIndexWithRangeByte"   ) WithRange< WithIndex<   byte[]>> sample, @ForAll Comparator<Byte     > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Short  ( @ForAll("arraysWithIndexWithRangeShort"  ) WithRange< WithIndex<  short[]>> sample, @ForAll Comparator<Short    > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Int    ( @ForAll("arraysWithIndexWithRangeInt"    ) WithRange< WithIndex<    int[]>> sample, @ForAll Comparator<Integer  > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Long   ( @ForAll("arraysWithIndexWithRangeLong"   ) WithRange< WithIndex<   long[]>> sample, @ForAll Comparator<Long     > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Char   ( @ForAll("arraysWithIndexWithRangeChar"   ) WithRange< WithIndex<   char[]>> sample, @ForAll Comparator<Character> cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Float  ( @ForAll("arraysWithIndexWithRangeFloat"  ) WithRange< WithIndex<  float[]>> sample, @ForAll Comparator<Float    > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    @Property default                 void findsIndex_Double ( @ForAll("arraysWithIndexWithRangeDouble" ) WithRange< WithIndex< double[]>> sample, @ForAll Comparator<Double   > cmp ) { findsIndex(sample.map(x->x.map(Boxing::boxed)),cmp); }
    <T extends Comparable<? super T>> void findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private abstract class TstTmpl implements TemplateRaw
  {
    @Override public int maxArraySize() { return SearchAccessTestTemplate.this.maxArraySize(); }
    @Override public <T extends Comparable<? super T>> void findsIndex( WithRange<WithIndex<T[]>> sample, Comparator<? super T> cmp )
    {
      var acc = new SearchAccessTestTemplate.Acc<>(sample, cmp);

      assertThat(acc.from ).isBetween(       0,acc.vals.length);
      assertThat(acc.until).isBetween(acc.from,acc.vals.length);
      assertThat(acc.iKey ).isBetween(       0,acc.vals.length);

      Arrays.sort(acc.vals, acc.from,acc.until, cmp);
      var valCpy = acc.vals.clone();

      int i = testImpl(createAccess(acc), cmp, acc.vals, acc.from, acc.until, acc.iKey);
      assertThat(i).isNotNegative();

      assertThat(acc.vals).isEqualTo(valCpy);
      assertThat(acc.nComps).isBetween(0L, comparisonLimit(acc.from,acc.until, i));
    }

    abstract <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey );
  }

// CONSTRUCTORS

// METHODS
  abstract SearchAccess createAccess( CompareAccess acc );
  abstract long comparisonLimit( int from, int until, int i );

  protected int maxArraySize() { return 8192; }

  @PropertyDefaults( tries = N_TRIES )
  @Group class Search extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.search(from,until, iKey);

      var key = vals[iKey];
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
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.searchL(from,until, iKey);

      var key = vals[iKey];
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
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.searchR(from,until, iKey);

      var key = vals[iKey];
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
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.searchGap(from,until, iKey);
      assertThat(i).isBetween(from,until);

      var key = vals[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).   isLessThanOrEqualTo(key);

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchGapL extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.searchGapL(from,until, iKey);
      assertThat(i).isBetween(from,until);

      var key = vals[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThanOrEqualTo(key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).isLessThan            (key);

      return i;
    }
  }

  @PropertyDefaults( tries = N_TRIES )
  @Group class SearchGapR extends TstTmpl
  {
    @Override <T extends Comparable<? super T>> int testImpl( SearchAccess acc, Comparator<? super T> cmp, T[] vals, int from, int until, int iKey )
    {
      int i = acc.searchGapR(from,until, iKey);
      assertThat(i).isBetween(from,until);

      var key = vals[iKey];
      if( i < until ) assertThat(vals[i  ]).usingComparator(cmp).isGreaterThan      (key);
      if( i > from  ) assertThat(vals[i-1]).usingComparator(cmp).isLessThanOrEqualTo(key);

      return i;
    }
  }
}
