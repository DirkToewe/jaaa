package com.github.jaaa.merge;

import com.github.jaaa.compare.ComparatorByte;
import com.github.jaaa.CompareSwapAccess;
import com.github.jaaa.merge.datagen.MergeDataProviderTemplate;
import com.github.jaaa.merge.datagen.MergeInput;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.Boxing;
import com.github.jaaa.util.ZipWithIndex;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.Boxing.unboxed;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 1_000 )
public interface MergeAccessTestTemplate extends MergeDataProviderTemplate
{
// STATIC FIELDS
  boolean isStable();

  interface MergeAccess {
    void merge( int from, int mid, int until );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  MergeAccess createAccess( CompareSwapAccess srtAcc );


  @Property(tries=8_000_000) default void merges_uByte_exhaustive   ( @ForAll("mergeInputs_uByte_exhaustive") MergeInput<byte[]> sample ) { merges_byte(sample); }
  @Property(tries=2_000_000) default void merges_uByte_len13        ( @ForAll("mergeInputs_uByte_len13"     ) MergeInput<byte[]> sample ) { merges_byte(sample); }
  @Property(tries=2_000_000) default void merges_uByte_len14        ( @ForAll("mergeInputs_uByte_len14"     ) MergeInput<byte[]> sample ) { merges_byte(sample); }
  @Property(tries=2_000_000) default void merges_uByte_len15        ( @ForAll("mergeInputs_uByte_len15"     ) MergeInput<byte[]> sample ) { merges_byte(sample); }
  @Property(tries=2_000_000) default void merges_uByte_len16        ( @ForAll("mergeInputs_uByte_len16"     ) MergeInput<byte[]> sample ) { merges_byte(sample); }
  @Property(tries=2_000_000) default void merges_uByte_len17        ( @ForAll("mergeInputs_uByte_len17"     ) MergeInput<byte[]> sample ) { merges_byte(sample); }
  default                            void merges_byte               (                                         MergeInput<byte[]> sample ) {
    byte[] tst = sample.array().clone();
    int mid = sample.mid,
       from = sample.from,
      until = sample.until;

    byte[] ref = tst.clone();
    Arrays.sort(ref, from,until);

    MergeAccess acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return Byte.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }


  @Property(tries=8_000_000) default void mergesStably_uByte_exhaustive   ( @ForAll("mergeInputs_uByte_exhaustive") MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  @Property(tries=2_000_000) default void mergesStably_uByte_len13        ( @ForAll("mergeInputs_uByte_len13"     ) MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  @Property(tries=2_000_000) default void mergesStably_uByte_len14        ( @ForAll("mergeInputs_uByte_len14"     ) MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  @Property(tries=2_000_000) default void mergesStably_uByte_len15        ( @ForAll("mergeInputs_uByte_len15"     ) MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  @Property(tries=2_000_000) default void mergesStably_uByte_len16        ( @ForAll("mergeInputs_uByte_len16"     ) MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  @Property(tries=2_000_000) default void mergesStably_uByte_len17        ( @ForAll("mergeInputs_uByte_len17"     ) MergeInput<byte[]> sample ) { mergesStably_uByte(sample); }
  default                            void mergesStably_uByte              (                                         MergeInput<byte[]> sample ) {
    byte[] tst = sample.array().clone();
    int mid = sample.mid,
       from = sample.from,
      until = sample.until;

    for( int i=from; i < until; i++ )
      assertThat(tst[i]).isNotNegative();

    ComparatorByte cmp;
    if( isStable() )
    {
      for( int i=mid; i < until; i++ )
        tst[i] ^= -1;

      cmp = (x,y) -> {
        x ^= x>>31;
        y ^= y>>31;
        return Byte.compare(x,y);
      };
    }
    else cmp = Byte::compare;

    Byte[] ref = boxed(tst);
    Arrays.sort(ref, from,until, cmp::compare);

    MergeAccess acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return cmp.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo( unboxed(ref) );
  }


  @Property                         default void mergesArr_byte_limitedRanges( @ForAll("mergeInputs_byte_limitedRanges") MergeInput<   byte[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_boolean           ( @ForAll("mergeInputs_boolean"           ) MergeInput<boolean[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_byte              ( @ForAll("mergeInputs_byte"              ) MergeInput<   byte[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_short             ( @ForAll("mergeInputs_short"             ) MergeInput<  short[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_int_v1            ( @ForAll("mergeInputs_int_v1"            ) MergeInput<    int[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_int_v2            ( @ForAll("mergeInputs_int_v2"            ) MergeInput<    int[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_long              ( @ForAll("mergeInputs_long"              ) MergeInput<   long[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_char              ( @ForAll("mergeInputs_char"              ) MergeInput<   char[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_float             ( @ForAll("mergeInputs_float"             ) MergeInput<  float[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  @Property                         default void mergesArr_double            ( @ForAll("mergeInputs_double"            ) MergeInput< double[]> sample ) { mergesArr( sample.map(Boxing::boxed) ); }
  default <T extends Comparable<? super T>> void mergesArr                   (                                           MergeInput<      T[]> sample )
  {

    int mid = sample.mid,
       from = sample.from,
      until = sample.until;

    T[] tst = sample.array().clone(); Arrays.sort(tst,from,mid      );
                                      Arrays.sort(tst,     mid,until);
    T[] ref = tst.clone();            Arrays.sort(ref,from,    until);

    MergeAccess acc = createAccess(new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return tst[i].compareTo(tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }


  @Property                         default void mergesStablyArr_byte_limitedRanges( @ForAll("mergeInputs_byte_limitedRanges") MergeInput<            byte[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_boolean           ( @ForAll("mergeInputs_boolean"           ) MergeInput<         boolean[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_byte              ( @ForAll("mergeInputs_byte"              ) MergeInput<            byte[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_short             ( @ForAll("mergeInputs_short"             ) MergeInput<           short[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_int_v1            ( @ForAll("mergeInputs_int_v1"            ) MergeInput<             int[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_int_v2            ( @ForAll("mergeInputs_int_v2"            ) MergeInput<             int[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_long              ( @ForAll("mergeInputs_long"              ) MergeInput<            long[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_char              ( @ForAll("mergeInputs_char"              ) MergeInput<            char[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_float             ( @ForAll("mergeInputs_float"             ) MergeInput<           float[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  @Property                         default void mergesStablyArr_double            ( @ForAll("mergeInputs_double"            ) MergeInput<          double[]> sample ) { mergesStablyArr( sample.map(ZipWithIndex::zipWithIndex) ); }
  default <T extends Comparable<? super T>> void mergesStablyArr                   (                                           MergeInput<Entry<T,Integer>[]> sample )
  {
    Comparator<Entry<T,Integer>> cmp =             comparingByKey();
    if( ! isStable() )     cmp = cmp.thenComparing(comparingByValue());

    int mid = sample.mid,
       from = sample.from,
      until = sample.until;

    Entry<T,Integer>[] tst,ref;
    tst = sample.array().clone(); Arrays.sort(tst,from,mid,      cmp);
                                  Arrays.sort(tst,     mid,until,cmp);
    ref = tst.clone();            Arrays.sort(ref,from,    until,cmp);

    Comparator<Entry<T,Integer>> CMP = cmp;
    MergeAccess acc = createAccess( new CompareSwapAccess() {
      @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
    });

    acc.merge(from,mid,until);
    assertThat(tst).isEqualTo(ref);
  }
}
