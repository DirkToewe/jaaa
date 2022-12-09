package com.github.jaaa;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.ArrayArbitrary;

import java.lang.reflect.Array;

import static com.github.jaaa.permute.Swap.swap;
import static com.github.jaaa.util.Combinatorics.factorial;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.api.RandomDistribution.uniform;


public interface ArrayProviderTemplate
{
  static <T> Arbitrary<WithRange<T>> withRange( Arbitrary<T> arbitrary ) {
    return arbitrary.flatMap( arr -> {
      int len;
      if( arr instanceof WithIndex withIndex )
        len = withIndex.contentLength();
      else
        len = Array.getLength(arr);

      return Arbitraries.integers().between(0,len).withDistribution( uniform() ).tuple2().map(
        ij -> {
          int i = ij.get1(),
              j = ij.get2();
          return new WithRange<>(min(i,j), max(i,j), arr);
        }
      );
    });
  }

  static <T> Arbitrary<WithInsertIndex<T>> withInsertIndex( Arbitrary<T> arbitrary ) {
    return arbitrary.flatMap( arr -> {
      if( arr instanceof WithRange wr )
        return Arbitraries.integers().between(wr.getFrom(), wr.getUntil()).withDistribution( uniform() ).map(
          i -> new WithInsertIndex<>(i,arr)
        );

      return Arbitraries.integers().between(0, Array.getLength(arr)).withDistribution( uniform() ).map(
        i -> new WithInsertIndex<>(i,arr)
      );
    });
  }

  static <T> Arbitrary<WithIndex<T>> withIndex( Arbitrary<T> arbitrary ) {
    return arbitrary.flatMap( arr ->
      Arbitraries.integers().between(0, Array.getLength(arr)-1).withDistribution( uniform() ).map(
        i -> new WithIndex<>(i,arr)
      )
    );
  }

          int maxArraySize      ();
  default int maxArraySizeString() { return min(32*1024, maxArraySize()); }

  @Provide default Arbitrary<byte[]> arraysByte_limitedRange() {
    return bytes().tuple2().flatMap( xy -> {
      byte to = (byte) max(xy.get1(), xy.get2()),
         from = (byte) min(xy.get1(), xy.get2());
      return bytes().between(from,to).array(byte[].class).ofMaxSize( maxArraySize() );
    });
  }

  @Provide default Arbitrary<byte[]> arraysByte_smallPermutations() {
    return integers().between(0,8).flatMap(
      n ->    longs().between(0,factorial(n)-1).map( off -> {
        var next = new byte[n];
        for( int i=0; i < n; ) {
          next[i] = (byte) i;
          swap(next,i++, (int) (off % i));
          off /= i;
        }
        return next;
      })
    );
  }

  @Provide default ArrayArbitrary<  Boolean, boolean[]> arraysBoolean() { return Arbitraries.of(false,true).array(boolean[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<     Byte,    byte[]> arraysByte   () { return Arbitraries.       bytes().array(   byte[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<    Short,   short[]> arraysShort  () { return Arbitraries.      shorts().array(  short[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<  Integer,     int[]> arraysInt    () { return Arbitraries.    integers().array(    int[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<     Long,    long[]> arraysLong   () { return Arbitraries.       longs().array(   long[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<Character,    char[]> arraysChar   () { return Arbitraries.       chars().array(   char[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<    Float,   float[]> arraysFloat  () { return Arbitraries.      floats().array(  float[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<   Double,  double[]> arraysDouble () { return Arbitraries.     doubles().array( double[].class).ofMaxSize( maxArraySize()       ).withSizeDistribution(uniform()); }
  @Provide default ArrayArbitrary<   String,  String[]> arraysString () { return Arbitraries.     strings().array( String[].class).ofMaxSize( maxArraySizeString() ).withSizeDistribution(uniform()); }

  @Provide default Arbitrary< WithRange<boolean[]> > arraysWithRangeBoolean() { return withRange( arraysBoolean() ); }
  @Provide default Arbitrary< WithRange<   byte[]> > arraysWithRangeByte   () { return withRange( arraysByte   () ); }
  @Provide default Arbitrary< WithRange<  short[]> > arraysWithRangeShort  () { return withRange( arraysShort  () ); }
  @Provide default Arbitrary< WithRange<    int[]> > arraysWithRangeInt    () { return withRange( arraysInt    () ); }
  @Provide default Arbitrary< WithRange<   long[]> > arraysWithRangeLong   () { return withRange( arraysLong   () ); }
  @Provide default Arbitrary< WithRange<   char[]> > arraysWithRangeChar   () { return withRange( arraysChar   () ); }
  @Provide default Arbitrary< WithRange<  float[]> > arraysWithRangeFloat  () { return withRange( arraysFloat  () ); }
  @Provide default Arbitrary< WithRange< double[]> > arraysWithRangeDouble () { return withRange( arraysDouble () ); }
  @Provide default Arbitrary< WithRange< String[]> > arraysWithRangeString () { return withRange( arraysString () ); }

  @Provide default Arbitrary< WithInsertIndex<boolean[]> > arraysWithInsertIndexBoolean() { return withInsertIndex( arraysBoolean() ); }
  @Provide default Arbitrary< WithInsertIndex<   byte[]> > arraysWithInsertIndexByte   () { return withInsertIndex( arraysByte   () ); }
  @Provide default Arbitrary< WithInsertIndex<  short[]> > arraysWithInsertIndexShort  () { return withInsertIndex( arraysShort  () ); }
  @Provide default Arbitrary< WithInsertIndex<    int[]> > arraysWithInsertIndexInt    () { return withInsertIndex( arraysInt    () ); }
  @Provide default Arbitrary< WithInsertIndex<   long[]> > arraysWithInsertIndexLong   () { return withInsertIndex( arraysLong   () ); }
  @Provide default Arbitrary< WithInsertIndex<   char[]> > arraysWithInsertIndexChar   () { return withInsertIndex( arraysChar   () ); }
  @Provide default Arbitrary< WithInsertIndex<  float[]> > arraysWithInsertIndexFloat  () { return withInsertIndex( arraysFloat  () ); }
  @Provide default Arbitrary< WithInsertIndex< double[]> > arraysWithInsertIndexDouble () { return withInsertIndex( arraysDouble () ); }
  @Provide default Arbitrary< WithInsertIndex< String[]> > arraysWithInsertIndexString () { return withInsertIndex( arraysString () ); }

  @Provide default Arbitrary< WithIndex<boolean[]> > arraysWithIndexBoolean() { return withIndex( arraysBoolean().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<   byte[]> > arraysWithIndexByte   () { return withIndex( arraysByte   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<  short[]> > arraysWithIndexShort  () { return withIndex( arraysShort  ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<    int[]> > arraysWithIndexInt    () { return withIndex( arraysInt    ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<   long[]> > arraysWithIndexLong   () { return withIndex( arraysLong   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<   char[]> > arraysWithIndexChar   () { return withIndex( arraysChar   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<  float[]> > arraysWithIndexFloat  () { return withIndex( arraysFloat  ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex< double[]> > arraysWithIndexDouble () { return withIndex( arraysDouble ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex< String[]> > arraysWithIndexString () { return withIndex( arraysString ().ofMinSize(1) ); }

  @Provide default Arbitrary< WithRange< WithIndex< String[]>>> arraysWithIndexWithRangeString () { return withRange( withIndex( arraysString ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<boolean[]>>> arraysWithIndexWithRangeBoolean() { return withRange( withIndex( arraysBoolean().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<   byte[]>>> arraysWithIndexWithRangeByte   () { return withRange( withIndex( arraysByte   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<  short[]>>> arraysWithIndexWithRangeShort  () { return withRange( withIndex( arraysShort  ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<    int[]>>> arraysWithIndexWithRangeInt    () { return withRange( withIndex( arraysInt    ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<   long[]>>> arraysWithIndexWithRangeLong   () { return withRange( withIndex( arraysLong   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<   char[]>>> arraysWithIndexWithRangeChar   () { return withRange( withIndex( arraysChar   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<  float[]>>> arraysWithIndexWithRangeFloat  () { return withRange( withIndex( arraysFloat  ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex< double[]>>> arraysWithIndexWithRangeDouble () { return withRange( withIndex( arraysDouble ().ofMinSize(1) ) ); }

  @Provide default Arbitrary< WithInsertIndex< WithRange< String[]>>> arraysWithRangeWithInsertIndexString () { return withInsertIndex( withRange( arraysString () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<boolean[]>>> arraysWithRangeWithInsertIndexBoolean() { return withInsertIndex( withRange( arraysBoolean() ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<   byte[]>>> arraysWithRangeWithInsertIndexByte   () { return withInsertIndex( withRange( arraysByte   () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<  short[]>>> arraysWithRangeWithInsertIndexShort  () { return withInsertIndex( withRange( arraysShort  () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<    int[]>>> arraysWithRangeWithInsertIndexInt    () { return withInsertIndex( withRange( arraysInt    () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<   long[]>>> arraysWithRangeWithInsertIndexLong   () { return withInsertIndex( withRange( arraysLong   () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<   char[]>>> arraysWithRangeWithInsertIndexChar   () { return withInsertIndex( withRange( arraysChar   () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange<  float[]>>> arraysWithRangeWithInsertIndexFloat  () { return withInsertIndex( withRange( arraysFloat  () ) ); }
  @Provide default Arbitrary< WithInsertIndex< WithRange< double[]>>> arraysWithRangeWithInsertIndexDouble () { return withInsertIndex( withRange( arraysDouble () ) ); }
}
