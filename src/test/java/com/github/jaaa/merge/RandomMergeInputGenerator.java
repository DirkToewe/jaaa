package com.github.jaaa.merge;

import com.github.jaaa.Swap;
import net.jqwik.api.Tuple;

import java.util.BitSet;
import java.util.Random;

import static java.util.Objects.requireNonNull;
import static net.jqwik.api.Tuple.Tuple2;

public class RandomMergeInputGenerator
{
// FIELDS
  private final Random rng;

// CONSTRUCTORS
  public RandomMergeInputGenerator(             ) { rng = new Random(    ); }
  public RandomMergeInputGenerator(   long seed ) { rng = new Random(seed); }
  public RandomMergeInputGenerator( Random _rng ) { rng = requireNonNull(_rng); }

// METHODS
  private void randomize( double[] refA, double[] refB )
  {
    assert    refA.length == refB.length;
    int len = refA.length;
    if( len > 0 )
    {
      refA[0] =
      refB[0] = 1.0;
      switch( rng.nextInt(3) ) {
        default: throw new AssertionError();
        case 2 : refA[0] = 2.0; break;
        case 1 : refB[0] = 2.0; break;
        case 0 :                break;
      }

      for( int i=1; i < len; i++ )
      {
        double a = refA[i-1],
               b = refB[i-1];
        if( a==b )
          switch( rng.nextInt(6) )
          {
            default: throw new AssertionError();
            case 0:             break;
            case 1: a+=1;       break;
            case 2:       b+=1; break;
            case 3: a+=1; b+=1; break;
            case 4: a+=1; b+=2; break;
            case 5: a+=2; b+=1; break;
          }
        else
        {
          var swap = a > b;
          if( swap ) { Double c=a; a=b; b=c; }

          switch( rng.nextInt(8) )
          {
            default: throw new AssertionError();
            case 0:              break;
            case 1: a = (a+b)/2; break;
            case 2: a =    b   ; break;
            case 3: a =  1+b   ; break;
            case 4:          b = b+1; break;
            case 5: a = a+1; b = b+1; break;
            case 6: a = b+1; b = b+1; break;
            case 7: a = b+2; b = b+1; break;
          }

          if( swap ) { Double c=a; a=b; b=c; }
        }

        refA[i] = a;
        refB[i] = b;
      }
    }
  }

  private void subSample( double[] src, double[] dest )
  {
    assert   dest.length <= src.length;
    int[] indices = new int[src.length];
    for( int i=indices.length; i-- > 0; )
      indices[i] = i;

    boolean[] chosen = new boolean[src.length];

    // shuffle indices and draw
    for( int i=indices.length,
             j=   dest.length; j-- > 0; )
    {
      Swap.swap(indices, rng.nextInt(i), --i);
      chosen[indices[i]] = true;
    }

    int j=0;
    for( int i=0; i < src.length; i++ )
      if( chosen[i] )
        dest[j++] = src[i];

    if( j != dest.length )
      throw new AssertionError();
  }

  public Tuple2<double[],double[]> next( int lenA, int lenB ) {
    boolean swap = lenA < lenB;
        if( swap ) { var t=lenA; lenA=lenB; lenB=t; }

    double[] a = new double[lenA],
             b = new double[lenB],
             c = new double[lenA];
    randomize(a,c);
    subSample(c,b);

    if( swap ) { var t=a; a=b; b=t; }

    return Tuple.of(a,b);
  }
}
