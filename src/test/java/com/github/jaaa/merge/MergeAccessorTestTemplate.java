package com.github.jaaa.merge;

import com.github.jaaa.misc.Revert;
import com.github.jaaa.Swap;
import com.github.jaaa.WithInsertIndex;
import com.github.jaaa.WithRange;
import com.github.jaaa.CompareRandomAccessor;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.*;

public abstract class MergeAccessorTestTemplate
{
// STATIC FIELDS
  private static final int N_TRIES = 100_000,
                          MAX_SIZE = 8192;

  abstract protected boolean isStable();
  abstract protected boolean mergesInplaceL2R();
  abstract protected boolean mergesInplaceR2L();

  public static interface MergeAccessor<T>
  {
    public void merge(
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

  @Property( tries = N_TRIES )
  void mergeArraysByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRef, @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRef, @ForAll boolean reversed )
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

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int len( byte[] buf ) { return buf.length; }
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

  @Property( tries = N_TRIES )
  void mergeArraysInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] aRef, @ForAll @Size(min=0, max=MAX_SIZE) int[] bRef, @ForAll boolean reversed )
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

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int len( int[] buf ) { return buf.length; }
      @Override public void copy( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
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



  @Property( tries = N_TRIES )
  void mergeArraysWithRangeByte(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> a,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> b,
    @ForAll boolean reversed
  )
  {
    byte[] aRef = a.getData(),
           bRef = b.getData();
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

    var acc = createAccessor(new CompareRandomAccessor<byte[]>() {
      @Override public int len( byte[] buf ) { return buf.length; }
      @Override public void copy( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
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

  @Property( tries = N_TRIES )
  void mergeArraysWithRangeInt(
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> a,
    @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> b,
    @ForAll boolean reversed
  )
  {
    int[] aRef = a.getData(),
          bRef = b.getData();
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

    var acc = createAccessor(new CompareRandomAccessor<int[]>() {
      @Override public int len( int[] buf ) { return buf.length; }
      @Override public void copy( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
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



  @Property( tries = N_TRIES )
  void mergesStablyArraysTupleByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRefRaw, @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRefRaw, @ForAll boolean reversed )
  {
    Comparator<Tuple2<Byte,Integer>> __cmp = comparing(Tuple2::get1);
    if( ! isStable()) __cmp = __cmp.thenComparing(Tuple2::get2);
    if(   reversed  ) __cmp = __cmp.reversed();
    var cmp = __cmp;

    Tuple2<Byte,Integer>[]
      aRef  = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef  = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef  =                     Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new),
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = new Tuple2[aTest.length+bTest.length];

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Byte,Integer>[]>() {
      @Override public int     len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public void   copy( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
    });

    acc.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property( tries = N_TRIES )
  void mergesStablyArraysTupleInteger( @ForAll @Size(min=0, max=MAX_SIZE) int[] aRefRaw, @ForAll @Size(min=0, max=MAX_SIZE) int[] bRefRaw, @ForAll boolean reversed )
  {
    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    Tuple2<Integer,Integer>[]
      aRef  = range(0,aRefRaw.length).mapToObj( i -> Tuple.of(aRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      bRef  = range(0,bRefRaw.length).mapToObj( i -> Tuple.of(bRefRaw[i],i) ).sorted(cmp).toArray(Tuple2[]::new),
      cRef  =                     Stream.concat( stream(aRef), stream(bRef) ).sorted(cmp).toArray(Tuple2[]::new),
      aTest = aRef.clone(),
      bTest = bRef.clone(),
      cTest = new Tuple2[aTest.length + bTest.length];

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int     len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public void   copy( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
    });

    acc.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }



  @Property( tries = N_TRIES )
  void mergesStablyInplaceL2RArraysTupleInteger( @ForAll WithInsertIndex<@Size(min=0, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {
//    if( ! mergesInplaceL2R() )
//      return;

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    int split = sample.getIndex();

    Tuple2<Integer,Integer>[]
      cTest, cRef = range(0,sample.getData().length).mapToObj( i -> Tuple.of(sample.getData()[i],i) ).toArray(Tuple2[]::new),
      aTest, aRef = copyOfRange(cRef, 0,split);
    Arrays.sort(aRef,cmp);

    aTest = aRef.clone();
    cTest = cRef.clone();

    Arrays.sort(cTest, split,cTest.length, cmp);
    Arrays.sort(cRef,cmp);

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int     len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public void   copy( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
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



  @Property( tries = N_TRIES )
  void mergesStablyInplaceR2LArraysTupleInteger( @ForAll WithInsertIndex<@Size(min=0, max=MAX_SIZE) int[]> sample, @ForAll boolean reversed )
  {

    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      if( ! isStable()) _cmp = _cmp.thenComparing(Tuple2::get2);
      if(   reversed  ) _cmp = _cmp.reversed();
      cmp = _cmp;
    }

    int split = sample.getIndex();

    Tuple2<Integer,Integer>[]
      cTest, cRef = range(0,sample.getData().length).mapToObj( i -> Tuple.of(sample.getData()[i],i) ).toArray(Tuple2[]::new),
      aTest, aRef = copyOfRange(cRef, split,cRef.length);
    Arrays.sort(aRef,cmp);

    aTest = aRef.clone();
    cTest = cRef.clone();

    Arrays.sort(cTest, 0,split, cmp);
    Arrays.sort(cRef,cmp);

    var acc = createAccessor(new CompareRandomAccessor<Tuple2<Integer,Integer>[]>() {
      @Override public int     len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public void   copy( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void   swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i],b[j]); }
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
