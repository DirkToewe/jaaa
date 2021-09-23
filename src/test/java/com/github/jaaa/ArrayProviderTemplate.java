package com.github.jaaa;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.ArrayArbitrary;

import java.lang.reflect.Array;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.jqwik.api.Arbitraries.bytes;
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
    return arbitrary.flatMap( arr ->
      Arbitraries.integers().between(0, Array.getLength(arr)).withDistribution( uniform() ).map(
        i -> new WithInsertIndex<>(i,arr)
      )
    );
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
      return bytes().between(from,to).array(byte[].class).ofMinSize( maxArraySize() );
    });
  }

  @Provide default ArrayArbitrary<  Boolean, boolean[]> arraysBoolean() { return Arbitraries.of(false,true).array(boolean[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<     Byte,    byte[]> arraysByte   () { return Arbitraries.       bytes().array(   byte[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<    Short,   short[]> arraysShort  () { return Arbitraries.      shorts().array(  short[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<  Integer,     int[]> arraysInt    () { return Arbitraries.    integers().array(    int[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<     Long,    long[]> arraysLong   () { return Arbitraries.       longs().array(   long[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<Character,    char[]> arraysChar   () { return Arbitraries.       chars().array(   char[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<    Float,   float[]> arraysFloat  () { return Arbitraries.      floats().array(  float[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<   Double,  double[]> arraysDouble () { return Arbitraries.     doubles().array( double[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<   String,  String[]> arraysString () { return Arbitraries.     strings().array( String[].class).ofMaxSize( maxArraySizeString() ); }

  @Provide default ArrayArbitrary<  Boolean,   Boolean[]> arraysObjBoolean() { return Arbitraries.of(false,true).array(  Boolean[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<     Byte,      Byte[]> arraysObjByte   () { return Arbitraries.       bytes().array(     Byte[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<    Short,     Short[]> arraysObjShort  () { return Arbitraries.      shorts().array(    Short[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<  Integer,   Integer[]> arraysObjInt    () { return Arbitraries.    integers().array(  Integer[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<     Long,      Long[]> arraysObjLong   () { return Arbitraries.       longs().array(     Long[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<Character, Character[]> arraysObjChar   () { return Arbitraries.       chars().array(Character[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<    Float,     Float[]> arraysObjFloat  () { return Arbitraries.      floats().array(    Float[].class).ofMaxSize( maxArraySize()       ); }
  @Provide default ArrayArbitrary<   Double,    Double[]> arraysObjDouble () { return Arbitraries.     doubles().array(   Double[].class).ofMaxSize( maxArraySize()       ); }

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

  @Provide default Arbitrary< WithIndex<  Boolean[]> > arraysWithIndexObjBoolean() { return withIndex( arraysObjBoolean().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<     Byte[]> > arraysWithIndexObjByte   () { return withIndex( arraysObjByte   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<    Short[]> > arraysWithIndexObjShort  () { return withIndex( arraysObjShort  ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<  Integer[]> > arraysWithIndexObjInt    () { return withIndex( arraysObjInt    ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<     Long[]> > arraysWithIndexObjLong   () { return withIndex( arraysObjLong   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<Character[]> > arraysWithIndexObjChar   () { return withIndex( arraysObjChar   ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<    Float[]> > arraysWithIndexObjFloat  () { return withIndex( arraysObjFloat  ().ofMinSize(1) ); }
  @Provide default Arbitrary< WithIndex<   Double[]> > arraysWithIndexObjDouble () { return withIndex( arraysObjDouble ().ofMinSize(1) ); }

  @Provide default Arbitrary< WithRange< WithIndex<   String[]>>> arraysWithIndexWithRangeString    () { return withRange( withIndex( arraysString    ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<  Boolean[]>>> arraysWithIndexWithRangeObjBoolean() { return withRange( withIndex( arraysObjBoolean().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<     Byte[]>>> arraysWithIndexWithRangeObjByte   () { return withRange( withIndex( arraysObjByte   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<    Short[]>>> arraysWithIndexWithRangeObjShort  () { return withRange( withIndex( arraysObjShort  ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<  Integer[]>>> arraysWithIndexWithRangeObjInt    () { return withRange( withIndex( arraysObjInt    ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<     Long[]>>> arraysWithIndexWithRangeObjLong   () { return withRange( withIndex( arraysObjLong   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<Character[]>>> arraysWithIndexWithRangeObjChar   () { return withRange( withIndex( arraysObjChar   ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<    Float[]>>> arraysWithIndexWithRangeObjFloat  () { return withRange( withIndex( arraysObjFloat  ().ofMinSize(1) ) ); }
  @Provide default Arbitrary< WithRange< WithIndex<   Double[]>>> arraysWithIndexWithRangeObjDouble () { return withRange( withIndex( arraysObjDouble ().ofMinSize(1) ) ); }
}
