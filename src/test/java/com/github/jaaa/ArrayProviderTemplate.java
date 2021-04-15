package com.github.jaaa;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

import java.lang.reflect.Array;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.jqwik.api.RandomDistribution.uniform;


public interface ArrayProviderTemplate
{
  static <T> Arbitrary<WithRange<T>> withRange( Arbitrary<T> arbitrary ) {
    return arbitrary.flatMap( arr ->
      Arbitraries.integers().between(0, Array.getLength(arr)).withDistribution( uniform() ).tuple2().map(
        ij -> {
          int i = ij.get1(),
              j = ij.get2();
          return new WithRange<>(min(i,j), max(i,j), arr);
        }
      )
    );
  }

  static <T> Arbitrary<WithInsertIndex<T>> withInsertIndex( Arbitrary<T> arbitrary ) {
    return arbitrary.flatMap( arr ->
      Arbitraries.integers().between(0, Array.getLength(arr)).withDistribution( uniform() ).map(
        i -> new WithInsertIndex<>(i,arr)
      )
    );
  }

          int maxArraySize();
  default int maxArraySizeString() { return min(32*1024, maxArraySize()); }

  @Provide default Arbitrary<boolean[]> arraysBoolean() { return Arbitraries.of(false,true).array(boolean[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<   byte[]> arraysByte   () { return Arbitraries.       bytes().array(   byte[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<  short[]> arraysShort  () { return Arbitraries.      shorts().array(  short[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<    int[]> arraysInt    () { return Arbitraries.    integers().array(    int[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<   long[]> arraysLong   () { return Arbitraries.       longs().array(   long[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<   char[]> arraysChar   () { return Arbitraries.       chars().array(   char[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary<  float[]> arraysFloat  () { return Arbitraries.      floats().array(  float[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary< double[]> arraysDouble () { return Arbitraries.     doubles().array( double[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default Arbitrary< String[]> arraysString () { return Arbitraries.     strings().array( String[].class).ofMaxSize( maxArraySizeString() ); }

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
}
