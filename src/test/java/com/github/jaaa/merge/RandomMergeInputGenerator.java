package com.github.jaaa.merge;

import net.jqwik.api.Tuple;

import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;

import static com.github.jaaa.permute.RandomShuffle.randomShuffle;
import static java.lang.Math.addExact;
import static java.lang.Math.subtractExact;
import static net.jqwik.api.Tuple.Tuple2;


public class RandomMergeInputGenerator
{
// STATIC FIELDS
// STATIC CONSTRUCTOR
// STATIC METHODS
// FIELDS
  private final IntBinaryOperator rng;

// CONSTRUCTORS
  public RandomMergeInputGenerator()                        { this( new SplittableRandom() ); }
  public RandomMergeInputGenerator( long seed )             { this( new SplittableRandom(seed) ); }
  public RandomMergeInputGenerator( SplittableRandom _rng ) { rng = _rng::nextInt; }
  public RandomMergeInputGenerator(           Random _rng ) { rng = (lo,hi) -> _rng.nextInt( subtractExact(hi,lo) ) + lo; }

// METHODS
  public Tuple2<int[],int[]> next( int lenA, int lenB )
  {
    int                   len = addExact(lenA,lenB);
    var isB = new boolean[len];
    Arrays.fill(isB,0,lenB, true);
    randomShuffle(isB, rng::applyAsInt);

    int val = 0;

    int[] A = new int[lenA],
          B = new int[lenB];
    if( 0 < lenA || 0 < lenB )
    {
      int a=0, b=0;

      var wasB = isB[0];
      if( wasB ) b++;
      else       a++;

      for( int i=0; ++i < len; )
        if( isB[i] ) {
          B[b++] = val += rng.applyAsInt(0,2);
          wasB = true;
        }
        else if( wasB ) {
          A[a++] = ++val;
          wasB = false;
        }
        else
          A[a++] = val += rng.applyAsInt(0,2);

      assert a == lenA;
      assert b == lenB;
    }

    return Tuple.of(A,B);
  }
}
