package com.github.jaaa.util;

import net.jqwik.api.Example;

import static com.github.jaaa.util.Hashing.hash;
import static com.github.jaaa.util.Hashing.unhash;
import static org.assertj.core.api.Assertions.assertThat;


public class HashingTest
{
  @Example
  void hashUnhash()
  {
    long nBad = 0;
    for( int x = Integer.MIN_VALUE;; x++ )
    {
      int y = hash(x);
      if( y==x ) nBad++;

      assertThat( unhash(y) ).isEqualTo(x);

      if( x == Integer.MAX_VALUE )
        break;
    }
    assertThat(nBad).isLessThanOrEqualTo(1);
  }
}
