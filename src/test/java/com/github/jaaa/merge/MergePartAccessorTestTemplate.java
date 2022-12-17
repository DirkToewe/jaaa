package com.github.jaaa.merge;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.compare.CompareRandomAccessor;
import com.github.jaaa.permute.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.Boxing;
import com.github.jaaa.permute.Revert;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.Boxing.boxed;
import static com.github.jaaa.Concat.concat;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;


@PropertyDefaults( tries = 100_000 )
public abstract class MergePartAccessorTestTemplate implements ArrayProviderTemplate
{
// STATIC FIELDS
  abstract protected boolean isStable();

  public interface MergePartAccessor<T>
  {
    void mergePartL2R(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );

    void mergePartR2L(
      T a, int a0, int aLen,
      T b, int b0, int bLen,
      T c, int c0, int cLen
    );
  }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  @Group public class MergeFullyL2R extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize      () { return MergePartAccessorTestTemplate.this.maxArraySize(); }
    @Override public int maxArraySizeString() { return MergePartAccessorTestTemplate.this.maxArraySizeString(); }
    @Override protected boolean    isStable() { return MergePartAccessorTestTemplate.this.isStable(); }
    @Override protected boolean mergesInplaceL2R() { return true; }
    @Override protected boolean mergesInplaceR2L() { return false; }
    @Override protected <T> MergeAccessor<T> createAccessor( CompareRandomAccessor<T> srtAcc ) {
      MergePartAccessor<T> acc = MergePartAccessorTestTemplate.this.createAccessor(srtAcc);
      return (a,a0,aLen, b,b0,bLen, c,c0) -> acc.mergePartL2R(a,a0,aLen, b,b0,bLen, c,c0,aLen+bLen);
    }
  }
  @Group public class MergeFullyR2L extends MergeAccessorTestTemplate
  {
    @Override public int maxArraySize      () { return MergePartAccessorTestTemplate.this.maxArraySize(); }
    @Override public int maxArraySizeString() { return MergePartAccessorTestTemplate.this.maxArraySizeString(); }
    @Override protected boolean    isStable() { return MergePartAccessorTestTemplate.this.isStable(); }
    @Override protected boolean mergesInplaceL2R() { return false; }
    @Override protected boolean mergesInplaceR2L() { return true; }
    @Override protected <T> MergeAccessor<T> createAccessor( CompareRandomAccessor<T> srtAcc ) {
      MergePartAccessor<T> acc = MergePartAccessorTestTemplate.this.createAccessor(srtAcc);
      return (a,a0,aLen, b,b0,bLen, c,c0) -> acc.mergePartR2L(a,a0,aLen, b,b0,bLen, c,c0,aLen+bLen);
    }
  }

// CONSTRUCTORS

// METHODS
  abstract protected <T> MergePartAccessor<T> createAccessor( CompareRandomAccessor<T> randAcc );

   //==============//
  // mergePartL2R //
 //==============//
  @Property
  void mergePartL2RArraysByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef,
    @ForAll("arraysByte") byte[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { byte[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { byte[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor<byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef);
    Arrays.sort(bRef);
    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
    }

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = cRef.clone();
    {
      byte[] ab = new byte[aRef.length+bRef.length];
      System.arraycopy(aRef,0, ab,0,           aRef.length);
      System.arraycopy(bRef,0, ab,aRef.length, bRef.length);
      Arrays.sort(ab);
      if( reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,0, cRef.length);
    }

    acc.mergePartL2R(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  void mergePartL2RArraysInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef,
    @ForAll("arraysInt") int[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { int[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { int[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor<int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef);
    Arrays.sort(bRef);
    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
    }

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = cRef.clone();
    {
      int[] ab = new int[aRef.length+bRef.length];
      System.arraycopy(aRef,0, ab,0,           aRef.length);
      System.arraycopy(bRef,0, ab,aRef.length, bRef.length);
      Arrays.sort(ab);
      if( reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,0, cRef.length);
    }

    acc.mergePartL2R(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergePartL2RArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] aRefRaw, @ForAll("arraysBoolean") boolean[] bRefRaw, @ForAll("arraysBoolean") boolean[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleByte   ( @ForAll("arraysByte"   )    byte[] aRefRaw, @ForAll("arraysByte"   )    byte[] bRefRaw, @ForAll("arraysByte"   )    byte[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleShort  ( @ForAll("arraysShort"  )   short[] aRefRaw, @ForAll("arraysShort"  )   short[] bRefRaw, @ForAll("arraysShort"  )   short[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleInt    ( @ForAll("arraysInt"    )     int[] aRefRaw, @ForAll("arraysInt"    )     int[] bRefRaw, @ForAll("arraysInt"    )     int[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleLong   ( @ForAll("arraysLong"   )    long[] aRefRaw, @ForAll("arraysLong"   )    long[] bRefRaw, @ForAll("arraysLong"   )    long[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleChar   ( @ForAll("arraysChar"   )    char[] aRefRaw, @ForAll("arraysChar"   )    char[] bRefRaw, @ForAll("arraysChar"   )    char[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleFloat  ( @ForAll("arraysFloat"  )   float[] aRefRaw, @ForAll("arraysFloat"  )   float[] bRefRaw, @ForAll("arraysFloat"  )   float[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleDouble ( @ForAll("arraysDouble" )  double[] aRefRaw, @ForAll("arraysDouble" )  double[] bRefRaw, @ForAll("arraysDouble" )  double[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple( boxed(aRefRaw), boxed(bRefRaw), boxed(cRefRaw), reversed ); }
  @Property                                 void mergePartL2RArraysTupleString ( @ForAll("arraysString" )  String[] aRefRaw, @ForAll("arraysString" )  String[] bRefRaw, @ForAll("arraysString" )  String[] cRefRaw, @ForAll boolean reversed ) { mergePartL2RArraysTuple(       aRefRaw ,       bRefRaw ,       cRefRaw , reversed ); }
  private <T extends Comparable<? super T>> void mergePartL2RArraysTuple( T[] aRefRaw, T[] bRefRaw, T[] cRefRaw, boolean reversed )
  {
    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                        bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                        cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
                 cmp = reversed ? _cmp.reversed() : _cmp;
    }

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { Tuple2<T,Integer>[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { Tuple2<T,Integer>[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor <Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int    compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    Tuple2<T,Integer>[]
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = cRef.clone();
    {
      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
      System.arraycopy(aRef,0, ab,0,           aRef.length);
      System.arraycopy(bRef,0, ab,aRef.length, bRef.length);
      Arrays.sort(ab,cmp);
      System.arraycopy(ab,0, cRef,0, cRef.length);
    }

    acc.mergePartL2R(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property
  void mergePartL2RArraysWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> aRefWithRange,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> bRefWithRange,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<byte[]> tmp=cRefWithRange;
                              cRefWithRange=aRefWithRange;
                                            aRefWithRange=tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<byte[]> tmp=cRefWithRange;
                            cRefWithRange=bRefWithRange;
                                          bRefWithRange=tmp;
    }

    MergePartAccessor<byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    byte[]  aRef = aRefWithRange.getData().clone(),
            bRef = bRefWithRange.getData().clone(),
            cRef = cRefWithRange.getData().clone();
    final int a0 = aRefWithRange.getFrom(),
              b0 = bRefWithRange.getFrom(),
              c0 = cRefWithRange.getFrom(),
            aLen = aRefWithRange.rangeLength(),
            bLen = bRefWithRange.rangeLength(),
            cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef,a0,a0+aLen);
    Arrays.sort(bRef,b0,b0+bLen);
    if( reversed ) {
      Revert.revert(aRef,a0,a0+aLen);
      Revert.revert(bRef,b0,b0+bLen);
    }

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = cRef.clone();
    {
      byte[] ab = new byte[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab);
      if( reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,c0, cLen);
    }

    acc.mergePartL2R(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  void mergePartL2RArraysWithRangeInt(
    @ForAll("arraysWithRangeInt") WithRange<int[]> aRefWithRange,
    @ForAll("arraysWithRangeInt") WithRange<int[]> bRefWithRange,
    @ForAll("arraysWithRangeInt") WithRange<int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<int[]> tmp=cRefWithRange;
                             cRefWithRange=aRefWithRange;
                                           aRefWithRange=tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<int[]> tmp=cRefWithRange;
                           cRefWithRange=bRefWithRange;
                                         bRefWithRange=tmp;
    }

    MergePartAccessor<int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    int[]   aRef = aRefWithRange.getData().clone(),
            bRef = bRefWithRange.getData().clone(),
            cRef = cRefWithRange.getData().clone();
    final int a0 = aRefWithRange.getFrom(),
              b0 = bRefWithRange.getFrom(),
              c0 = cRefWithRange.getFrom(),
            aLen = aRefWithRange.rangeLength(),
            bLen = bRefWithRange.rangeLength(),
            cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef,a0,a0+aLen);
    Arrays.sort(bRef,b0,b0+bLen);
    if( reversed ) {
      Revert.revert(aRef,a0,a0+aLen);
      Revert.revert(bRef,b0,b0+bLen);
    }

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = cRef.clone();
    {
      int[] ab = new int[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab);
      if( reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,c0, cLen);
    }

    acc.mergePartL2R(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergePartStablyL2RArraysWithRangeTupleBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> aRef, @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> bRef, @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> aRef, @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> bRef, @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> aRef, @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> bRef, @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> aRef, @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> bRef, @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> aRef, @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> bRef, @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> aRef, @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> bRef, @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> aRef, @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> bRef, @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> aRef, @ForAll("arraysWithRangeDouble" ) WithRange< double[]> bRef, @ForAll("arraysWithRangeDouble" ) WithRange< double[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed); }
  @Property                                 void mergePartStablyL2RArraysWithRangeTupleString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> aRef, @ForAll("arraysWithRangeString" ) WithRange< String[]> bRef, @ForAll("arraysWithRangeString" ) WithRange< String[]> cRef, @ForAll boolean reversed ) { mergePartStablyL2RArraysWithRangeTuple(aRef                   , bRef                   , cRef                   , reversed); }
  private <T extends Comparable<? super T>> void mergePartStablyL2RArraysWithRangeTuple( WithRange<T[]> aRefWithRange, WithRange<T[]> bRefWithRange, WithRange<T[]> cRefWithRange, boolean reversed )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<T[]> tmp=cRefWithRange;
                           cRefWithRange=aRefWithRange;
                                         aRefWithRange=tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<T[]> tmp=cRefWithRange;
                         cRefWithRange=bRefWithRange;
                                       bRefWithRange=tmp;
    }

    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
     cmp = reversed ? _cmp.reversed() : _cmp;
    }

    MergePartAccessor <Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int    compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<T,Integer>[] aRef, bRef, cRef; {
      T[] aRaw = aRefWithRange.getData(),
          bRaw = bRefWithRange.getData(),
          cRaw = cRefWithRange.getData();
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],i) ).toArray(Tuple2[]::new);
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],i) ).toArray(Tuple2[]::new);
      cRef = range(0,cRaw.length).mapToObj( i -> Tuple.of(cRaw[i],i) ).toArray(Tuple2[]::new);
    }
    final int a0 = aRefWithRange.getFrom(),
              b0 = bRefWithRange.getFrom(),
              c0 = cRefWithRange.getFrom(),
            aLen = aRefWithRange.rangeLength(),
            bLen = bRefWithRange.rangeLength(),
            cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef, a0,a0+aLen, cmp);
    Arrays.sort(bRef, b0,b0+bLen, cmp);

    Tuple2<T,Integer>[]
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = cRef.clone();
    {
      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ab = new Tuple2[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab,cmp);
      System.arraycopy(ab,0, cRef,c0, cLen);
    }

    acc.mergePartL2R(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  //==============//
 // mergePartR2L //
//==============//
  @Property
  void mergePartR2LArraysByte(
    @ForAll("arraysByte") byte[] aRef,
    @ForAll("arraysByte") byte[] bRef,
    @ForAll("arraysByte") byte[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { byte[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { byte[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor<byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef);
    Arrays.sort(bRef);
    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
    }

    byte[]
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = cRef.clone();
    {
      byte[]      ab = concat(aRef,bRef);
      Arrays.sort(ab);
      if( ! reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,0, cRef.length);
      Revert.revert(cRef);
    }

    acc.mergePartR2L(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  void mergePartR2LArraysInt(
    @ForAll("arraysInt") int[] aRef,
    @ForAll("arraysInt") int[] bRef,
    @ForAll("arraysInt") int[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { int[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { int[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor<int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef);
    Arrays.sort(bRef);
    if( reversed ) {
      Revert.revert(aRef);
      Revert.revert(bRef);
    }

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = cRef.clone();
    {
      int[]       ab = concat(aRef,bRef);
      Arrays.sort(ab);
      if( ! reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,0, cRef.length);
      Revert.revert(cRef);
    }

    acc.mergePartR2L(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergePartR2LArraysTupleBoolean( @ForAll("arraysBoolean") boolean[] aRef, @ForAll("arraysBoolean") boolean[] bRef, @ForAll("arraysBoolean") boolean[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleByte   ( @ForAll("arraysByte"   )    byte[] aRef, @ForAll("arraysByte"   )    byte[] bRef, @ForAll("arraysByte"   )    byte[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleShort  ( @ForAll("arraysShort"  )   short[] aRef, @ForAll("arraysShort"  )   short[] bRef, @ForAll("arraysShort"  )   short[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleInt    ( @ForAll("arraysInt"    )     int[] aRef, @ForAll("arraysInt"    )     int[] bRef, @ForAll("arraysInt"    )     int[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleLong   ( @ForAll("arraysLong"   )    long[] aRef, @ForAll("arraysLong"   )    long[] bRef, @ForAll("arraysLong"   )    long[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleChar   ( @ForAll("arraysChar"   )    char[] aRef, @ForAll("arraysChar"   )    char[] bRef, @ForAll("arraysChar"   )    char[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleFloat  ( @ForAll("arraysFloat"  )   float[] aRef, @ForAll("arraysFloat"  )   float[] bRef, @ForAll("arraysFloat"  )   float[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleDouble ( @ForAll("arraysDouble" )  double[] aRef, @ForAll("arraysDouble" )  double[] bRef, @ForAll("arraysDouble" )  double[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(boxed(aRef), boxed(bRef), boxed(cRef), reversed); }
  @Property                                 void mergePartR2LArraysTupleString ( @ForAll("arraysString" )  String[] aRef, @ForAll("arraysString" )  String[] bRef, @ForAll("arraysString" )  String[] cRef, @ForAll boolean reversed ) { mergePartR2LArraysTuple(      aRef ,       bRef ,       cRef,  reversed); }
  private <T extends Comparable<? super T>> void mergePartR2LArraysTuple( T[] aRefRaw, T[] bRefRaw, T[] cRefRaw, boolean reversed )
  {
    @SuppressWarnings("unchecked")
    Tuple2<T,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                        bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                        cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { Tuple2<T,Integer>[] tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { Tuple2<T,Integer>[] tmp=cRef; cRef=bRef; bRef=tmp; } }

    MergePartAccessor<Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int    compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    Tuple2<T,Integer>[]
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = cRef.clone();
    {
      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
      System.arraycopy(aRef,0, ab,0,           aRef.length);
      System.arraycopy(bRef,0, ab,aRef.length, bRef.length);
      Arrays.sort(ab, cmp);
      Revert.revert(ab);
      System.arraycopy(ab,0, cRef,0, cRef.length);
      Revert.revert(cRef);
    }

    acc.mergePartR2L(
      aTest,0,aTest.length,
      bTest,0,bTest.length,
      cTest,0,cTest.length
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }


  @Property
  void mergePartR2LArraysWithRangeByte(
    @ForAll("arraysWithRangeByte") WithRange<byte[]> aRefWithRange,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> bRefWithRange,
    @ForAll("arraysWithRangeByte") WithRange<byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<byte[]> tmp = cRefWithRange;
                                cRefWithRange = aRefWithRange;
                                                aRefWithRange = tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<byte[]> tmp = cRefWithRange;
                              cRefWithRange = bRefWithRange;
                                              bRefWithRange = tmp;
    }

    MergePartAccessor <byte[]> acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public byte[] malloc( int len ) { return new byte[len]; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    byte[]  aRef = aRefWithRange.getData().clone(),
            bRef = bRefWithRange.getData().clone(),
            cRef = cRefWithRange.getData().clone();
    final int a0 = aRefWithRange.getFrom(),
              b0 = bRefWithRange.getFrom(),
              c0 = cRefWithRange.getFrom(),
            aLen = aRefWithRange.rangeLength(),
            bLen = bRefWithRange.rangeLength(),
            cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef,a0,a0+aLen);
    Arrays.sort(bRef,b0,b0+bLen);
    if( reversed ) {
      Revert.revert(aRef,a0,a0+aLen);
      Revert.revert(bRef,b0,b0+bLen);
    }

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = cRef.clone();
    {
      byte[] ab = new byte[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab);
      if( ! reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,c0, cLen);
      Revert.revert(cRef, c0,c0+cLen);
    }

    acc.mergePartR2L(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property
  void mergePartR2LArraysWithRangeInt(
    @ForAll("arraysWithRangeInt") WithRange<int[]> aRefWithRange,
    @ForAll("arraysWithRangeInt") WithRange<int[]> bRefWithRange,
    @ForAll("arraysWithRangeInt") WithRange<int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<int[]> tmp = cRefWithRange;
                               cRefWithRange = aRefWithRange;
                                               aRefWithRange=tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<int[]> tmp = cRefWithRange;
                             cRefWithRange = bRefWithRange;
                                             bRefWithRange=tmp;
    }

    MergePartAccessor <int[]> acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int[] malloc( int len ) { return new int[len]; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    int[]   aRef = aRefWithRange.getData().clone(),
            bRef = bRefWithRange.getData().clone(),
            cRef = cRefWithRange.getData().clone();
    final int a0 = aRefWithRange.getFrom(),
              b0 = bRefWithRange.getFrom(),
              c0 = cRefWithRange.getFrom(),
            aLen = aRefWithRange.rangeLength(),
            bLen = bRefWithRange.rangeLength(),
            cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef,a0,a0+aLen);
    Arrays.sort(bRef,b0,b0+bLen);
    if( reversed ) {
      Revert.revert(aRef,a0,a0+aLen);
      Revert.revert(bRef,b0,b0+bLen);
    }

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = cRef.clone();
    {
      int[] ab = new int[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab);
      if( ! reversed )
        Revert.revert(ab);
      System.arraycopy(ab,0, cRef,c0, cLen);
      Revert.revert(cRef, c0,c0+cLen);
    }

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);

    acc.mergePartR2L(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property                                 void mergePartStablyR2LArraysWithRangeTupleBoolean( @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> aRef, @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> bRef, @ForAll("arraysWithRangeBoolean") WithRange<boolean[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleByte   ( @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> aRef, @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> bRef, @ForAll("arraysWithRangeByte"   ) WithRange<   byte[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleShort  ( @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> aRef, @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> bRef, @ForAll("arraysWithRangeShort"  ) WithRange<  short[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleInt    ( @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> aRef, @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> bRef, @ForAll("arraysWithRangeInt"    ) WithRange<    int[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleLong   ( @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> aRef, @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> bRef, @ForAll("arraysWithRangeLong"   ) WithRange<   long[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleChar   ( @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> aRef, @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> bRef, @ForAll("arraysWithRangeChar"   ) WithRange<   char[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleFloat  ( @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> aRef, @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> bRef, @ForAll("arraysWithRangeFloat"  ) WithRange<  float[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleDouble ( @ForAll("arraysWithRangeDouble" ) WithRange< double[]> aRef, @ForAll("arraysWithRangeDouble" ) WithRange< double[]> bRef, @ForAll("arraysWithRangeDouble" ) WithRange< double[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef.map(Boxing::boxed), bRef.map(Boxing::boxed), cRef.map(Boxing::boxed), reversed ); }
  @Property                                 void mergePartStablyR2LArraysWithRangeTupleString ( @ForAll("arraysWithRangeString" ) WithRange< String[]> aRef, @ForAll("arraysWithRangeString" ) WithRange< String[]> bRef, @ForAll("arraysWithRangeString" ) WithRange< String[]> cRef, @ForAll boolean reversed ) { mergePartStablyR2LArraysWithRangeTuple( aRef                   , bRef                   , cRef                   , reversed ); }
  private <T extends Comparable<? super T>> void mergePartStablyR2LArraysWithRangeTuple( WithRange<T[]> aRefWithRange, WithRange<T[]> bRefWithRange, WithRange<T[]> cRefWithRange, boolean reversed )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() ) {
      if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) {
        WithRange<T[]> tmp = cRefWithRange;
                             cRefWithRange = aRefWithRange;
                                             aRefWithRange = tmp;
      }
    }
    else if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) {
      WithRange<T[]> tmp = cRefWithRange;
                           cRefWithRange = bRefWithRange;
                                           bRefWithRange = tmp;
    }

    Comparator<Tuple2<T,Integer>>  cmp; {
    Comparator<Tuple2<T,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    MergePartAccessor <Tuple2<T,Integer>[]> acc = createAccessor(new CompareRandomAccessor<Tuple2<T,Integer>[]>() {
      @SuppressWarnings("unchecked")
      @Override public Tuple2<T,Integer>[] malloc( int len ) { return new Tuple2[len]; }
      @Override public int    compare( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<T,Integer>[] a, int i, Tuple2<T,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<T,Integer>[] aRef, bRef, cRef; {
      T[] aRaw = aRefWithRange.getData(),
          bRaw = bRefWithRange.getData(),
          cRaw = cRefWithRange.getData();
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],i) ).toArray(Tuple2[]::new);
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],i) ).toArray(Tuple2[]::new);
      cRef = range(0,cRaw.length).mapToObj( i -> Tuple.of(cRaw[i],i) ).toArray(Tuple2[]::new);
    }
    int a0 = aRefWithRange.getFrom(),
        b0 = bRefWithRange.getFrom(),
        c0 = cRefWithRange.getFrom(),
       aLen = aRefWithRange.rangeLength(),
       bLen = bRefWithRange.rangeLength(),
       cLen = cRefWithRange.rangeLength();

    Arrays.sort(aRef, a0,a0+aLen, cmp);
    Arrays.sort(bRef, b0,b0+bLen, cmp);

    Tuple2<T,Integer>[]
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = cRef.clone();
    {
      @SuppressWarnings("unchecked")
      Tuple2<T,Integer>[] ab = new Tuple2[aLen + bLen];
      System.arraycopy(aRef,a0, ab,0,    aLen);
      System.arraycopy(bRef,b0, ab,aLen, bLen);
      Arrays.sort(ab,cmp);
      Revert.revert(ab);
      System.arraycopy(ab,0, cRef,c0, cLen);
      Revert.revert(cRef, c0,c0+cLen);
    }

    acc.mergePartR2L(
      aTest,a0,aLen,
      bTest,b0,bLen,
      cTest,c0,cLen
    );

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }
}
