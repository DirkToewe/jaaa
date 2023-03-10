package com.github.jaaa.permute;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;


public interface RandomShuffleAccess extends SwapAccess
{
  default IntBinaryOperator randomShuffle_newRNG() {
    return new SplittableRandom()::nextInt;
  }

  default void randomShuffle( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    IntBinaryOperator rng = randomShuffle_newRNG();
    while( from < until-1 ) {
      int  i = rng.applyAsInt(from,until);
      swap(i,--until);
    }
  }
}
