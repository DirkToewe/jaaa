package com.github.jaaa.merge;

import com.github.jaaa.*;
import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.Boxing;
import com.github.jaaa.permute.Revert;
import com.github.jaaa.permute.Swap;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.PropertyDefaults;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static com.github.jaaa.Boxing.boxed;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@PropertyDefaults( tries = 10_000 )
public abstract class MergeAccessorTestTemplate implements ArrayProviderTemplate
{
// STATIC FIELDS
  abstract protected boolean isStable();
  abstract protected boolean mergesInplaceL2R();
  abstract protected boolean mergesInplaceR2L();

  public interface MergeAccessor<T>
  {
    void merge(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0
    );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected <T> MergeAccessor<T> createAccessor( CompareRandomAccessor<T> srtAcc );
//  abstract protected long comparisonLimit( int len, int i );

  @Property void mergeArraysByte( @ForAll("arraysByte") byte[] aRef, @ForAll("arraysByte") byte[] bRef, @ForAll boolean reversed )
  {
    byte[] cRef = new byte[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = new byte[aTest.length+bTest.length];

    MergeAccessor<byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public void copy( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( byte[] a, int i, byte[] b, int j ) {
        return reversed ? -Byte.compare(a[i],b[j])
                        : +Byte.compare(a[i],b[j]);
      }
    });

    acc.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property void mergeArraysInt( @ForAll("arraysInt") int[] aRef, @ForAll("arraysInt") int[] bRef, @ForAll boolean reversed )
  {
    int[] cRef = new int[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = new int[aTest.length+bTest.length];

    MergeAccessor<int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public void   copy( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( int[] a, int i, int[] b, int j ) {
        return reversed
          ? -Integer.compare(a[i],b[j])
          : +Integer.compare(a[i],b[j]);
      }
    });

    acc.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property void mergeArraysWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> a,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> b,
    @ForAll boolean reversed
  )
  {
    byte[] aRef = a.getData().clone(),
           bRef = b.getData().clone();
    int aOff = a.getFrom(),
        bOff = b.getFrom(),
        aLen = a.getUntil() - aOff,
        bLen = b.getUntil() - bOff;
    byte[] cRef = new byte[aLen + bLen];
    arraycopy(aRef,aOff, cRef,0,    aLen);
    arraycopy(bRef,bOff, cRef,aLen, bLen);
    Arrays.sort(aRef, aOff,aOff+aLen); if(reversed) Revert.revert(aRef, aOff,aOff+aLen);
    Arrays.sort(bRef, bOff,bOff+bLen); if(reversed) Revert.revert(bRef, bOff,bOff+bLen);
    Arrays.sort(cRef);                 if(reversed) Revert.revert(cRef);

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = new byte[aLen + bLen];

    MergeAccessor<byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public void   copy( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( byte[] a, int i, byte[] b, int j ) {
        return reversed
          ? -Integer.compare(a[i],b[j])
          : +Integer.compare(a[i],b[j]);
      }
    });

    acc.merge(aTest,aOff,aLen, bTest,bOff,bLen, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property void mergeArraysWithRangeInt(
    @ForAll("arraysWithRangeInt") WithRange<int[]> a,
    @ForAll("arraysWithRangeInt") WithRange<int[]> b,
    @ForAll boolean reversed
  )
  {
    int[] aRef = a.getData().clone(),
          bRef = b.getData().clone();
    int   aOff = a.getFrom(),
          bOff = b.getFrom(),
          aLen = a.getUntil() - aOff,
          bLen = b.getUntil() - bOff;
    int[] cRef = new int[aLen + bLen];
    arraycopy(aRef,aOff, cRef,0,    aLen);
    arraycopy(bRef,bOff, cRef,aLen, bLen);
    Arrays.sort(aRef, aOff,aOff+aLen); if(reversed) Revert.revert(aRef, aOff,aOff+aLen);
    Arrays.sort(bRef, bOff,bOff+bLen); if(reversed) Revert.revert(bRef, bOff,bOff+bLen);
    Arrays.sort(cRef);                 if(reversed) Revert.revert(cRef);

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = new int[aLen + bLen];

    MergeAccessor<int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public void   copy( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( int[] a, int i, int[] b, int j ) {
        return reversed
          ? -Integer.compare(a[i],b[j])
          : +Integer.compare(a[i],b[j]);
      }
    });

    acc.merge(aTest,aOff,aLen, bTest,bOff,bLen, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergesStablyArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] aRef, @ForAll("arraysBoolean") boolean[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleByte   ( @ForAll("arraysByte"   )    byte[] aRef, @ForAll("arraysByte"   )    byte[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleShort  ( @ForAll("arraysShort"  )   short[] aRef, @ForAll("arraysShort"  )   short[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleInt    ( @ForAll("arraysInt"    )     int[] aRef, @ForAll("arraysInt"    )     int[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleLong   ( @ForAll("arraysLong"   )    long[] aRef, @ForAll("arraysLong"   )    long[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleChar   ( @ForAll("arraysChar"   )    char[] aRef, @ForAll("arraysChar"   )    char[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleFloat  ( @ForAll("arraysFloat"  )   float[] aRef, @ForAll("arraysFloat"  )   float[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleDouble ( @ForAll("arraysDouble" )  double[] aRef, @ForAll("arraysDouble" )  double[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(boxed(aRef), boxed(bRef), reversed); }
  @Property                                 void mergesStablyArraysTupleString ( @ForAll("arraysString" )  String[] aRef, @ForAll("arraysString" )  String[] bRef, @ForAll boolean reversed ) { mergesStablyArraysTuple(      aRef ,       bRef , reversed); }
  private <T extends Comparable<? super T>> void mergesStablyArraysTuple( T[] aRefRaw, T[] bRefRaw, @ForAll boolean reversed )
  {
    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[]
      aRef  = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef  = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef  =                     Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new),
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = new Tuple2[aTest.length + bTest.length];

    MergeAccessor<Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void   copy( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
    });

    acc.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergesStablyInplaceL2RArraysTupleBoolean( @ForAll("arraysWithInsertIndexBoolean") WithInsertIndex<boolean[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleByte   ( @ForAll("arraysWithInsertIndexByte"   ) WithInsertIndex<   byte[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleShort  ( @ForAll("arraysWithInsertIndexShort"  ) WithInsertIndex<  short[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleInt    ( @ForAll("arraysWithInsertIndexInt"    ) WithInsertIndex<    int[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleLong   ( @ForAll("arraysWithInsertIndexLong"   ) WithInsertIndex<   long[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleChar   ( @ForAll("arraysWithInsertIndexChar"   ) WithInsertIndex<   char[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleFloat  ( @ForAll("arraysWithInsertIndexFloat"  ) WithInsertIndex<  float[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleDouble ( @ForAll("arraysWithInsertIndexDouble" ) WithInsertIndex< double[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceL2RArraysTupleString ( @ForAll("arraysWithInsertIndexString" ) WithInsertIndex< String[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceL2RArraysTuple(sample                   , reversed); }
  private <T extends Comparable<? super T>> void mergesStablyInplaceL2RArraysTuple( @ForAll WithInsertIndex<T[]> sample, @ForAll boolean reversed )
  {
    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    int split = sample.getIndex();

    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[]
      cTest, cRef = range(0,sample.getData().length).mapToObj( i -> Tuple.of(sample.getData()[i],i) ).toArray(Tuple2[]::new),
      aTest, aRef = copyOfRange(cRef, 0,split);
    Arrays.sort(aRef,cmp);

    aTest = aRef.clone();
    cTest = cRef.clone();

    Arrays.sort(cTest, split,cTest.length, cmp);
    Arrays.sort(cRef,cmp);

    MergeAccessor<Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void   copy( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
    });

    Runnable test = () -> {
      acc.merge(
        aTest,    0,aTest.length,
        cTest,split,cTest.length-split,
        cTest,0
      );

      assertThat(aTest).isEqualTo(aRef);
      assertThat(cTest).isEqualTo(cRef);
    };

    if( mergesInplaceL2R() || cTest.length == split || split == 0 )
      test.run();
    else
      assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(test::run);
  }



  @Property                                 void mergesStablyInplaceR2LArraysTupleBoolean( @ForAll("arraysWithInsertIndexBoolean") WithInsertIndex<boolean[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleByte   ( @ForAll("arraysWithInsertIndexByte"   ) WithInsertIndex<   byte[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleShort  ( @ForAll("arraysWithInsertIndexShort"  ) WithInsertIndex<  short[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleInt    ( @ForAll("arraysWithInsertIndexInt"    ) WithInsertIndex<    int[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleLong   ( @ForAll("arraysWithInsertIndexLong"   ) WithInsertIndex<   long[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleChar   ( @ForAll("arraysWithInsertIndexChar"   ) WithInsertIndex<   char[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleFloat  ( @ForAll("arraysWithInsertIndexFloat"  ) WithInsertIndex<  float[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleDouble ( @ForAll("arraysWithInsertIndexDouble" ) WithInsertIndex< double[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample.map(Boxing::boxed), reversed); }
  @Property                                 void mergesStablyInplaceR2LArraysTupleString ( @ForAll("arraysWithInsertIndexString" ) WithInsertIndex< String[]> sample, @ForAll boolean reversed ) { mergesStablyInplaceR2LArraysTuple(sample                   , reversed); }
  private <T extends Comparable<? super T>> void mergesStablyInplaceR2LArraysTuple( @ForAll WithInsertIndex<T[]> sample, @ForAll boolean reversed )
  {

    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    int split = sample.getIndex();

    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[]
      cTest, cRef = range(0,sample.getData().length).mapToObj( i -> Tuple.of(sample.getData()[i],i) ).toArray(Tuple2[]::new),
      aTest, aRef = copyOfRange(cRef, split,cRef.length);
    Arrays.sort(aRef,cmp);

    aTest = aRef.clone();
    cTest = cRef.clone();

    Arrays.sort(cTest, 0,split, cmp);
    Arrays.sort(cRef,cmp);

    MergeAccessor<Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public void   copy( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
    });

    Runnable test = () -> {
      acc.merge(
        cTest, 0,split,
        aTest, 0,aTest.length,
        cTest, 0
      );

      assertThat(aTest).isEqualTo(aRef);
      assertThat(cTest).isEqualTo(cRef);
    };

    if( mergesInplaceR2L() || cTest.length == split || split == 0 )
      test.run();
    else
      assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(test::run);
  }
}
