package com.github.jaaa.merge;

import com.github.jaaa.CompareRandomAccessor;
import com.github.jaaa.Swap;
import com.github.jaaa.misc.Revert;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.Size;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.lang.System.arraycopy;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;
import static java.util.Comparator.comparing;


public class MergerTestTemplate
{
// STATIC FIELDS
static final int N_TRIES = 100_000,
                MAX_SIZE =  10_000;

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final Merger merger;

// CONSTRUCTORS
  public MergerTestTemplate( Merger _merger ) {
    merger = requireNonNull(_merger);
  }

// METHODS
  @Property( tries = N_TRIES )
  void mergeAccessorArrayByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRef,
    @ForAll boolean reversed
  ) {
    byte[]            cRef = new byte[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    byte[] aTest = aRef.clone(),
           bTest = bRef.clone(),
           cTest = new byte[aTest.length+bTest.length];

    var acc = new CompareRandomAccessor<byte[]>(){
      @Override public int        len( byte[] buf ) { return buf.length; }
      @Override public void copyRange( byte[] a, int i, byte[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( byte[] a, int i, byte[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( byte[] a, int i, byte[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( byte[] a, int i, byte[] b, int j ) {
        return reversed ? -Integer.compare(a[i],b[j])
                        : +Integer.compare(a[i],b[j]);
      }
    };

    merger.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property( tries = N_TRIES )
  void mergeAccessorArrayInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRef,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRef,
    @ForAll boolean reversed
  ) {
    var               cRef = new int[aRef.length + bRef.length];
    arraycopy(aRef,0, cRef,0,           aRef.length);
    arraycopy(bRef,0, cRef,aRef.length, bRef.length);
    Arrays.sort(aRef); if(reversed) Revert.revert(aRef);
    Arrays.sort(bRef); if(reversed) Revert.revert(bRef);
    Arrays.sort(cRef); if(reversed) Revert.revert(cRef);

    int[] aTest = aRef.clone(),
          bTest = bRef.clone(),
          cTest = new int[aTest.length+bTest.length];

    var acc = new CompareRandomAccessor<int[]>(){
      @Override public int        len( int[] buf ) { return buf.length; }
      @Override public void copyRange( int[] a, int i, int[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( int[] a, int i, int[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( int[] a, int i, int[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( int[] a, int i, int[] b, int j ) {
        return reversed ? -Integer.compare(a[i],b[j])
                        : +Integer.compare(a[i],b[j]);
      }
    };

    merger.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void mergeAccessorStablyArrayTupleByte(
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] aRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) byte[] bRaw,
    @ForAll boolean reversed
  ) {
    Comparator<Tuple2<Byte,Integer>>  cmp; {
    Comparator<Tuple2<Byte,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    Tuple2<Byte,Integer>[]
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],              i) ).toArray(Tuple2[]::new),
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],aRaw.length + i) ).toArray(Tuple2[]::new),
      cRef =                             Stream.concat( stream(aRef), stream(bRef) ).toArray(Tuple2[]::new);

    Arrays.sort(aRef,cmp); var aTest = aRef.clone();
    Arrays.sort(bRef,cmp); var bTest = bRef.clone();
                           var cTest = cRef.clone();
    Arrays.sort(cRef,cmp);

    var acc = new CompareRandomAccessor<Tuple2<Byte,Integer>[]>(){
      @Override public int        len( Tuple2<Byte,Integer>[] buf ) { return buf.length; }
      @Override public void copyRange( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( Tuple2<Byte,Integer>[] a, int i, Tuple2<Byte,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
    };

    merger.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void mergeAccessorStablyArrayTupleInt(
    @ForAll @Size(min=0, max=MAX_SIZE) int[] aRaw,
    @ForAll @Size(min=0, max=MAX_SIZE) int[] bRaw,
    @ForAll boolean reversed
  ) {
    Comparator<Tuple2<Integer,Integer>>  cmp; {
    Comparator<Tuple2<Integer,Integer>> _cmp = comparing(Tuple2::get1);
      cmp = reversed ? _cmp.reversed() : _cmp;
    }

    Tuple2<Integer,Integer>[]
      aRef = range(0,aRaw.length).mapToObj( i -> Tuple.of(aRaw[i],              i) ).toArray(Tuple2[]::new),
      bRef = range(0,bRaw.length).mapToObj( i -> Tuple.of(bRaw[i],aRaw.length + i) ).toArray(Tuple2[]::new),
      cRef =                             Stream.concat( stream(aRef), stream(bRef) ).toArray(Tuple2[]::new);

    Arrays.sort(aRef,cmp); var aTest = aRef.clone();
    Arrays.sort(bRef,cmp); var bTest = bRef.clone();
                           var cTest = cRef.clone();
    Arrays.sort(cRef,cmp);

    var acc = new CompareRandomAccessor<Tuple2<Integer,Integer>[]>(){
      @Override public int        len( Tuple2<Integer,Integer>[] buf ) { return buf.length; }
      @Override public void copyRange( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j, int len ) { System.arraycopy(a,i, b,j, len); }
      @Override public void copy     ( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { b[j] = a[i]; }
      @Override public void      swap( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { Swap.swap(a,i, b,j); }
      @Override public int    compare( Tuple2<Integer,Integer>[] a, int i, Tuple2<Integer,Integer>[] b, int j ) { return cmp.compare(a[i], b[j]); }
    };

    merger.merge(aTest,0,aTest.length, bTest,0,bTest.length, cTest,0, acc);

    assertThat(aTest).isEqualTo(aRef);
    assertThat(bTest).isEqualTo(bRef);
    assertThat(cTest).isEqualTo(cRef);
  }
}
