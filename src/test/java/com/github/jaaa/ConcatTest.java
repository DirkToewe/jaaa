package com.github.jaaa;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import static java.util.stream.IntStream.range;

import static com.github.jaaa.Concat.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Arrays.stream;

public class ConcatTest
{
  private static final int N_TRIES = 10_000;

  @Property( tries = N_TRIES )
  void concatArraysByte( @ForAll byte[][] seq )
  {
    var test = concat(seq);
    var ref = new ByteArrayOutputStream();

    for( var arr: seq )
    for( var  y : arr )
      ref.write(y);

    assertThat(test).isEqualTo( ref.toByteArray() );
  }

  @Property( tries = N_TRIES )
  void concatArraysShort( @ForAll short[][] seq )
  {
    var    test = concat(seq);
    short[] ref; {
      var refInt = stream(seq).flatMapToInt( arr -> range(0,arr.length).map(i -> arr[i]) ).toArray();
      ref = new short[refInt.length];
      for( int i=ref.length; i-- > 0; )
        ref[i] = (short) refInt[i];
    }
    assertThat(test).isEqualTo(ref);
  }

  @Property( tries = N_TRIES )
  void concatArraysInt( @ForAll int[][] seq )
  {
    var test = concat(seq);
    var ref = stream(seq).flatMapToInt(Arrays::stream).toArray();
    assertThat(test).isEqualTo(ref);
  }

  @Property( tries = N_TRIES )
  void concatArraysLong( @ForAll long[][] seq )
  {
    var test = concat(seq);
    var ref = stream(seq).flatMapToLong(Arrays::stream).toArray();
    assertThat(test).isEqualTo(ref);
  }

  @Property( tries = N_TRIES )
  void concatArraysChar( @ForAll char[][] seq )
  {
    var    test = concat(seq);
    char[] ref; {
      var refInt = stream(seq).flatMapToInt( arr -> range(0,arr.length).map(i -> arr[i]) ).toArray();
      ref = new char[refInt.length];
      for( int i=ref.length; i-- > 0; )
        ref[i] = (char) refInt[i];
    }
    assertThat(test).isEqualTo(ref);
  }

  @Property( tries = N_TRIES )
  void concatArraysFloat( @ForAll float[][] seq )
  {
    var test = concat(seq);
    float[] ref; {
      var refDouble = stream(seq).flatMapToDouble( arr -> range(0,arr.length).mapToDouble(i -> arr[i]) ).toArray();
      ref = new float[refDouble.length];
      for( int i=ref.length; i-- > 0; )
        ref[i] = (float) refDouble[i];
    }
    assertThat(test).isEqualTo(ref);
  }

  @Property( tries = N_TRIES )
  void concatArraysDouble( @ForAll double[][] seq )
  {
    var test = concat(seq);
    var ref = stream(seq).flatMapToDouble(Arrays::stream).toArray();
    assertThat(test).isEqualTo(ref);
  }
}
