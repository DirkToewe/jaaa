package com.github.jaaa.merge.datagen;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.deepHashCode;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;


public final class MergeInput<T>
{
// STATIC FIELDS
  public static final MergeInput<byte[]> EMPTY_BYTE = new MergeInput<>(new byte[0], 0,0,0);

// STATIC CONSTRUCTOR

// STATIC METHODS
  private static int length( Object array ) {
    if( array instanceof List ) return ((List<?>) array).size();
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

  @SuppressWarnings("unchecked")
  private static <T> T clone( T array ) {
    if( array instanceof boolean[] ) { boolean[] arr = (boolean[]) array; return (T) arr.clone(); }
    if( array instanceof    byte[] ) {    byte[] arr = (   byte[]) array; return (T) arr.clone(); }
    if( array instanceof   short[] ) {   short[] arr = (  short[]) array; return (T) arr.clone(); }
    if( array instanceof     int[] ) {     int[] arr = (    int[]) array; return (T) arr.clone(); }
    if( array instanceof    long[] ) {    long[] arr = (   long[]) array; return (T) arr.clone(); }
    if( array instanceof    char[] ) {    char[] arr = (   char[]) array; return (T) arr.clone(); }
    if( array instanceof   float[] ) {   float[] arr = (  float[]) array; return (T) arr.clone(); }
    if( array instanceof  double[] ) {  double[] arr = ( double[]) array; return (T) arr.clone(); }
    if( array instanceof  Object[] ) {  Object[] arr = ( Object[]) array; return (T) arr.clone(); }
    throw new IllegalArgumentException();
  }

  private static Stream<String> stream( Object array, int from, int until ) {
    if( array instanceof boolean[] ) { boolean[] dat = (boolean[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof    byte[] ) {    byte[] dat = (   byte[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof   short[] ) {   short[] dat = (  short[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof     int[] ) {     int[] dat = (    int[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof    long[] ) {    long[] dat = (   long[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof    char[] ) {    char[] dat = (   char[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof   float[] ) {   float[] dat = (  float[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof  double[] ) {  double[] dat = ( double[]) array; return range(from,until).mapToObj( i ->   String.valueOf(dat[i]) ); }
    if( array instanceof  Object[] ) {  Object[] dat = ( Object[]) array; return range(from,until).mapToObj( i -> Objects.toString(dat[i]) ); }
    throw new IllegalArgumentException();
  }

  public static MergeInput<boolean[]> of(boolean[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<   byte[]> of(   byte[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<  short[]> of(  short[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<    int[]> of(    int[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<   long[]> of(   long[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<   char[]> of(   char[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput<  float[]> of(  float[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static MergeInput< double[]> of( double[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }
  public static   <T> MergeInput<T[]> of(      T[] array, int from, int mid, int until ) { return new MergeInput<>(array,from,mid,until); }

// FIELDS
  private final T array;
  private final int hashCode;
  public final int from, mid, until;

// CONSTRUCTORS
  private MergeInput( T _array, int _from, int _mid, int _until ) {
    requireNonNull(_array);
    int len = length(_array);
    if( _from < 0 || _from > _mid || _until < _mid || _until > len )
      throw new IllegalArgumentException();
    array = clone(_array);
    from = _from;
    mid = _mid;
    until = _until;
    hashCode = deepHashCode(new Object[] {_array, _from, _mid, _until});
  }

// METHODS
  public T array() { return clone(array); }

  @Override public String toString() {
    String l = str(array, from,mid  ),
           r = str(array,  mid,until);
    return l+r;
  }

  @Override public boolean equals( Object obj ) {
    if( this == obj ) return true;
    if( !(obj instanceof MergeInput) )
      return false;
    MergeInput<?> that = (MergeInput<?>) obj;
    return Arrays.deepEquals(
      new Object[] {this.array, this.from, this.mid, this.until},
      new Object[] {that.array, that.from, that.mid, that.until}
    );
  }

  public <R> MergeInput<R> map( Function<? super T, ? extends R> mapper ) {
    return new MergeInput<>(mapper.apply(array), from, mid, until);
  }

  @Override public int hashCode() {
    return hashCode;
  }
}