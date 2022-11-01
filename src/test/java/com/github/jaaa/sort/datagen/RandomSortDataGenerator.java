package com.github.jaaa.sort.datagen;

import com.github.jaaa.misc.RandomShuffle;

import java.util.SplittableRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class RandomSortDataGenerator
{
  private final SplittableRandom rng;

  public RandomSortDataGenerator( SplittableRandom _rng ) {
    rng = requireNonNull(_rng);
  }
  public RandomSortDataGenerator() { this( new SplittableRandom() ); }

  private void nextUniform( int[] array, int from, int until, int step )
  {
    int mag = rng.nextInt(array.length*2+1) + 1,
        off = rng.nextInt(array.length*2+1) - array.length;
    for( int i=from; i < until; i+= step )
      array[i] = rng.nextInt(mag) + off;
  }

//  private void nextGaussian( int[] array, int from, int until, int step )
//  {
//    int mag = rng.nextInt(array.length*2+1),
//        off = rng.nextInt(array.length*2+1) - array.length;
//    for( int i=from; i < until; i+= step )
//      array[i] = (int) (rng.nextGaussian()*mag + off);
//  }

  private void nextAscending( int[] array, int from, int until, int step )
  {
    int slope = rng.nextInt(4) + 2,
          off = rng.nextInt(array.length*3+1) - array.length*2;

    if( from < until )
      array[from] = off;

    for( int i=from; (i+=step) < until; )
      array[i] = array[i-step] + rng.nextInt(slope);
  }

  private void nextDescending( int[] array, int from, int until, int step )
  {
    int slope = rng.nextInt(4) + 2,
          off = rng.nextInt(array.length*3+1) - array.length;

    if( from < until )
      array[from] = off;

    for( int i=from; (i+=step) < until; )
      array[i] = array[i-step] - rng.nextInt(slope);
  }

  private void nextMixed( int[] array, int from, int until, int step )
  {
    int len = (until-from) / step;

    if( 3 < len && rng.nextDouble() < 2/3d )
    {
      if( rng.nextDouble() < 0.5 ) {
        // INTERLEAVE
        int s = rng.nextInt(2)+2; // <- TODO: increase number of interleaves, maybe to sqrt(len) or something?
        nextMixed(array, from       ,until, step*s);
        nextMixed(array, from+step  ,until, step*s);
        nextMixed(array, from+step*2,until, step*s);
      }
      else {
        // SPLIT
        int  nSteps = rng.nextInt(2)+2;
        int[] steps = Stream.of(
          IntStream.of(0),
          rng.ints(1,len).limit(nSteps-1).sorted(),
          IntStream.of(len)
        ).flatMapToInt(x->x).toArray();

        for( int i=0; i++ < nSteps; ) {
          int lo = from + step*steps[i-1],
              hi = from + step*steps[i];
          nextMixed(array, lo,hi, step);
        }
      }
    }
    else switch (rng.nextInt(3)) {
      case 0 -> nextAscending(array, from, until, step);
      case 1 -> nextDescending(array, from, until, step);
      case 2 -> nextUniform(array, from, until, step);
//      case 3 : nextGaussian  (array, from,until,step); break;
      default -> throw new AssertionError();
    }
  }

  public int[] nextShuffled( int len )
  {
    int[] result = new int[len];
    for( int i=len; i-- > 0; )
      result[i] = i;
    RandomShuffle.shuffle(result, rng::nextInt);
    return result;
  }

  public int[] nextUniform( int len )
  {
    int[] result = new int[len];
    nextUniform(result, 0,result.length, 1);
    return result;
  }

//  public int[] nextGaussian( int len )
//  {
//    int[] result = new int[len];
//    nextGaussian(result, 0,result.length, 1);
//    return result;
//  }

  public int[] nextAscending( int len )
  {
    int[] result = new int[len];
    nextAscending(result, 0,result.length, 1);
    return result;
  }

  public int[] nextDescending( int len )
  {
    int[] result = new int[len];
    nextDescending(result, 0,result.length, 1);
    return result;
  }

  public int[] nextMixed( int len )
  {
    int[] result = new int[len];
    nextMixed(result, 0,result.length, 1);
    return result;
  }

  public void nextMixed( int[] array )
  {
    nextMixed(array, 0,array.length, 1);
  }
}
