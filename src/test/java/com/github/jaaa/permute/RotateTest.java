package com.github.jaaa.permute;

import com.github.jaaa.WithRange;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import static com.github.jaaa.permute.Rotate.rotate;
import static org.assertj.core.api.Assertions.assertThat;
import static java.util.stream.IntStream.range;
import static com.github.jaaa.Boxing.unboxed;


@PropertyDefaults( shrinking = ShrinkingMode.OFF )
public class RotateTest
{
  private static final int N_TRIES = 100_000;



  @Property( tries = N_TRIES )
  void rotatesArraysBoolean1( @ForAll boolean[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    boolean[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysBoolean2( @ForAll boolean[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    boolean[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysBoolean3( @ForAll boolean[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    boolean[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysBoolean4( @ForAll boolean[] input, @ForAll int dist )
  {
    Assume.that( dist >= input.length - Integer.MAX_VALUE );

    Boolean[] reference = range(0,input.length).mapToObj(i -> {
      i -= dist;
      i %= input.length;
      if( i < 0 ) i += input.length;
      return           input[i];
    }).toArray(Boolean[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
  }

  @Property( tries = N_TRIES )
  void rotatesArraysBoolean5( @ForAll WithRange<boolean[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    boolean[] input = sample.getData();

    Boolean[] reference = range(0,input.length).mapToObj( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Boolean[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
  }



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

    assertThat(input).isEqualTo( unboxed(reference) );
  }

  @Property( tries = N_TRIES )
  void rotatesArraysByte5( @ForAll WithRange<byte[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    byte[] input = sample.getData();

    Byte[] reference = range(0,input.length).mapToObj( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Byte[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
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

    assertThat(input).isEqualTo( unboxed(reference) );
  }

  @Property( tries = N_TRIES )
  void rotatesArraysShort5( @ForAll WithRange<short[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    short[] input = sample.getData();

    Short[] reference = range(0,input.length).mapToObj(i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Short[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
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
  void rotatesArraysInt5( @ForAll WithRange<int[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    int[] input = sample.getData();

    int[] reference = range(0,input.length).map( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray();

    rotate(input, from,until, dist);

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
  void rotatesArraysLong5( @ForAll WithRange<long[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    long[] input = sample.getData();

    long[] reference = range(0,input.length).mapToLong(i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray();

    rotate(input, from,until, dist);

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

    assertThat(input).isEqualTo( unboxed(reference) );
  }

  @Property( tries = N_TRIES )
  void rotatesArraysChar5( @ForAll WithRange<char[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    char[] input = sample.getData();

    Character[] reference = range(0,input.length).mapToObj( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Character[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
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

    assertThat(input).isEqualTo( unboxed(reference) );
  }

  @Property( tries = N_TRIES )
  void rotatesArraysFloat5( @ForAll WithRange<float[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    float[] input = sample.getData();

    Float[] reference = range(0,input.length).mapToObj( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Float[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo( unboxed(reference) );
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
    int len = input.length;
    double[] reference = range(0,len).mapToDouble( i -> {
          i = (i - dist % len) % len;
      if( i < 0 )
          i += len;
      return input[i];
    }).toArray();

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysDouble5( @ForAll WithRange<double[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    double[] input = sample.getData();

    double[] reference = range(0,input.length).mapToDouble( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray();

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo(reference);
  }



  @Property( tries = N_TRIES )
  void rotatesArraysObject1( @ForAll Integer[] input, @ForAll @IntRange( min = -Integer.MAX_VALUE ) int dist )
  {
    Integer[] reference = input.clone();
    rotate(input,+dist);
    rotate(input,-dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysObject2( @ForAll Integer[] input, @ForAll int dist1, @ForAll int dist2 )
  {
    Assume.that(
      dist1 < 0 ? (dist2 > Integer.MIN_VALUE - dist1)
                : (dist2 < Integer.MAX_VALUE - dist1)
    );
    Integer[] reference = input.clone();
    rotate(input, dist1);
    rotate(input, dist2);
    rotate(reference, dist1+dist2);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysObject3( @ForAll Integer[] input, @ForAll int dist )
  {
    if(  0 != input.length ) {
      dist /= input.length;
      dist *= input.length;
    }
    Integer[] reference = input.clone();
    rotate(input, dist);
    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysObject4( @ForAll Integer[] input, @ForAll int dist )
  {
    int len = input.length;
    Integer[] reference = range(0,len).mapToObj( i -> {
          i = (i - dist % len) % len;
      if( i < 0 )
          i += len;
      return input[i];
    }).toArray(Integer[]::new);

    rotate(input, dist);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES )
  void rotatesArraysObject5( @ForAll WithRange<Integer[]> sample, @ForAll int dist )
  {
    int  from = sample.getFrom(),
        until = sample.getUntil(),
          len = until-from;
    Integer[] input = sample.getData();

    Integer[] reference = range(0,input.length).mapToObj( i -> {
      if( from <= i && i < until ) {
            i -= from;
            i = (i - dist % len) % len;
        if( i < 0 )
            i += len;
            i += from;
      }
      return input[i];
    }).toArray(Integer[]::new);

    rotate(input, from,until, dist);

    assertThat(input).isEqualTo(reference);
  }
}
