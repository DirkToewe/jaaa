package com.github.jaaa.merge.datagen;

import com.github.jaaa.ArrayProviderTemplate;
import com.github.jaaa.fn.ObjInt3Fn;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.jaaa.Concat.concat;
import static com.github.jaaa.permute.Revert.revert;
import static net.jqwik.api.Arbitraries.bytes;
import static net.jqwik.api.Arbitraries.integers;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.RandomDistribution.uniform;


public interface MergeDataProviderTemplate extends ArrayProviderTemplate
{
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_byte_limitedRanges(
    @ForAll byte rangeL,
    @ForAll byte rangeR
  ) {
    return combine(
      bytes().between(Byte.MIN_VALUE, rangeL).array(byte[].class).ofMinSize(0).ofMaxSize( maxArraySize()/2 ).withSizeDistribution( uniform() ),
      bytes().between(Byte.MIN_VALUE, rangeR).array(byte[].class).ofMinSize(0).ofMaxSize( maxArraySize()/2 ).withSizeDistribution( uniform() )
    ).as( (l,r) ->
      MergeInput.of(concat(l,r), 0, l.length, l.length+r.length)
    );//.withoutEdgeCases(); // <- FIXME: should not necessary
  }

  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_exhaustive() { return Arbitraries.of( new MergeInputsUpToLength(12) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_len13     () { return Arbitraries.of( new MergeInputsOfLength(13) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_len14     () { return Arbitraries.of( new MergeInputsOfLength(14) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_len15     () { return Arbitraries.of( new MergeInputsOfLength(15) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_len16     () { return Arbitraries.of( new MergeInputsOfLength(16) ); }
  @Provide default Arbitrary<MergeInput<byte[]>> mergeInputs_uByte_len17     () { return Arbitraries.of( new MergeInputsOfLength(17) ); }

  @Provide default Arbitrary<MergeInput<boolean[]>> mergeInputs_boolean() { return mergeInputs_array(arraysBoolean(), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<   byte[]>> mergeInputs_byte   () { return mergeInputs_array(arraysByte   (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<  short[]>> mergeInputs_short  () { return mergeInputs_array(arraysShort  (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<   long[]>> mergeInputs_long   () { return mergeInputs_array(arraysLong   (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<   char[]>> mergeInputs_char   () { return mergeInputs_array(arraysChar   (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<  float[]>> mergeInputs_float  () { return mergeInputs_array(arraysFloat  (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput< double[]>> mergeInputs_double () { return mergeInputs_array(arraysDouble (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<    int[]>> mergeInputs_int_v1 () { return mergeInputs_array(arraysInt    (), MergeInput::of); }
  @Provide default Arbitrary<MergeInput<    int[]>> mergeInputs_int_v2 () {
    return Arbitraries.fromGenerator( rng -> {

      class ShrinkableMergeInput implements Shrinkable<MergeInput<int[]>>
      {
        private final int merged[], l,r;

        private ShrinkableMergeInput( int[] _merged, int _l, int _r )
        {
          if( _l < 0 || _l > _r || _r > _merged.length )
            throw new IllegalArgumentException();
          merged =_merged;
          l =_l;
          r =_r;
        }

        @Override public MergeInput<int[]> value()
        {
          int[] result = new int[r-l];

          int a=0, b=result.length;

          for( int i=l; i < r; i++ ) {
            int x = merged[i];
            if( x < 0 ) result[--b] = ~x;
            else        result[a++] =  x;
          }

          assert a==b;
          revert(result,a,result.length);

          if( l < r ) {
            int off = merged[l];
                off ^= off >> 31;
            for( int i=result.length; i-- > 0; )
              result[i] -= off;
          }

          return MergeInput.of(result, 0,a,result.length);
        }

        @Override public Stream<Shrinkable<MergeInput<int[]>>> shrink()
        {
          int LEN = r-l;
          Stream.Builder<Shrinkable<MergeInput<int[]>>> builder = Stream.builder();
          builder.add( new ShrinkableMergeInput(merged,0,0) );
          for( int d=1; d < LEN; d*=2 ) {
            int gap = LEN-d >>> 1;
            if( gap > 0 )
              builder.add( new ShrinkableMergeInput(merged, l+gap,l+gap+d) );
            builder.add( new ShrinkableMergeInput(merged, l,l+d) );
            builder.add( new ShrinkableMergeInput(merged, r-d,r) );
          }
          return builder.build();
        }

        @Override public ShrinkingDistance distance() {
          return ShrinkingDistance.of(r-l);
        }
      }

      int lenA = rng.nextInt( maxArraySize() / 2 ),
          lenB = rng.nextInt( maxArraySize() / 2 );

      Tuple2<int[],int[]> sample = new RandomMergeInputGenerator(rng).next(lenA,lenB);
      int[] a = sample.get1().clone(),
            b = sample.get2().clone(),
            c = new int[lenA+lenB];

      // tape merge
      for( int i=0, j=0, k=0; i < lenA || j < lenB; )
        c[k++] = j >= lenB || i < lenA && a[i] <= b[j] ? a[i++] : ~b[j++];

      ShrinkableMergeInput result = new ShrinkableMergeInput(c, 0,lenA+lenB);

      assert Arrays.equals( result.value().array(), concat(a,b) ) : Arrays.toString(result.value().array()) + " != " + Arrays.toString( concat(a,b) );

      return result;
    });
  }

  static <T> Arbitrary<MergeInput<T>> mergeInputs_array( Arbitrary<T> arrays, ObjInt3Fn<T,MergeInput<T>> newMergeInput )
  {
    return arrays.flatMap(
      array -> integers().between(0, Array.getLength(array)).withDistribution( uniform() ).array(int[].class).ofSize(3).map( lmr -> {
        assert lmr.length == 3;
        Arrays.sort(lmr);
        int l = lmr[0],
            m = lmr[1],
            r = lmr[2];
        return newMergeInput.apply(array, l,m,r);
      })
    );
  }
}
