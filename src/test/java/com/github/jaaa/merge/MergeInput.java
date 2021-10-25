package com.github.jaaa.merge;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


public record MergeInput<T>(T array, int from, int mid, int until )
{
// STATIC FIELDS
  public static final MergeInput<byte[]> EMPTY_BYTE = new MergeInput<>(new byte[0], 0,0,0);

// STATIC CONSTRUCTOR

// STATIC METHODS
  private static Stream<String> stream( Object array, int from, int until ) {
    if( array instanceof boolean[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof    byte[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof   short[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof     int[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof    long[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof    char[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof   float[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof  double[] dat ) return range(from,until).mapToObj( i ->  String.valueOf(dat[i]) );
    if( array instanceof  Object[] dat ) return range(from,until).mapToObj( i -> Objects.toString(dat[i]) );
    if( array instanceof   List<?> dat ) return dat.stream().map(Objects::toString);
    throw new IllegalArgumentException();
  }

// FIELDS
// CONSTRUCTORS
  public MergeInput {
    requireNonNull(array);
  }

// METHODS
  @Override public String toString() {
    var l = stream(array, from,mid  ).collect( joining(", ", "[", "]") );
    var r = stream(array,  mid,until).collect( joining(", ", "[", "]") );
    return l+r;
  }

  @Override public boolean equals( Object o ) {
    if( o instanceof MergeInput that )
      return Arrays.deepEquals(
        new Object[] {this.array, this.from, this.mid, this.until},
        new Object[] {that.array, that.from, that.mid, that.until}
      );
    return false;
  }

  @Override public int hashCode() {
    return Arrays.deepHashCode(new Object[] {array, from, mid, until});
  }
}