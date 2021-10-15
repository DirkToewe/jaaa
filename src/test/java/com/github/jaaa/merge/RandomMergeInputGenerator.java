package com.github.jaaa.merge;

import net.jqwik.api.Tuple;

import java.util.Arrays;
import java.util.SplittableRandom;

import static com.github.jaaa.misc.Shuffle.shuffle;
import static java.util.Objects.requireNonNull;
import static net.jqwik.api.Tuple.Tuple2;


public class RandomMergeInputGenerator
{
// STATIC FIELDS
// STATIC CONSTRUCTOR
// STATIC METHODS
// FIELDS
  private final SplittableRandom rng;

// CONSTRUCTORS
  public RandomMergeInputGenerator()                        { rng = new SplittableRandom(); }
  public RandomMergeInputGenerator( long seed )             { rng = new SplittableRandom(seed); }
  public RandomMergeInputGenerator( SplittableRandom _rng ) { rng = requireNonNull(_rng); }

// METHODS
  public Tuple2<int[],int[]> next( int lenA, int lenB )
  {
    int                   len = lenA + lenB;
    var isB = new boolean[len];
    Arrays.fill(isB,0,lenB, true);
    shuffle(isB, rng::nextInt);

    int val = 0;
    var wasB = false;

    int[] A = new int[lenA],
          B = new int[lenB];
    int a=0, b=0;

    for( int i=0; i < len; i++ )
      if( isB[i] ) {
        B[b++] = val += rng.nextInt(2);
        wasB = true;
      }
      else if( wasB ) {
        A[a++] = val += 1;
        wasB = false;
      }
      else
        A[a++] = val += rng.nextInt(2);

    assert a == lenA;
    assert b == lenB;

    return Tuple.of(A,B);
  }
}
