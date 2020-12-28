package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import com.github.jaaa.WithRange;
import com.github.jaaa.misc.Revert;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;

import static com.github.jaaa.misc.Concat.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.IntStream.range;
import static java.util.Comparator.comparing;

public abstract class MergePartAccessorTestTemplate
{
// STATIC FIELDS
 private static final int N_TRIES = 100_000,
                         MAX_SIZE = 8192;

 abstract protected boolean isStable();

 public static interface MergePartAccessor<T>
 {
   public void mergePartL2R(
     T a, int a0, int aLen,
     T b, int b0, int bLen,
     T c, int c0, int cLen
   );

   public void mergePartR2L(
     T a, int a0, int aLen,
     T b, int b0, int bLen,
     T c, int c0, int cLen
   );
 }

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS

// CONSTRUCTORS

// METHODS
  abstract protected <T> MergePartAccessor<T> createAccessor( CompareRandomAccessor<T> randAcc );

   //==============//
  // mergePartL2R //
 //==============//
  @Property( tries = N_TRIES )
  void mergePartL2RArraysByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int        len( byte[] buf ) { return buf.length; }
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      var ab = new byte[aRef.length+bRef.length];
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

  @Property( tries = N_TRIES )
  void mergePartL2RArraysInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int        len( int[] buf ) { return buf.length; }
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      var ab = new int[aRef.length+bRef.length];
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



  @Property( tries = N_TRIES )
  void mergePartL2RArraysTupleByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] cRefRaw,
    @ForAll boolean reversed
  )
  {
    Tuple2<Byte,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                           bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                           cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    };

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Byte,Integer>[]>() {
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Byte,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
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

  @Property( tries = N_TRIES )
  void mergePartL2RArraysTupleInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] cRefRaw,
    @ForAll boolean reversed
  )
  {
    Tuple2<Integer,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                              bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                              cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    };

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Integer,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
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



  @Property( tries = N_TRIES )
  void mergePartL2RArraysWithRangeByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int        len( byte[] buf ) { return buf.length; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    byte[]  aRef = aRefWithRange.getData(),
            bRef = bRefWithRange.getData(),
            cRef = cRefWithRange.getData();
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
      var ab = new byte[aLen + bLen];
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

  @Property( tries = N_TRIES )
  void mergePartL2RArraysWithRangeInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int        len( int[] buf ) { return buf.length; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    int[]   aRef = aRefWithRange.getData(),
            bRef = bRefWithRange.getData(),
            cRef = cRefWithRange.getData();
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
      var ab = new int[aLen + bLen];
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



  @Property( tries = N_TRIES )
  void mergePartStablyL2RArraysWithRangeTupleByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Byte,Integer>[]>() {
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<Byte,Integer>[] aRef, bRef, cRef; {
      byte[] aRaw = aRefWithRange.getData(),
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Byte,Integer>[] ab = new Tuple2[aLen + bLen];
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

  @Property( tries = N_TRIES )
  void mergePartStablyL2RArraysWithRangeTupleInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<Integer,Integer>[] aRef, bRef, cRef; {
      int[] aRaw = aRefWithRange.getData(),
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Integer,Integer>[] ab = new Tuple2[aLen + bLen];
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
  @Property( tries = N_TRIES )
  void mergePartR2LArraysByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int        len( byte[] buf ) { return buf.length; }
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      var         ab = concat(aRef,bRef);
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

  @Property( tries = N_TRIES )
  void mergePartR2LArraysInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] cRef,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int        len( int[] buf ) { return buf.length; }
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      var         ab = concat(aRef,bRef);
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



  @Property( tries = N_TRIES )
  void mergePartR2LArraysTupleByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] cRefRaw,
    @ForAll boolean reversed
  )
  {
    Tuple2<Byte,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                           bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                           cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
    cmp = reversed ? _cmp.reversed() : _cmp;
  };

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Byte,Integer>[]>() {
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Byte,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
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

  @Property( tries = N_TRIES )
  void mergePartR2LArraysTupleInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRefRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] cRefRaw,
    @ForAll boolean reversed
  )
  {
    Tuple2<Integer,Integer>[] aRef = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).toArray(Tuple2[]::new),
                              bRef = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).toArray(Tuple2[]::new),
                              cRef = range(0,cRefRaw.length).mapToObj( i -> Tuple.of(cRefRaw[i],i) ).toArray(Tuple2[]::new);

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
    cmp = reversed ? _cmp.reversed() : _cmp;
  };

    // make sure cRef is the shortest
    if( aRef.length < bRef.length )
         { if( cRef.length > aRef.length ) { var tmp=cRef; cRef=aRef; aRef=tmp; } }
    else { if( cRef.length > bRef.length ) { var tmp=cRef; cRef=bRef; bRef=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Arrays.sort(aRef,cmp);
    Arrays.sort(bRef,cmp);

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Integer,Integer>[] ab = new Tuple2[aRef.length+bRef.length];
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


  @Property( tries = N_TRIES )
  void mergePartR2LArraysWithRangeByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int        len( byte[] buf ) { return buf.length; }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) { int c = Byte.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    byte[]  aRef = aRefWithRange.getData(),
            bRef = bRefWithRange.getData(),
            cRef = cRefWithRange.getData();
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
      var ab = new byte[aLen + bLen];
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

  @Property( tries = N_TRIES )
  void mergePartR2LArraysWithRangeInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
    { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
      cRefWithRange=aRefWithRange;
      aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
      cRefWithRange=bRefWithRange;
      bRefWithRange=tmp; } }

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int        len( int[] buf ) { return buf.length; }
      @Override public int    compare( int[] a, int i, int[] b, int j ) { int c = Integer.compare(a[i], b[j]); return reversed ? -c : +c; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    int[]   aRef = aRefWithRange.getData(),
            bRef = bRefWithRange.getData(),
            cRef = cRefWithRange.getData();
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
      var ab = new int[aLen + bLen];
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



  @Property( tries = N_TRIES )
  void mergePartStablyR2LArraysWithRangeTupleByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
    cmp = reversed ? _cmp.reversed() : _cmp;
  }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Byte,Integer>[]>() {
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<Byte,Integer>[] aRef, bRef, cRef; {
    byte[] aRaw = aRefWithRange.getData(),
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Byte,Integer>[] ab = new Tuple2[aLen + bLen];
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

  @Property( tries = N_TRIES )
  void mergePartStablyR2LArraysWithRangeTupleInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> aRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> bRefWithRange,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> cRefWithRange,
    @ForAll boolean reversed
  )
  {
    // make sure cRef is the shortest
    if( aRefWithRange.rangeLength() < bRefWithRange.rangeLength() )
         { if( cRefWithRange.rangeLength() > aRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=aRefWithRange;
                                                                                                   aRefWithRange=tmp; } }
    else { if( cRefWithRange.rangeLength() > bRefWithRange.rangeLength() ) { var tmp=cRefWithRange;
                                                                                     cRefWithRange=bRefWithRange;
                                                                                                   bRefWithRange=tmp; } }

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
    cmp = reversed ? _cmp.reversed() : _cmp;
  }

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
    });

    Tuple2<Integer,Integer>[] aRef, bRef, cRef; {
    int[] aRaw = aRefWithRange.getData(),
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

    var aTest = aRef.clone();
    var bTest = bRef.clone();
    var cTest = cRef.clone();
    {
      Tuple2<Integer,Integer>[] ab = new Tuple2[aLen + bLen];
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
