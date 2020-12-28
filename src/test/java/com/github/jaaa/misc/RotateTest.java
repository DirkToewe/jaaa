package com.github.jaaa.misc;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.misc.Rotate.rotate;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.IntStream.range;

public class RotateTest
{
  private static final int N_TRIES = 100_000;

  @Property( tries = N_TRIES )
  void rotatesArraysByte1( @ForAll byte[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    byte[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysByte2( @ForAll byte[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
       dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                 : (dist2 < Integer.MAX_VALUE - dist1)
    );
    byte[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysByte3( @ForAll byte[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    byte[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysByte4( @ForAll byte[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    Byte[] reference = range(0,input.length).mapToObj( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(Byte[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysShort1( @ForAll short[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    short[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysShort2( @ForAll short[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    short[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysShort3( @ForAll short[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    short[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysShort4( @ForAll short[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    Short[] reference = range(0,input.length).mapToObj( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(Short[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysInt1( @ForAll int[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    int[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysInt2( @ForAll int[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    int[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysInt3( @ForAll int[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    int[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysInt4( @ForAll int[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    int[] reference = range(0,input.length).map( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray();

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysLong1( @ForAll long[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    long[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysLong2( @ForAll long[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
       dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                 : (dist2 < Integer.MAX_VALUE - dist1)
    );
    long[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysLong3( @ForAll long[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    long[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysLong4( @ForAll long[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    long[] reference = range(0,input.length).mapToLong( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray();

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysChar1( @ForAll char[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    char[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysChar2( @ForAll char[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    char[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysChar3( @ForAll char[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    char[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysChar4( @ForAll char[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    Character[] reference = range(0,input.length).mapToObj( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(Character[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysFloat1( @ForAll float[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    float[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysFloat2( @ForAll float[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    float[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysFloat3( @ForAll float[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    float[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysFloat4( @ForAll float[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    Float[] reference = range(0,input.length).mapToObj( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(Float[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysDouble1( @ForAll double[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    double[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysDouble2( @ForAll double[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    double[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysDouble3( @ForAll double[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    double[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysDouble4( @ForAll double[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    double[] reference = range(0,input.length).mapToDouble( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray();

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = 10_000 )
  void rotatesArraysObject1( @ForAll String[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    String[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = 10_000 )
  void rotatesArraysObject2( @ForAll String[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    String[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = 10_000 )
  void rotatesArraysObject3( @ForAll String[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    String[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = 10_000 )
  void rotatesArraysObject4( @ForAll String[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    String[] reference = range(0,input.length).mapToObj( i -> {
                  i -= dist;
                  i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(String[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }
}
