package com.github.jaaa.merge.datagen;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static java.lang.String.format;


public record MergeInput<T>( T array, int from, int mid, int until )
{
// STATIC FIELDS
  public static final MergeInput<byte[]> EMPTY_BYTE = new MergeInput<>(new byte[0], 0,0,0);

// STATIC CONSTRUCTOR

// STATIC METHODS
  private static int length( Object array ) {
    if( array instanceof List<?> dat ) return dat.size();
    return Array.getLength(array);
  }

  private static String str( Object array, int from, int until )
  {
    final int LIMIT = 16;

    int len = until-from;
    if( len <= LIMIT*2 + 2 )
      return stream(array, from,until).collect( joining(", ", "[", "]") );

    return format(
      "[%s, ...%d more..., %s]",
      stream(array,from, LIMIT+from ).collect( joining(", ") ), len - 2*LIMIT,
      stream(array,until-LIMIT,until).collect( joining(", ") )
    );
  }

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
    int len = length(array);
    if( from < 0 || from > mid || until < mid || until > len )
      throw new IllegalArgumentException();
  }

// METHODS
  @Override public String toString() {
    var l = str(array, from,mid  );
    var r = str(array,  mid,until);
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

  public <R> MergeInput<R> map( Function<? super T, ? extends R> mapper ) {
    return new MergeInput<>(mapper.apply(array), from, mid, until);
  }

  @Override public int hashCode() {
    return Arrays.deepHashCode(new Object[] {array, from, mid, until});
  }
}