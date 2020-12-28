package com.github.jaaa.partition;

import com.github.jaaa.*;
import com.github.jaaa.Swap;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.ShrinkingMode;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static java.lang.invoke.MethodType.methodType;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;

public class BiPartitionTestTemplate
{
// STATIC FIELDS
  static final int N_TRIES = 100_000,
                  MAX_SIZE = 8192;

// STATIC CONSTRUCTOR

// STATIC METHODS

// FIELDS
  private final MethodHandle
    biPartitionArraysObject, biPartitionArraysWithRangeObject,
    biPartitionArraysByte,   biPartitionArraysWithRangeByte,
    biPartitionArraysShort,  biPartitionArraysWithRangeShort,
    biPartitionArraysInt,    biPartitionArraysWithRangeInt,
    biPartitionArraysLong,   biPartitionArraysWithRangeLong,
    biPartitionArraysChar,   biPartitionArraysWithRangeChar,
    biPartitionArraysFloat,  biPartitionArraysWithRangeFloat,
    biPartitionArraysDouble, biPartitionArraysWithRangeDouble,
    biPartitionAccess;

  // CONSTRUCTORS
  public BiPartitionTestTemplate( Class<?> sortClass )
  {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();

    BiFunction<Class<?>,Class<?>,MethodHandle> fn = (arrClass, predClass) -> {
      try {
        return lookup.findStatic( sortClass, "biPartition", methodType(void.class, arrClass, predClass) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new ExceptionInInitializerError(err);
      }
    };

    BiFunction<Class<?>,Class<?>,MethodHandle> rn = (arrClass, predClass) -> {
      try {
        return lookup.findStatic( sortClass, "biPartition", methodType(void.class, arrClass, int.class,int.class, predClass) );
      }
      catch( NoSuchMethodException | IllegalAccessException err ) {
        throw new ExceptionInInitializerError(err);
      }
    };

    biPartitionArraysObject = fn.apply(Object[].class, Predicate      .class);
    biPartitionArraysByte   = fn.apply(  byte[].class, PredicateByte  .class);
    biPartitionArraysShort  = fn.apply( short[].class, PredicateShort .class);
    biPartitionArraysInt    = fn.apply(   int[].class, PredicateInt   .class);
    biPartitionArraysLong   = fn.apply(  long[].class, PredicateLong  .class);
    biPartitionArraysChar   = fn.apply(  char[].class, PredicateChar  .class);
    biPartitionArraysFloat  = fn.apply( float[].class, PredicateFloat .class);
    biPartitionArraysDouble = fn.apply(double[].class, PredicateDouble.class);

    biPartitionArraysWithRangeObject = rn.apply(Object[].class, Predicate      .class);
    biPartitionArraysWithRangeByte   = rn.apply(  byte[].class, PredicateByte  .class);
    biPartitionArraysWithRangeShort  = rn.apply( short[].class, PredicateShort .class);
    biPartitionArraysWithRangeInt    = rn.apply(   int[].class, PredicateInt   .class);
    biPartitionArraysWithRangeLong   = rn.apply(  long[].class, PredicateLong  .class);
    biPartitionArraysWithRangeChar   = rn.apply(  char[].class, PredicateChar  .class);
    biPartitionArraysWithRangeFloat  = rn.apply( float[].class, PredicateFloat .class);
    biPartitionArraysWithRangeDouble = rn.apply(double[].class, PredicateDouble.class);

    try {
      biPartitionAccess = lookup.findStatic( sortClass, "biPartition", methodType(void.class, int.class,int.class, PredicateSwapAccess.class) );
    }
    catch( NoSuchMethodException | IllegalAccessException err ) {
      throw new ExceptionInInitializerError(err);
    }
  }



// METHODS
  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsAccessBoolean( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] input ) throws Throwable
  {
    boolean[] reference = input.clone(); {
      Boolean[]   ref = range(0,input.length).mapToObj( i -> input[i] ).toArray(Boolean[]::new);
      Arrays.sort(ref);
      for( int i=ref.length; i-- > 0; )
        reference[i] = ref[i];
    }

    biPartitionAccess.invoke(0, input.length, new PredicateSwapAccess(){
      @Override public boolean predicate(int i) { return input[i]; }
      @Override public void swap(int i, int j) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsStablyAccessTuple( @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample ) throws Throwable
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, comparing(Tuple2::get1));

    biPartitionAccess.invoke(0, input.length, new PredicateSwapAccess() {
      @Override public boolean predicate( int i ) { return input[i].get1(); }
      @Override public void swap( int i, int j ) { Swap.swap(input,i,j); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsStablyAccessWithOffsetObjectTuple(
    @ForAll @Size(min=0, max=MAX_SIZE) boolean[] sample,
    @ForAll @IntRange(
      min = Integer.MIN_VALUE,
      max = Integer.MAX_VALUE - MAX_SIZE
    ) int offset
  ) throws Throwable
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, comparing(Tuple2::get1));

    biPartitionAccess.invoke(offset, offset+input.length, new PredicateSwapAccess() {
      @Override public boolean predicate( int i ) { return input[i-offset].get1(); }
      @Override public void swap( int i, int j ) { Swap.swap(input, i-offset, j-offset); }
    });

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayTuple( @ForAll @Size(min=0, max=MAX_SIZE) Boolean[] sample ) throws Throwable
  {
    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, comparing(Tuple2::get1));

    biPartitionArraysObject.invoke(input, (Predicate<Tuple2<Boolean,?>>) Tuple2::get1);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayByte( @ForAll @Size(min=0, max=MAX_SIZE) byte[] input, @ForAll @IntRange(min=0, max=Byte.SIZE-1) int bit ) throws Throwable
  {
    PredicateByte pred = x -> (x >>> bit) == 1;

    Byte[]       reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Byte[]::new);
    Arrays.sort( reference, comparing(pred::test) );

    biPartitionArraysByte.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayShort( @ForAll @Size(min=0, max=MAX_SIZE) short[] input, @ForAll @IntRange(min=0, max=Short.SIZE-1) int bit ) throws Throwable
  {
    PredicateShort pred = x -> (x >>> bit) == 1;

    Short[]      reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Short[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysShort.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayInt( @ForAll @Size(min=0, max=MAX_SIZE) int[] input, @ForAll @IntRange(min=0, max=Integer.SIZE-1) int bit ) throws Throwable
  {
    PredicateInt pred = x -> (x >>> bit) == 1;

    Integer[]    reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Integer[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysInt.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayLong( @ForAll @Size(min=0, max=MAX_SIZE) long[] input, @ForAll @IntRange(min=0, max=Long.SIZE-1) int bit ) throws Throwable
  {
    PredicateLong pred = x -> (x >>> bit) == 1;

    Long[]       reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Long[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysLong.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayChar( @ForAll @Size(min=0, max=MAX_SIZE) char[] input, @ForAll @IntRange(min=0, max=Character.SIZE-1) int bit ) throws Throwable
  {
    PredicateChar pred = x -> (x >>> bit) == 1;

    Character[]  reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Character[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysChar.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayFloat( @ForAll @Size(min=0, max=MAX_SIZE) float[] input, @ForAll float split ) throws Throwable
  {
    PredicateFloat pred = x -> x <= split;

    Float[]      reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Float[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysFloat.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsArrayDouble( @ForAll @Size(min=0, max=MAX_SIZE) double[] input, @ForAll double split ) throws Throwable
  {
    PredicateDouble pred = x -> x <= split;

    Double[]     reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Double[]::new);
    Arrays.sort( reference, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysDouble.invoke(input, pred);

    assertThat(input).isEqualTo(reference);
  }




  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeTuple( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) Boolean[]> sampleWithRange ) throws Throwable
  {
    Boolean[] sample = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    Tuple2<Boolean,Integer>[] input = range(0,sample.length).mapToObj(i -> Tuple.of(sample[i],i) ).toArray(Tuple2[]::new),
                reference  =  input.clone();
    Arrays.sort(reference, from,until, comparing(Tuple2::get1));

    biPartitionArraysWithRangeObject.invoke(input, from,until, (Predicate<Tuple2<Boolean,?>>) Tuple2::get1);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeByte( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) byte[]> sampleWithRange, @ForAll @IntRange(min=0, max=Byte.SIZE-1) int bit ) throws Throwable
  {
    byte[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateByte pred = x -> (x >>> bit) == 1;

    Byte[]       reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Byte[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeByte.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeShort( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) short[]> sampleWithRange, @ForAll @IntRange(min=0, max=Short.SIZE-1) int bit ) throws Throwable
  {
    short[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateShort pred = x -> (x >>> bit) == 1;

    Short[]      reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Short[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeShort.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeInt( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) int[]> sampleWithRange, @ForAll @IntRange(min=0, max=Integer.SIZE-1) int bit ) throws Throwable
  {
    int[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateInt pred = x -> (x >>> bit) == 1;

    Integer[]    reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Integer[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeInt.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeLong( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) long[]> sampleWithRange, @ForAll @IntRange(min=0, max=Long.SIZE-1) int bit ) throws Throwable
  {
    long[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateLong pred = x -> (x >>> bit) == 1;

    Long[]       reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Long[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeLong.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeChar( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) char[]> sampleWithRange, @ForAll @IntRange(min=0, max=Character.SIZE-1) int bit ) throws Throwable
  {
    char[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateChar pred = x -> (x >>> bit) == 1;

    Character[]  reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Character[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeChar.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeFloat( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) float[]> sampleWithRange, @ForAll float split ) throws Throwable
  {
    float[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateFloat pred = x -> x <= split;

    Float[]      reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Float[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeFloat.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }

  @Property( tries = N_TRIES, shrinking = ShrinkingMode.OFF )
  void biPartitionsWithRangeDouble( @ForAll WithRange<@Size(min=0, max=MAX_SIZE) double[]> sampleWithRange, @ForAll double split ) throws Throwable
  {
    double[] input = sampleWithRange.getData();
    int from = sampleWithRange.getFrom(),
       until = sampleWithRange.getUntil();

    PredicateDouble pred = x -> x <= split;

    Double[]     reference = range(0,input.length).mapToObj( i -> input[i] ).toArray(Double[]::new);
    Arrays.sort( reference, from,until, (x,y) -> Boolean.compare( pred.test(x), pred.test(y) ) );

    biPartitionArraysWithRangeDouble.invoke(input, from,until, pred);

    assertThat(input).isEqualTo(reference);
  }
}
