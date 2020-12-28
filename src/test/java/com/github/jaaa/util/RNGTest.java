package com.github.jaaa.util;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import static org.assertj.core.api.Assertions.assertThat;

public class RNGTest
{
  private static final int N_TRIES = 100_000;
  private static final double[] CHIQ = {
    10.828, //  1
    13.816, //  2
    16.266, //  3
    18.467, //  4
    20.515, //  5
    22.458, //  6
    24.322, //  7
    26.124, //  8
    27.877, //  9
    29.588, // 10
    31.264, // 11
    32.909, // 12
    34.528, // 13
    36.123, // 14
    37.697, // 15
    39.252  // 16
  };

  @Property( tries = 42 )
  void passesChiSquare(
    @ForAll long seed,
    @ForAll @IntRange(min=100_000, max=1_000_000) int nSamples,
    @ForAll @IntRange(min=-Integer.MAX_VALUE, max=Integer.MAX_VALUE - 17) int start,
    @ForAll @IntRange(min=2, max=17) int len
  )
  {
    RNG rng = new RNG(seed);

    int[] counts = new int[len];

    for( int i=nSamples; i-- > 0; )
    {
      int    nxt = rng.nextInt(start,start+len) - start;
      counts[nxt] += 1;
    }

    double E = nSamples*1.0 / len,
        chi2 = 0.0;

    for( int i=len; i-- > 0; )
    {
      double   d = counts[i] - E;
      chi2 += d*d / E;
    }

    assertThat(chi2).isLessThan( CHIQ[len-2] );
  }
}
