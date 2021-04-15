package com.github.jaaa.merge;

import com.github.jaaa.Swap;
import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import static com.github.jaaa.misc.Revert.revert;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@Group
@PropertyDefaults( tries = 10_000 )
public class ExtractMergeBufUniqueAccessTest
{
  private static final int MAX_SIZE = 8192;


  @Group
  class ExtractMergeBufUniqueMin
  {
    @Property                         void extractsArrBoolean  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Boolean  []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrByte     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Byte     []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrShort    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Short    []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrInteger  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Integer  []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrLong     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Long     []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrCharacter( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Character[]> sample ) { extractsArr(sample); }
    @Property                         void extractsArrFloat    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Float    []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrDouble   ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Double   []> sample ) { extractsArr(sample); }
    <T extends Comparable<? super T>> void extractsArr         (         WithInsertIndex<T[]>                             sample ) {
      int len = sample.getIndex();
      var ref = sample.getData().clone(); Arrays.sort(ref);
      var tst =    ref.clone();

      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return tst[i].compareTo(tst[j]); }
      }.extractMergeBufUniqueMin(0,tst.length, len);

      assertThat( copyOfRange(tst,0,nUnique) ).isEqualTo( stream(ref).distinct().limit(len).toArray() );

      Arrays.sort(tst);
      Arrays.sort(ref);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void isStableArrBoolean  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Boolean  []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrByte     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Byte     []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrShort    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Short    []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrInteger  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Integer  []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrLong     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Long     []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrCharacter( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Character[]> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrFloat    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Float    []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrDouble   ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Double   []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    <T extends Comparable<? super T>> void isStableArr         (         WithInsertIndex<T[]>                             sample,         boolean reversed ) {
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
      if( reversed ) cmp = cmp.reversed();

      var raw = sample.getData();
      int len = sample.getIndex();

      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref,cmp);
      var tst = ref.clone();

      var CMP = cmp;
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      }.extractMergeBufUniqueMin(0,tst.length, len);

      assertThat(
        copyOfRange(tst,0,nUnique)
      ).isEqualTo(
        stream(ref).filter(new TreeSet<>(cmp)::add).limit(len).toArray(Tuple2[]::new)
      );

      Arrays.sort(tst, cmp);
      Arrays.sort(ref, cmp);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void extractsArrWithRangeBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Boolean  []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeByte     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Byte     []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeShort    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Short    []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeInteger  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Integer  []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeLong     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Long     []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeCharacter( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Character[]>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeFloat    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Float    []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeDouble   ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Double   []>> sample ) { extractsArrWithRange(sample); }
    <T extends Comparable<? super T>> void extractsArrWithRange         (         WithInsertIndex<WithRange<T[]>>                             sample ) {
      int from = sample.getData().getFrom(),
         until = sample.getData().getUntil();
      int  len = sample.getIndex() - from;
      var  ref = sample.getData().getData().clone(); Arrays.sort(ref);
      var  tst = ref.clone();
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return tst[i].compareTo(tst[j]); }
      }.extractMergeBufUniqueMin(from,until, len);

      assertThat( copyOfRange(tst,from,from+nUnique) ).isEqualTo( stream(ref,from,until).distinct().limit(len).toArray() );

      assertThat( copyOfRange(tst,0,from) )
      .isEqualTo( copyOfRange(ref,0,from) );

      assertThat( copyOfRange(tst,until,tst.length) )
      .isEqualTo( copyOfRange(ref,until,ref.length) );

      Arrays.sort(tst);
      Arrays.sort(ref);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void isStableArrWithRangeBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Boolean  []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeByte     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Byte     []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeShort    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Short    []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeInteger  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Integer  []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeLong     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Long     []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeCharacter( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Character[]>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeFloat    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Float    []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeDouble   ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Double   []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    <T extends Comparable<? super T>> void isStableArrWithRange         (         WithInsertIndex<WithRange<T[]>>                             sample,         boolean reversed ) {
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
      if( reversed )          cmp = cmp.reversed();

      int from = sample.getData().getFrom(),
         until = sample.getData().getUntil();
      var  raw = sample.getData().getData();
      int  len = sample.getIndex() - from;

      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref,cmp);
      var tst = ref.clone();

      var CMP = cmp;
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      }.extractMergeBufUniqueMin(from,until, len);

      assertThat(
        copyOfRange(tst,from,from+nUnique)
      ).isEqualTo(
        stream(ref,from,until).filter(new TreeSet<>(cmp)::add).limit(len).toArray(Tuple2[]::new)
      );

      assertThat( copyOfRange(tst,0,from) )
      .isEqualTo( copyOfRange(ref,0,from) );

      assertThat( copyOfRange(tst,until,tst.length) )
      .isEqualTo( copyOfRange(ref,until,ref.length) );

      Arrays.sort(tst, cmp);
      Arrays.sort(ref, cmp);
      assertThat(tst).isEqualTo(ref);
    }
  }


  @Group
  class ExtractMergeBufUniqueMax
  {
    @Property                         void extractsArrBoolean  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Boolean  []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrByte     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Byte     []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrShort    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Short    []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrInteger  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Integer  []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrLong     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Long     []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrCharacter( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Character[]> sample ) { extractsArr(sample); }
    @Property                         void extractsArrFloat    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Float    []> sample ) { extractsArr(sample); }
    @Property                         void extractsArrDouble   ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Double   []> sample ) { extractsArr(sample); }
    <T extends Comparable<? super T>> void extractsArr         (         WithInsertIndex<T[]>                             sample ) {
      int len = sample.getIndex();
      var ref = sample.getData().clone(); Arrays.sort(ref);
      var tst =    ref.clone();

      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return tst[i].compareTo(tst[j]); }
      }.extractMergeBufUniqueMax(0,tst.length, len);

      var    tail = ref.clone();
      revert(tail);
      revert(tail = (T[]) stream(tail).distinct().limit(len).toArray(Comparable[]::new) );

      assertThat( copyOfRange(tst,tst.length-nUnique,tst.length) ).isEqualTo(tail);

      Arrays.sort(tst);
      Arrays.sort(ref);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void isStableArrBoolean  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Boolean  []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrByte     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Byte     []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrShort    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Short    []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrInteger  ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Integer  []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrLong     ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Long     []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrCharacter( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Character[]> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrFloat    ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Float    []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    @Property                         void isStableArrDouble   ( @ForAll WithInsertIndex<@Size(max=MAX_SIZE) Double   []> sample, @ForAll boolean reversed ) { isStableArr(sample,reversed); }
    <T extends Comparable<? super T>> void isStableArr         (         WithInsertIndex<T[]>                             sample,         boolean reversed ) {
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
      if( reversed )          cmp = cmp.reversed();

      var raw = sample.getData();
      int len = sample.getIndex();

      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref,cmp);
      var tst = ref.clone();

      var CMP = cmp;
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      }.extractMergeBufUniqueMax(0,tst.length, len);

      var    tail = ref.clone();
      revert(tail);
      revert(tail = stream(tail).filter(new TreeSet<>(cmp)::add).limit(len).toArray(Tuple2[]::new) );

      assertThat( copyOfRange(tst,tst.length-nUnique,tst.length) ).isEqualTo(tail);

      Arrays.sort(tst, cmp);
      Arrays.sort(ref, cmp);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void extractsArrWithRangeBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Boolean  []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeByte     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Byte     []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeShort    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Short    []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeInteger  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Integer  []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeLong     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Long     []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeCharacter( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Character[]>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeFloat    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Float    []>> sample ) { extractsArrWithRange(sample); }
    @Property                         void extractsArrWithRangeDouble   ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Double   []>> sample ) { extractsArrWithRange(sample); }
    <T extends Comparable<? super T>> void extractsArrWithRange         (         WithInsertIndex<WithRange<T[]>>                             sample ) {
      int from = sample.getData().getFrom(),
         until = sample.getData().getUntil();
      int  len = sample.getIndex() - from;
      var  ref = sample.getData().getData().clone(); Arrays.sort(ref);
      var  tst = ref.clone();
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return tst[i].compareTo(tst[j]); }
      }.extractMergeBufUniqueMax(from,until, len);

      var    tail = copyOfRange(ref,from,until);
      revert(tail);
      revert(tail = (T[]) stream(tail).distinct().limit(len).toArray(Comparable[]::new) );

      assertThat( copyOfRange(tst,until-nUnique,until) ).isEqualTo( tail );

      assertThat( copyOfRange(tst,0,from) )
      .isEqualTo( copyOfRange(ref,0,from) );

      assertThat( copyOfRange(tst,until,tst.length) )
      .isEqualTo( copyOfRange(ref,until,ref.length) );

      Arrays.sort(tst);
      Arrays.sort(ref);
      assertThat(tst).isEqualTo(ref);
    }

    @Property                         void isStableArrWithRangeBoolean  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Boolean  []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeByte     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Byte     []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeShort    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Short    []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeInteger  ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Integer  []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeLong     ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Long     []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeCharacter( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Character[]>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeFloat    ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Float    []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    @Property                         void isStableArrWithRangeDouble   ( @ForAll WithInsertIndex<WithRange<@Size(max=MAX_SIZE) Double   []>> sample, @ForAll boolean reversed ) { isStableArrWithRange(sample,reversed); }
    <T extends Comparable<? super T>> void isStableArrWithRange         (         WithInsertIndex<WithRange<T[]>>                             sample,         boolean reversed ) {
      Comparator<Tuple2<T,Integer>> cmp = comparing(Tuple2::get1);
      if( reversed )          cmp = cmp.reversed();

      int from = sample.getData().getFrom(),
         until = sample.getData().getUntil();
      var  raw = sample.getData().getData();
      int  len = sample.getIndex() - from;

      Tuple2<T,Integer>[] ref = range(0,raw.length).mapToObj( i -> Tuple.of(raw[i],i) ).toArray(Tuple2[]::new);
      Arrays.sort(ref,cmp);
      var tst = ref.clone();

      var CMP = cmp;
      int nUnique = new ExtractMergeBufUniqueAccess() {
        @Override public void   swap( int i, int j ) { Swap.swap(tst,i,j); }
        @Override public int compare( int i, int j ) { return CMP.compare(tst[i], tst[j]); }
      }.extractMergeBufUniqueMax(from,until, len);

      var    tail = copyOfRange(ref,from,until);
      revert(tail);
      revert(tail = stream(tail).filter(new TreeSet<>(cmp)::add).limit(len).toArray(Tuple2[]::new) );

      assertThat( copyOfRange(tst,until-nUnique,until) ).isEqualTo(tail);

      assertThat( copyOfRange(tst,0,from) )
      .isEqualTo( copyOfRange(ref,0,from) );

      assertThat( copyOfRange(tst,until,tst.length) )
      .isEqualTo( copyOfRange(ref,until,ref.length) );

      Arrays.sort(tst, cmp);
      Arrays.sort(ref, cmp);
      assertThat(tst).isEqualTo(ref);
    }
  }
}
