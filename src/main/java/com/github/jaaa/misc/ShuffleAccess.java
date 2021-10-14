package com.github.jaaa.misc;

import com.github.jaaa.SwapAccess;

import java.util.SplittableRandom;
import java.util.function.IntBinaryOperator;


public interface ShuffleAccess extends SwapAccess
{
  default IntBinaryOperator shuffle_newRNG() {
    return new SplittableRandom()::nextInt;
  }

  default void shuffle( int from, int until )
  {
    if( from < 0 || from > until ) throw new IllegalArgumentException();

    var rng = shuffle_newRNG();
    while( from < until-1 ) {
      int  i = rng.applyAsInt(from,until);
      swap(i,--until);
    }
  }
}
